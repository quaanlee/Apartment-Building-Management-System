package com.quan.apartment_building_management_system.controller.maintenance;

import com.quan.apartment_building_management_system.dto.maintenance.MaintenanceReportDTO;
import com.quan.apartment_building_management_system.entity.MaintenanceReport;
import com.quan.apartment_building_management_system.entity.MaintenanceTask;
import com.quan.apartment_building_management_system.service.maintenance.MaintenanceReportService;
import com.quan.apartment_building_management_system.service.maintenance.MaintenanceTaskService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/maintenance_staff")
public class MaintenanceStaffController {

    private final MaintenanceTaskService maintenanceTaskService;
    private final MaintenanceReportService maintenanceReportService;

    public MaintenanceStaffController(MaintenanceTaskService maintenanceTaskService,
                                      MaintenanceReportService maintenanceReportService) {
        this.maintenanceTaskService = maintenanceTaskService;
        this.maintenanceReportService = maintenanceReportService;
    }

    // Utility to simulate logged-in maintenance staff. 
    // Defaults to 3 if no user is in session (assuming ID 3 is a staff member).
    private Integer getLoggedInStaffId(HttpSession session) {
        com.quan.apartment_building_management_system.entity.Account account = (com.quan.apartment_building_management_system.entity.Account) session.getAttribute("currentUser");
        return account != null ? account.getAccountId() : 3;
    }

    // Default route to redirect to dashboard page
    @GetMapping({"", "/"})
    public String index() {
        return "redirect:/maintenance_staff/dashboard";
    }

    // View Dashboard with summary metrics and activities
    @GetMapping("/dashboard")
    public String viewDashboard(HttpSession session, Model model) {
        Integer staffId = getLoggedInStaffId(session);

        List<MaintenanceTask> allTasks = maintenanceTaskService.findByStaffId(staffId);

        long totalTasks = allTasks.size();
        long pendingTasks = allTasks.stream().filter(t -> t.getStatus() == 1).count();
        long activeTasks = allTasks.stream().filter(t -> t.getStatus() == 2).count();
        long completedTasks = allTasks.stream().filter(t -> t.getStatus() == 3).count();

        LocalDateTime now = LocalDateTime.now();
        long overdueTasks = allTasks.stream()
                .filter(t -> t.getStatus() != 3 && t.getDeadline() != null && t.getDeadline().isBefore(now))
                .count();

        int completionRate = totalTasks > 0 ? (int) Math.round((double) completedTasks / totalTasks * 100) : 0;

        List<MaintenanceTask> recentTasks = allTasks.stream()
                .sorted((t1, t2) -> t2.getAssignedDate().compareTo(t1.getAssignedDate()))
                .limit(5)
                .collect(Collectors.toList());

        List<MaintenanceReport> recentReports = allTasks.stream()
                .flatMap(t -> t.getMaintenanceReports().stream())
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("totalTasks", totalTasks);
        model.addAttribute("pendingTasks", pendingTasks);
        model.addAttribute("activeTasks", activeTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("overdueTasks", overdueTasks);
        model.addAttribute("completionRate", completionRate);
        model.addAttribute("recentTasks", recentTasks);
        model.addAttribute("recentReports", recentReports);

        model.addAttribute("pageTitle", "Maintenance Dashboard");
        return "maintenance_staff/dashboard";
    }

    // 1. View Assigned Tasks (Status 1: Assigned, 2: In Progress) with Paging
    @GetMapping("/tasks")
    public String viewAssignedTasks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session, Model model) {
        Integer staffId = getLoggedInStaffId(session);
        int pageSize = 4; // 4 tasks per page
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("assignedDate").descending());
        
