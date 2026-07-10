package com.quan.apartment_building_management_system.controller.maintenance;

import com.quan.apartment_building_management_system.dto.maintenance.MaintenanceReportDTO;
import com.quan.apartment_building_management_system.entity.MaintenanceReport;
import com.quan.apartment_building_management_system.entity.MaintenanceReportImage;
import com.quan.apartment_building_management_system.entity.MaintenanceTask;
import com.quan.apartment_building_management_system.service.maintenance.MaintenanceReportService;
import com.quan.apartment_building_management_system.service.maintenance.MaintenanceTaskService;
import com.quan.apartment_building_management_system.service.utility.CloudinaryUploadService;
import com.quan.apartment_building_management_system.service.maintenance.MaintenanceReportImageService;
import org.springframework.web.multipart.MultipartFile;
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
    private final com.quan.apartment_building_management_system.service.user.AccountNotificationService accountNotificationService;
    private final com.quan.apartment_building_management_system.service.system.NotificationService notificationService;
    private final CloudinaryUploadService cloudinaryUploadService;
    private final MaintenanceReportImageService maintenanceReportImageService;

    public MaintenanceStaffController(MaintenanceTaskService maintenanceTaskService,
                                      MaintenanceReportService maintenanceReportService,
                                      com.quan.apartment_building_management_system.service.user.AccountNotificationService accountNotificationService,
                                      com.quan.apartment_building_management_system.service.system.NotificationService notificationService,
                                      CloudinaryUploadService cloudinaryUploadService,
                                      MaintenanceReportImageService maintenanceReportImageService) {
        this.maintenanceTaskService = maintenanceTaskService;
        this.maintenanceReportService = maintenanceReportService;
        this.accountNotificationService = accountNotificationService;
        this.notificationService = notificationService;
        this.cloudinaryUploadService = cloudinaryUploadService;
        this.maintenanceReportImageService = maintenanceReportImageService;
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
                
        // If there is an active task, redirect directly to its detail/reporting page
        if (!taskPage.isEmpty()) {
            MaintenanceTask activeTask = taskPage.getContent().get(0);
            return "redirect:/maintenance_staff/tasks/" + activeTask.getTaskId();
        }
                
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

        Byte lastProgress = reports.isEmpty() ? (byte) 0 : reports.get(reports.size() - 1).getProgressPercent();
        model.addAttribute("minProgress", lastProgress);

        if (!model.containsAttribute("reportDto")) {
            MaintenanceReportDTO dto = new MaintenanceReportDTO();
            dto.setTaskId(id);
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
                               @RequestParam(value = "reportImages", required = false) List<MultipartFile> reportImages,
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

        // Validate progress percent cannot be less than current progress
        List<MaintenanceReport> reports = maintenanceReportService.findByTaskId(id);
        Byte currentProgress = reports.isEmpty() ? (byte) 0 : reports.get(reports.size() - 1).getProgressPercent();
        if (reportDto.getProgressPercent() != null && reportDto.getProgressPercent() < currentProgress) {
            bindingResult.rejectValue("progressPercent", "error.progressPercent",
                    "Progress percent cannot be less than current progress (" + currentProgress + "%).");
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
        MaintenanceReport savedReport = maintenanceReportService.save(report);

        // Upload and save report images if present
        if (reportImages != null && !reportImages.isEmpty()) {
            for (MultipartFile file : reportImages) {
                if (file != null && !file.isEmpty()) {
                    try {
                        String url = cloudinaryUploadService.uploadImage(file, "abms/maintenance");
                        if (url != null) {
                            MaintenanceReportImage reportImage = new MaintenanceReportImage();
                            reportImage.setReport(savedReport);
                            reportImage.setImageUrl(url);
                            reportImage.setCaption(file.getOriginalFilename());
                            maintenanceReportImageService.save(reportImage);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

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

    // 5. Get Notifications for current logged-in staff
    @GetMapping("/api/notifications")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> getNotifications(HttpSession session) {
        Integer staffId = getLoggedInStaffId(session);
        
        // Scan and generate overdue task warnings dynamically
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        List<com.quan.apartment_building_management_system.entity.MaintenanceTask> tasks = maintenanceTaskService.findByStaffId(staffId);
        List<com.quan.apartment_building_management_system.entity.AccountNotification> existingNotifs = accountNotificationService.findByAccountId(staffId);

        for (com.quan.apartment_building_management_system.entity.MaintenanceTask task : tasks) {
            // Overdue check: status != 3 (not completed), deadline is not null and has passed
            if (task.getStatus() != 3 && task.getDeadline() != null && task.getDeadline().isBefore(now)) {
                boolean alreadyNotified = false;
                for (com.quan.apartment_building_management_system.entity.AccountNotification an : existingNotifs) {
                    if ("MaintenanceTask".equals(an.getNotification().getRelatedEntityType()) 
                            && an.getNotification().getContent().contains("'" + task.getMaintenanceRequest().getTitle() + "'")
                            && "Task Overdue Warning".equals(an.getNotification().getTitle())) {
                        alreadyNotified = true;
                        break;
                    }
                }

                if (!alreadyNotified) {
                    // Create Overdue Notification
                    com.quan.apartment_building_management_system.entity.Notification notification = new com.quan.apartment_building_management_system.entity.Notification();
                    notification.setTitle("Task Overdue Warning");
                    notification.setContent("Cảnh báo: Hạn chót hoàn thành công việc '" + task.getMaintenanceRequest().getTitle() + "' đã quá hạn!");
                    notification.setNotificationType((byte) 1); // System alert
                    notification.setCreatedBy(task.getAssignedBy());
                    notification.setCreatedAt(now);
                    notification.setRelatedEntityType("MaintenanceTask"); // Set to MaintenanceTask
                    notification.setReceiver(task.getStaff());
                    notificationService.save(notification);

                    // Create AccountNotification
                    com.quan.apartment_building_management_system.entity.AccountNotification accNotif = new com.quan.apartment_building_management_system.entity.AccountNotification();
                    accNotif.setNotification(notification);
                    accNotif.setAccount(task.getStaff());
                    accNotif.setIsRead(false);
                    accountNotificationService.save(accNotif);
                }
            }
        }

        // Fetch refreshed notifications list
        List<com.quan.apartment_building_management_system.entity.AccountNotification> list = accountNotificationService.findByAccountId(staffId);
        
        // Sort by notification.createdAt descending (newest first)
        list.sort((n1, n2) -> n2.getNotification().getCreatedAt().compareTo(n1.getNotification().getCreatedAt()));
        
        List<java.util.Map<String, Object>> response = new java.util.ArrayList<>();
        for (com.quan.apartment_building_management_system.entity.AccountNotification an : list) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", an.getId());
            map.put("title", an.getNotification().getTitle());
            map.put("content", an.getNotification().getContent());
            map.put("isRead", an.getIsRead());
            map.put("createdAt", an.getNotification().getCreatedAt().toString());
            map.put("relatedEntityType", an.getNotification().getRelatedEntityType());
            
            Integer taskId = null;
            if ("MaintenanceTask".equals(an.getNotification().getRelatedEntityType())) {
                for (com.quan.apartment_building_management_system.entity.MaintenanceTask task : tasks) {
                    if (an.getNotification().getContent().contains("'" + task.getMaintenanceRequest().getTitle() + "'")) {
                        taskId = task.getTaskId();
                        break;
                    }
                }
            }
            map.put("relatedEntityId", taskId);
            response.add(map);
        }
        return org.springframework.http.ResponseEntity.ok(response);
    }

    // 6. Mark a single notification as read
    @PostMapping("/api/notifications/{id}/read")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> markAsRead(@PathVariable("id") Long id, HttpSession session) {
        Integer staffId = getLoggedInStaffId(session);
        java.util.Optional<com.quan.apartment_building_management_system.entity.AccountNotification> opt = accountNotificationService.findById(id);
        if (opt.isPresent()) {
            com.quan.apartment_building_management_system.entity.AccountNotification an = opt.get();
            if (an.getAccount().getAccountId().equals(staffId)) {
                an.setIsRead(true);
                an.setReadAt(java.time.LocalDateTime.now());
                accountNotificationService.save(an);
                return org.springframework.http.ResponseEntity.ok().build();
            }
        }
        return org.springframework.http.ResponseEntity.badRequest().build();
    }

    // 7. Mark all notifications as read
    @PostMapping("/api/notifications/read-all")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> markAllAsRead(HttpSession session) {
        Integer staffId = getLoggedInStaffId(session);
        List<com.quan.apartment_building_management_system.entity.AccountNotification> list = accountNotificationService.findUnreadByAccountId(staffId);
        for (com.quan.apartment_building_management_system.entity.AccountNotification an : list) {
            an.setIsRead(true);
            an.setReadAt(java.time.LocalDateTime.now());
            accountNotificationService.save(an);
        }
        return org.springframework.http.ResponseEntity.ok().build();
    }

    // 8. View notifications list page (HTML)
    @GetMapping("/notifications")
    public String viewNotificationsList(HttpSession session, Model model) {
        Integer staffId = getLoggedInStaffId(session);
        
        // Scan and generate overdue task warnings dynamically
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        List<com.quan.apartment_building_management_system.entity.MaintenanceTask> tasks = maintenanceTaskService.findByStaffId(staffId);
        List<com.quan.apartment_building_management_system.entity.AccountNotification> existingNotifs = accountNotificationService.findByAccountId(staffId);

        for (com.quan.apartment_building_management_system.entity.MaintenanceTask task : tasks) {
            if (task.getStatus() != 3 && task.getDeadline() != null && task.getDeadline().isBefore(now)) {
                boolean alreadyNotified = false;
                for (com.quan.apartment_building_management_system.entity.AccountNotification an : existingNotifs) {
                    if ("MaintenanceTask".equals(an.getNotification().getRelatedEntityType()) 
                            && an.getNotification().getContent().contains("'" + task.getMaintenanceRequest().getTitle() + "'")
                            && "Task Overdue Warning".equals(an.getNotification().getTitle())) {
                        alreadyNotified = true;
                        break;
                    }
                }

                if (!alreadyNotified) {
                    com.quan.apartment_building_management_system.entity.Notification notification = new com.quan.apartment_building_management_system.entity.Notification();
                    notification.setTitle("Task Overdue Warning");
                    notification.setContent("Cảnh báo: Hạn chót hoàn thành công việc '" + task.getMaintenanceRequest().getTitle() + "' đã quá hạn!");
                    notification.setNotificationType((byte) 1);
                    notification.setCreatedBy(task.getAssignedBy());
                    notification.setCreatedAt(now);
                    notification.setRelatedEntityType("MaintenanceTask");
                    notification.setReceiver(task.getStaff());
                    notificationService.save(notification);

                    com.quan.apartment_building_management_system.entity.AccountNotification accNotif = new com.quan.apartment_building_management_system.entity.AccountNotification();
                    accNotif.setNotification(notification);
                    accNotif.setAccount(task.getStaff());
                    accNotif.setIsRead(false);
                    accountNotificationService.save(accNotif);
                }
            }
        }

        // Fetch refreshed list
        List<com.quan.apartment_building_management_system.entity.AccountNotification> list = accountNotificationService.findByAccountId(staffId);
        list.sort((n1, n2) -> n2.getNotification().getCreatedAt().compareTo(n1.getNotification().getCreatedAt()));

        model.addAttribute("notifications", list);
        model.addAttribute("pageTitle", "Thông báo");
        model.addAttribute("activeTab", "notifications");
        return "maintenance_staff/notifications";
    }

    // 9. Mark notification as read and redirect to the task details page
    @PostMapping("/notifications/{id}/read-and-view")
    public String readAndViewNotification(@PathVariable("id") Long id, HttpSession session) {
        Integer staffId = getLoggedInStaffId(session);
        java.util.Optional<com.quan.apartment_building_management_system.entity.AccountNotification> opt = accountNotificationService.findById(id);
        if (opt.isPresent()) {
            com.quan.apartment_building_management_system.entity.AccountNotification an = opt.get();
            if (an.getAccount().getAccountId().equals(staffId)) {
                an.setIsRead(true);
                an.setReadAt(java.time.LocalDateTime.now());
                accountNotificationService.save(an);
                
                if ("MaintenanceTask".equals(an.getNotification().getRelatedEntityType())) {
                    List<com.quan.apartment_building_management_system.entity.MaintenanceTask> tasks = maintenanceTaskService.findByStaffId(staffId);
                    for (com.quan.apartment_building_management_system.entity.MaintenanceTask task : tasks) {
                        if (an.getNotification().getContent().contains("'" + task.getMaintenanceRequest().getTitle() + "'")) {
                            return "redirect:/maintenance_staff/tasks/" + task.getTaskId();
                        }
                    }
                }
            }
        }
        return "redirect:/maintenance_staff/notifications";
    }

    // 10. Mark all notifications as read (HTML form submit redirect)
    @PostMapping("/notifications/read-all")
    public String readAllNotificationsHtml(HttpSession session) {
        Integer staffId = getLoggedInStaffId(session);
        List<com.quan.apartment_building_management_system.entity.AccountNotification> list = accountNotificationService.findUnreadByAccountId(staffId);
        for (com.quan.apartment_building_management_system.entity.AccountNotification an : list) {
            an.setIsRead(true);
            an.setReadAt(java.time.LocalDateTime.now());
            accountNotificationService.save(an);
        }
        return "redirect:/maintenance_staff/notifications";
    }
}
