package com.quan.apartment_building_management_system.controller.manager;

import com.quan.apartment_building_management_system.dto.MaintenanceTaskAssignDTO;
import com.quan.apartment_building_management_system.dto.StaffWorkStatusDTO;
import com.quan.apartment_building_management_system.entity.MaintenanceRequest;
import com.quan.apartment_building_management_system.entity.MaintenanceTask;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.service.maintenance.MaintenanceRequestService;
import com.quan.apartment_building_management_system.service.maintenance.MaintenanceTaskService;
import com.quan.apartment_building_management_system.service.user.ProfileService;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager/maintenance")
public class ManagerMaintenanceController {

    private final MaintenanceRequestService maintenanceRequestService;
    private final MaintenanceTaskService maintenanceTaskService;
    private final ProfileService profileService;

    public ManagerMaintenanceController(MaintenanceRequestService maintenanceRequestService,
                                        MaintenanceTaskService maintenanceTaskService,
                                        ProfileService profileService) {
        this.maintenanceRequestService = maintenanceRequestService;
        this.maintenanceTaskService = maintenanceTaskService;
        this.profileService = profileService;
    }

    @GetMapping
    public String listRequests(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Byte status,
            @RequestParam(value = "fromDate", required = false) String fromDateStr,
            @RequestParam(value = "toDate", required = false) String toDateStr,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        String searchKeyword = (keyword == null) ? "" : keyword.trim();
        
        LocalDateTime fromDate = null;
        LocalDateTime toDate = null;
        try {
            if (fromDateStr != null && !fromDateStr.trim().isEmpty()) {
                fromDate = LocalDate.parse(fromDateStr).atStartOfDay();
            }
            if (toDateStr != null && !toDateStr.trim().isEmpty()) {
                toDate = LocalDate.parse(toDateStr).atTime(LocalTime.MAX);
            }
        } catch (Exception e) {
            // ignore parse errors, they will just be null
        }

        int pageSize = 7;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("requestDate").descending());
        Page<MaintenanceRequest> requestPage = maintenanceRequestService.searchRequests(searchKeyword, status, fromDate, toDate, pageable);
        List<Profile> activeStaff = profileService.findActiveMaintenanceStaffs();

        // Calculate counts based on all requests
        List<MaintenanceRequest> allRequests = maintenanceRequestService.findAll();
        long totalRequests = allRequests.size();
        long pendingRequests = allRequests.stream().filter(r -> r.getStatus() == 0).count();
        long activeRequests = allRequests.stream().filter(r -> r.getStatus() == 1).count();
        long completedRequests = allRequests.stream().filter(r -> r.getStatus() == 2).count();

        model.addAttribute("requests", requestPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", requestPage.getTotalPages());
        model.addAttribute("totalItems", requestPage.getTotalElements());

        model.addAttribute("totalRequests", totalRequests);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("activeRequests", activeRequests);
        model.addAttribute("completedRequests", completedRequests);
        model.addAttribute("staffs", activeStaff);
        model.addAttribute("keyword", searchKeyword);
        model.addAttribute("statusFilter", status);
        model.addAttribute("fromDate", fromDateStr);
        model.addAttribute("toDate", toDateStr);
        model.addAttribute("activeTab", "maintenance");
        model.addAttribute("pageTitle", "Maintenance Requests");

        return "manager/maintenance/list";
    }

    @PostMapping("/assign")
    public String assignStaff(
            @Valid @ModelAttribute MaintenanceTaskAssignDTO assignDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            redirectAttributes.addFlashAttribute("message", "Validation error: " + errorMessage);
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/manager/maintenance";
        }

