package com.quan.apartment_building_management_system.controller.manager;

import com.quan.apartment_building_management_system.dto.MaintenanceRequestDTO;
import com.quan.apartment_building_management_system.dto.MaintenanceTaskDTO;
import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.Apartment;
import com.quan.apartment_building_management_system.entity.MaintenanceReport;
import com.quan.apartment_building_management_system.entity.MaintenanceRequest;
import com.quan.apartment_building_management_system.entity.MaintenanceTask;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.repository.AccountRepository;
import com.quan.apartment_building_management_system.repository.ApartmentRepository;
import com.quan.apartment_building_management_system.repository.MaintenanceReportRepository;
import com.quan.apartment_building_management_system.repository.MaintenanceRequestRepository;
import com.quan.apartment_building_management_system.repository.MaintenanceTaskRepository;
import com.quan.apartment_building_management_system.repository.ProfileRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager/maintenance")
public class ManagerMaintenanceController {

    private final MaintenanceRequestRepository requestRepo;
    private final MaintenanceTaskRepository taskRepo;
    private final MaintenanceReportRepository reportRepo;
    private final ApartmentRepository apartmentRepo;
    private final AccountRepository accountRepo;
    private final com.quan.apartment_building_management_system.repository.ProfileRepository profileRepo;

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ManagerMaintenanceController(MaintenanceRequestRepository requestRepo,
                                        MaintenanceTaskRepository taskRepo,
                                        MaintenanceReportRepository reportRepo,
                                        ApartmentRepository apartmentRepo,
                                        AccountRepository accountRepo,
                                        com.quan.apartment_building_management_system.repository.ProfileRepository profileRepo) {
        this.requestRepo = requestRepo;
        this.taskRepo = taskRepo;
        this.reportRepo = reportRepo;
        this.apartmentRepo = apartmentRepo;
        this.accountRepo = accountRepo;
        this.profileRepo = profileRepo;
    }

    // ─── Maintenance Requests ────────────────────────────────────────

    @GetMapping("/requests")
    public String listRequests(
            @RequestParam(value = "status", required = false) Byte status,
            @RequestParam(value = "apartmentId", required = false) Integer apartmentId,
            @RequestParam(value = "fromDate", required = false) String fromDateStr,
            @RequestParam(value = "toDate", required = false) String toDateStr,
            Model model) {

        LocalDateTime fromDate = parseDate(fromDateStr, LocalDate.now().minusMonths(1).atStartOfDay());
        LocalDateTime toDate = parseDate(toDateStr, LocalDateTime.now());

        List<MaintenanceRequest> requests = requestRepo.findFiltered(status, apartmentId, fromDate, toDate);
        List<MaintenanceRequestDTO> dtos = requests.stream().map(this::toRequestDTO).collect(Collectors.toList());

        model.addAttribute("requests", dtos);
        model.addAttribute("apartments", apartmentRepo.findAll());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedApartmentId", apartmentId);
        model.addAttribute("fromDate", fromDateStr);
        model.addAttribute("toDate", toDateStr);
        model.addAttribute("pageTitle", "Maintenance Requests");
        return "manager/maintenance/requests";
    }

    @PostMapping("/requests/create")
    @Transactional
    public String createRequest(
            @RequestParam("apartmentId") Integer apartmentId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image,
            RedirectAttributes redirect) {

        Apartment apt = apartmentRepo.findById(apartmentId).orElse(null);
        if (apt == null) {
            redirect.addFlashAttribute("message", "Apartment not found");
            redirect.addFlashAttribute("messageType", "error");
            return "redirect:/manager/maintenance/requests";
        }

        MaintenanceRequest req = new MaintenanceRequest();
        req.setApartment(apt);
        req.setTitle(title);
        req.setDescription(description);
        req.setStatus((byte) 0);
        req.setRequestDate(LocalDateTime.now());
        // Set default profile
        var profiles = profileRepo.findAll();
        if (!profiles.isEmpty()) {
            req.setProfile(profiles.get(0));
        }

        // Handle image upload
        if (image != null && !image.isEmpty()) {
            try {
                String uploadDir = "uploads/maintenance/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                String filename = "req_" + UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                Path filePath = uploadPath.resolve(filename);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                req.setImageUrl("/" + uploadDir + filename);
            } catch (IOException e) {
                // ignore
            }
        }

        requestRepo.save(req);
        redirect.addFlashAttribute("message", "Maintenance request created successfully");
        redirect.addFlashAttribute("messageType", "success");
        return "redirect:/manager/maintenance/requests";
    }

    @PostMapping("/requests/{id}/approve")
    @Transactional
    public String approveRequest(@PathVariable Integer id, RedirectAttributes redirect) {
        MaintenanceRequest req = requestRepo.findById(id).orElse(null);
        if (req == null) {
            redirect.addFlashAttribute("message", "Request not found");
            redirect.addFlashAttribute("messageType", "error");
            return "redirect:/manager/maintenance/requests";
        }
        req.setStatus((byte) 1); // InProgress

        MaintenanceTask task = new MaintenanceTask();
        task.setMaintenanceRequest(req);
        task.setAssignedDate(LocalDateTime.now());
        task.setStatus((byte) 1); // Pending assignment
        taskRepo.save(task);
        requestRepo.save(req);

        redirect.addFlashAttribute("message", "Request approved, task created");
        redirect.addFlashAttribute("messageType", "success");
        return "redirect:/manager/maintenance/requests";
    }