        Page<MaintenanceTask> taskPage = maintenanceTaskService.findByStaffIdAndStatusIn(
                staffId, Arrays.asList((byte) 1, (byte) 2), pageable);
                
        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", taskPage.getTotalPages());
        model.addAttribute("totalItems", taskPage.getTotalElements());
        model.addAttribute("pageTitle", "Assigned Tasks");
        return "maintenance_staff/tasks";
    }

    // 2. View History of Tasks Handled (Status 3: Completed) with Paging
    @GetMapping("/history")
    public String viewTaskHistory(
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session, Model model) {
        Integer staffId = getLoggedInStaffId(session);
        int pageSize = 4; // 4 tasks per page
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("assignedDate").descending());
        
        Page<MaintenanceTask> taskPage = maintenanceTaskService.findByStaffIdAndStatus(
                staffId, (byte) 3, pageable);
                
        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", taskPage.getTotalPages());
        model.addAttribute("totalItems", taskPage.getTotalElements());
        model.addAttribute("pageTitle", "Task History");
        return "maintenance_staff/history";
    }

    // 3. View Task Detail & Submit Report Form
    @GetMapping("/tasks/{id}")
    public String viewTaskDetail(@PathVariable("id") Integer id, HttpSession session, Model model) {
        Integer staffId = getLoggedInStaffId(session);
        Optional<MaintenanceTask> taskOpt = maintenanceTaskService.findById(id);

        if (taskOpt.isEmpty() || !taskOpt.get().getStaff().getAccountId().equals(staffId)) {
            return "redirect:/maintenance_staff/tasks?error=Task not found or unauthorized";
        }

        MaintenanceTask task = taskOpt.get();
        List<MaintenanceReport> reports = maintenanceReportService.findByTaskId(id);

        model.addAttribute("task", task);
        model.addAttribute("reports", reports);

        if (!model.containsAttribute("reportDto")) {
            MaintenanceReportDTO dto = new MaintenanceReportDTO();
            dto.setTaskId(id);
            // Default progress to last report's progress or 0
            Byte lastProgress = reports.isEmpty() ? (byte) 0 : reports.get(reports.size() - 1).getProgressPercent();
            dto.setProgressPercent(lastProgress);
            model.addAttribute("reportDto", dto);
        }

        model.addAttribute("pageTitle", "Task Detail");
        return "maintenance_staff/task_detail";
    }

    // 4. Submit Detailed Repair Report & Update Progress
    @PostMapping("/tasks/{id}/report")
    public String submitReport(@PathVariable("id") Integer id,
                               @Valid @ModelAttribute("reportDto") MaintenanceReportDTO reportDto,
                               BindingResult bindingResult,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        
        Integer staffId = getLoggedInStaffId(session);
        Optional<MaintenanceTask> taskOpt = maintenanceTaskService.findById(id);

        if (taskOpt.isEmpty() || !taskOpt.get().getStaff().getAccountId().equals(staffId)) {
            redirectAttributes.addFlashAttribute("message", "Task not found or unauthorized");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/maintenance_staff/tasks";
        }

        MaintenanceTask task = taskOpt.get();

        if (task.getStatus() == 3) {
            redirectAttributes.addFlashAttribute("message", "Task is already completed.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/maintenance_staff/tasks/" + id;
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.reportDto", bindingResult);
            redirectAttributes.addFlashAttribute("reportDto", reportDto);
            return "redirect:/maintenance_staff/tasks/" + id;
        }

        // Create and save the report
        MaintenanceReport report = new MaintenanceReport();
        report.setMaintenanceTask(task);
        report.setReportContent(reportDto.getReportContent());
        report.setProgressPercent(reportDto.getProgressPercent());
        report.setCreatedAt(LocalDateTime.now());
        maintenanceReportService.save(report);

        // Update task status based on progress
        if (reportDto.getProgressPercent() == 100) {
            task.setStatus((byte) 3); // Completed
        } else if (task.getStatus() == 1 && reportDto.getProgressPercent() > 0) {
            task.setStatus((byte) 2); // In Progress
        }
        
        maintenanceTaskService.save(task);

        redirectAttributes.addFlashAttribute("message", "Report submitted successfully!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/maintenance_staff/tasks/" + id;
    }
}