        try {
            LocalDateTime deadline = null;
            if (assignDTO.getDeadline() != null && !assignDTO.getDeadline().isBlank()) {
                deadline = LocalDateTime.parse(assignDTO.getDeadline(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            // Fetch any manager to act as the assigner (since no Security context is configured yet)
            Optional<Profile> managerOpt = profileService.findAll().stream()
                    .filter(p -> p.getAccount() != null && "MANAGER".equalsIgnoreCase(p.getAccount().getRole().getRoleName()))
                    .findFirst();
            Integer managerAccountId = managerOpt.map(p -> p.getAccount().getAccountId()).orElse(1); // fallback to ID 1

            maintenanceTaskService.assignTask(assignDTO.getRequestId(), assignDTO.getStaffId(), deadline, managerAccountId);
            redirectAttributes.addFlashAttribute("message", "Task assigned successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }

        return "redirect:/manager/maintenance";
    }

    @GetMapping("/{requestId}/details")
    @ResponseBody
    public ResponseEntity<?> getRequestDetails(@PathVariable("requestId") Integer requestId) {
        Optional<MaintenanceRequest> requestOpt = maintenanceRequestService.findById(requestId);
        if (requestOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        MaintenanceRequest request = requestOpt.get();

        Map<String, Object> response = new HashMap<>();
        response.put("requestId", request.getRequestId());
        response.put("title", request.getTitle());
        response.put("description", request.getDescription());
        response.put("status", request.getStatus());
        response.put("requestDate", request.getRequestDate() != null ? request.getRequestDate().toString() : "N/A");
        response.put("residentName", request.getProfile() != null ? request.getProfile().getFullName() : "N/A");
        response.put("apartmentNumber", request.getApartment() != null ? request.getApartment().getApartmentNumber() : "N/A");
        response.put("imageUrl", request.getImageUrl());

        Optional<MaintenanceTask> taskOpt = maintenanceTaskService.findByRequestId(requestId);
        if (taskOpt.isPresent()) {
            MaintenanceTask task = taskOpt.get();
            Map<String, Object> taskMap = new HashMap<>();
            taskMap.put("taskId", task.getTaskId());
            taskMap.put("staffName", task.getStaff() != null && task.getStaff().getProfile() != null ? task.getStaff().getProfile().getFullName() : "N/A");
            taskMap.put("assignedDate", task.getAssignedDate() != null ? task.getAssignedDate().toString() : "N/A");
            taskMap.put("deadline", task.getDeadline() != null ? task.getDeadline().toString() : "N/A");
            taskMap.put("status", task.getStatus());
            
            String workStatus = "available";
            if (task.getStaff() != null) {
                workStatus = maintenanceTaskService.getStaffWorkStatus(task.getStaff().getAccountId());
            }
            taskMap.put("workStatus", workStatus);

            List<Map<String, Object>> reportsList = new ArrayList<>();
            if (task.getMaintenanceReports() != null) {
                for (com.quan.apartment_building_management_system.entity.MaintenanceReport report : task.getMaintenanceReports()) {
                    Map<String, Object> reportMap = new HashMap<>();
                    reportMap.put("reportId", report.getReportId());
                    reportMap.put("reportContent", report.getReportContent());
                    reportMap.put("progressPercent", report.getProgressPercent());
                    reportMap.put("createdAt", report.getCreatedAt() != null ? report.getCreatedAt().toString() : "N/A");
                    reportsList.add(reportMap);
                }
            }
            taskMap.put("reports", reportsList);
            response.put("task", taskMap);
        } else {
            response.put("task", null);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{requestId}/assign")
    public String showAssignPage(@PathVariable("requestId") Integer requestId, Model model) {
        Optional<MaintenanceRequest> requestOpt = maintenanceRequestService.findById(requestId);
        if (requestOpt.isEmpty()) {
            return "redirect:/manager/maintenance";
        }
        MaintenanceRequest request = requestOpt.get();

        List<StaffWorkStatusDTO> staffs = maintenanceTaskService.getActiveMaintenanceStaffWithWorkStatus();

        model.addAttribute("request", request);
        model.addAttribute("staffs", staffs);
        model.addAttribute("activeTab", "maintenance");
        model.addAttribute("pageTitle", "Assign Staff");

        return "manager/maintenance/assign";
    }

    @PostMapping("/{requestId}/assign/{staffId}")
    public String assignStaffToRequest(
            @PathVariable("requestId") Integer requestId,
            @PathVariable("staffId") Integer staffId,
            RedirectAttributes redirectAttributes) {
        try {
            // Fetch any manager to act as the assigner (since no Security context is configured yet)
            Optional<Profile> managerOpt = profileService.findAll().stream()
                    .filter(p -> p.getAccount() != null && "MANAGER".equalsIgnoreCase(p.getAccount().getRole().getRoleName()))
                    .findFirst();
            Integer managerAccountId = managerOpt.map(p -> p.getAccount().getAccountId()).orElse(1); // fallback to ID 1

            maintenanceTaskService.assignTask(requestId, staffId, null, managerAccountId);
            redirectAttributes.addFlashAttribute("message", "Task assigned successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/manager/maintenance";
    }
}