    @PostMapping("/requests/{id}/reject")
    @Transactional
    public String rejectRequest(@PathVariable Integer id, RedirectAttributes redirect) {
        MaintenanceRequest req = requestRepo.findById(id).orElse(null);
        if (req == null) {
            redirect.addFlashAttribute("message", "Request not found");
            redirect.addFlashAttribute("messageType", "error");
            return "redirect:/manager/maintenance/requests";
        }
        req.setStatus((byte) 3); // Rejected
        requestRepo.save(req);

        redirect.addFlashAttribute("message", "Request rejected");
        redirect.addFlashAttribute("messageType", "warning");
        return "redirect:/manager/maintenance/requests";
    }

    // ─── Maintenance Status ──────────────────────────────────────────

    @GetMapping("/status")
    public String listTasks(
            @RequestParam(value = "status", required = false) Byte status,
            @RequestParam(value = "staffId", required = false) Integer staffId,
            @RequestParam(value = "fromDate", required = false) String fromDateStr,
            @RequestParam(value = "toDate", required = false) String toDateStr,
            Model model) {

        LocalDateTime fromDate = parseDate(fromDateStr, LocalDate.now().minusMonths(1).atStartOfDay());
        LocalDateTime toDate = parseDate(toDateStr, LocalDateTime.now());

        List<MaintenanceTask> tasks = taskRepo.findFiltered(status, staffId, fromDate, toDate);
        List<MaintenanceTaskDTO> dtos = tasks.stream().map(this::toTaskDTO).collect(Collectors.toList());

        long total = tasks.size();
        long inProgress = tasks.stream().filter(t -> t.getStatus() == 1 || t.getStatus() == 2).count();
        long completed = tasks.stream().filter(t -> t.getStatus() == 3).count();
        long overdue = tasks.stream().filter(t -> t.getStatus() == 4 || (t.getDeadline() != null && t.getDeadline().isBefore(LocalDateTime.now()) && t.getStatus() != 3)).count();

        model.addAttribute("tasks", dtos);
        model.addAttribute("totalTasks", total);
        model.addAttribute("inProgressCount", inProgress);
        model.addAttribute("completedCount", completed);
        model.addAttribute("overdueCount", overdue);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedStaffId", staffId);
        model.addAttribute("fromDate", fromDateStr);
        model.addAttribute("toDate", toDateStr);
        model.addAttribute("pageTitle", "Maintenance Status");
        return "manager/maintenance/status";
    }

    @GetMapping("/tasks/{id}/reports")
    @ResponseBody
    public List<MaintenanceReport> getTaskReports(@PathVariable Integer id) {
        return reportRepo.findByMaintenanceTaskTaskId(id);
    }

    // ─── Helpers ─────────────────────────────────────────────────────

    private MaintenanceRequestDTO toRequestDTO(MaintenanceRequest r) {
        String residentName = "";
        if (r.getProfile() != null) {
            residentName = r.getProfile().getFullName();
        }
        String aptNum = r.getApartment() != null ? r.getApartment().getApartmentNumber() : "";
        return new MaintenanceRequestDTO(
            r.getRequestId(), residentName, aptNum,
            r.getTitle(), r.getDescription(), r.getImageUrl(),
            r.getRequestDate(), r.getStatus());
    }

    private MaintenanceTaskDTO toTaskDTO(MaintenanceTask t) {
        MaintenanceRequest req = t.getMaintenanceRequest();
        String title = req != null ? req.getTitle() : "";
        String desc = req != null ? req.getDescription() : "";
        String imgUrl = req != null ? req.getImageUrl() : "";
        LocalDateTime reqDate = req != null ? req.getRequestDate() : null;
        String aptNum = req != null && req.getApartment() != null ? req.getApartment().getApartmentNumber() : "";
        String residentName = req != null && req.getProfile() != null ? req.getProfile().getFullName() : "";
        String staffName = t.getStaff() != null ? t.getStaff().getUsername() : "";
        String assignedBy = t.getAssignedBy() != null ? t.getAssignedBy().getUsername() : "";

        Byte progress = 0;
        List<MaintenanceReport> reports = reportRepo.findByMaintenanceTaskTaskId(t.getTaskId());
        if (!reports.isEmpty()) {
            progress = reports.get(reports.size() - 1).getProgressPercent();
        }

        Integer reqId = req != null ? req.getRequestId() : null;
        MaintenanceTaskDTO dto = new MaintenanceTaskDTO(
            t.getTaskId(), reqId, title, desc, imgUrl,
            reqDate, aptNum, residentName, staffName, assignedBy,
            t.getAssignedDate(), t.getDeadline(), t.getStatus(), progress);
        dto.setReports(reports);
        return dto;
    }

    private LocalDateTime parseDate(String str, LocalDateTime fallback) {
        if (str == null || str.isBlank()) return fallback;
        try {
            return LocalDate.parse(str.trim()).atStartOfDay();
        } catch (Exception e) {
            return fallback;
        }
    }
}
