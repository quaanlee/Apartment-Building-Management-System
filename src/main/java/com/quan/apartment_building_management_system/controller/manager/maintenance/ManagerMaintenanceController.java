package com.quan.apartment_building_management_system.controller.manager.maintenance;

import com.quan.apartment_building_management_system.dto.maintenance.MaintenanceTaskAssignDTO;
import com.quan.apartment_building_management_system.dto.maintenance.StaffWorkStatusDTO;
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
    private final com.quan.apartment_building_management_system.service.user.AccountService accountService;

    public ManagerMaintenanceController(MaintenanceRequestService maintenanceRequestService,
                                        MaintenanceTaskService maintenanceTaskService,
                                        ProfileService profileService,
                                        com.quan.apartment_building_management_system.service.user.AccountService accountService) {
        this.maintenanceRequestService = maintenanceRequestService;
        this.maintenanceTaskService = maintenanceTaskService;
        this.profileService = profileService;
        this.accountService = accountService;
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
        List<com.quan.apartment_building_management_system.dto.maintenance.StaffWorkStatusDTO> activeStaff = maintenanceTaskService.getActiveMaintenanceStaffWithWorkStatus();

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
            jakarta.servlet.http.HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            redirectAttributes.addFlashAttribute("message", "Lỗi xác thực: " + errorMessage);
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/manager/maintenance";
        }

        try {
            LocalDateTime deadline = null;
            if (assignDTO.getDeadline() != null && !assignDTO.getDeadline().isBlank()) {
                deadline = LocalDateTime.parse(assignDTO.getDeadline(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            // Fetch Manager account from session, fallback to database
            com.quan.apartment_building_management_system.entity.Account manager = null;
            if (session != null) {
                manager = (com.quan.apartment_building_management_system.entity.Account) session.getAttribute("currentUser");
            }
            if (manager == null) {
                manager = accountService.findByUsername("manager@gmail.com")
                    .or(() -> accountService.findByUsername("admin@gmail.com"))
                    .orElseGet(() -> {
                        Optional<Profile> managerOpt = profileService.findAll().stream()
                                .filter(p -> p.getAccount() != null && "MANAGER".equalsIgnoreCase(p.getAccount().getRole().getRoleName()))
                                .findFirst();
                        return managerOpt.map(Profile::getAccount).orElse(null);
                    });
            }
            if (manager == null) {
                throw new IllegalArgumentException("No default manager/admin account found.");
            }

            maintenanceTaskService.assignTask(assignDTO.getRequestId(), assignDTO.getStaffId(), deadline, manager);
            redirectAttributes.addFlashAttribute("message", "Phân công công việc thành công!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Lỗi: " + e.getMessage());
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
//        response.put("imageUrl", request.getImageUrl()); // removed - use MaintenanceRequestImage instead

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
                    
                    List<String> imageUrls = new ArrayList<>();
                    if (report.getImages() != null) {
                        for (com.quan.apartment_building_management_system.entity.MaintenanceReportImage img : report.getImages()) {
                            imageUrls.add(img.getImageUrl());
                        }
                    }
                    reportMap.put("images", imageUrls);
                    
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
            jakarta.servlet.http.HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            // Fetch Manager account from session, fallback to database
            com.quan.apartment_building_management_system.entity.Account manager = null;
            if (session != null) {
                manager = (com.quan.apartment_building_management_system.entity.Account) session.getAttribute("currentUser");
            }
            if (manager == null) {
                manager = accountService.findByUsername("manager@gmail.com")
                    .or(() -> accountService.findByUsername("admin@gmail.com"))
                    .orElseGet(() -> {
                        Optional<Profile> managerOpt = profileService.findAll().stream()
                                .filter(p -> p.getAccount() != null && "MANAGER".equalsIgnoreCase(p.getAccount().getRole().getRoleName()))
                                .findFirst();
                        return managerOpt.map(Profile::getAccount).orElse(null);
                    });
            }
            if (manager == null) {
                throw new IllegalArgumentException("No default manager/admin account found.");
            }

            maintenanceTaskService.assignTask(requestId, staffId, null, manager);
            redirectAttributes.addFlashAttribute("message", "Phân công công việc thành công!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/manager/maintenance";
    }

    @GetMapping("/{requestId}/edit")
    public String showEditForm(@PathVariable("requestId") Integer requestId, Model model) {
        Optional<MaintenanceRequest> requestOpt = maintenanceRequestService.findById(requestId);
        if (requestOpt.isEmpty()) {
            return "redirect:/manager/maintenance";
        }
        MaintenanceRequest request = requestOpt.get();
        if (request.getStatus() != 0) {
            return "redirect:/manager/maintenance";
        }
        model.addAttribute("request", request);
        model.addAttribute("pageTitle", "Edit Request");
        return "manager/maintenance/edit";
    }

    @PostMapping("/{requestId}/edit")
    public String updateRequest(@PathVariable("requestId") Integer requestId,
                                 @RequestParam("title") String title,
                                 @RequestParam("description") String description,
                                 RedirectAttributes redirectAttributes) {
        Optional<MaintenanceRequest> requestOpt = maintenanceRequestService.findById(requestId);
        if (requestOpt.isEmpty()) {
            return "redirect:/manager/maintenance";
        }
        MaintenanceRequest request = requestOpt.get();
        if (request.getStatus() != 0) {
            redirectAttributes.addFlashAttribute("message", "Không thể chỉnh sửa: yêu cầu không ở trạng thái Chờ xử lý");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/manager/maintenance";
        }
        request.setTitle(title);
        request.setDescription(description);
        maintenanceRequestService.save(request);
        redirectAttributes.addFlashAttribute("message", "Cập nhật yêu cầu thành công");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/manager/maintenance";
    }

    @PostMapping("/{requestId}/unassign")
    public String unassignTask(@PathVariable("requestId") Integer requestId, RedirectAttributes redirectAttributes) {
        try {
            maintenanceTaskService.unassignTask(requestId);
            redirectAttributes.addFlashAttribute("message", "Hủy phân công công việc thành công!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Lỗi khi hủy phân công: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/manager/maintenance";
    }
}

