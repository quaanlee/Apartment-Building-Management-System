package com.quan.apartment_building_management_system.controller.manager;

import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.Notification;
import com.quan.apartment_building_management_system.repository.AccountRepository;
import com.quan.apartment_building_management_system.service.system.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/manager/notifications")
public class ManagerNotificationController {

    private final NotificationService notificationService;
    private final AccountRepository accountRepository;

    public ManagerNotificationController(NotificationService notificationService, AccountRepository accountRepository) {
        this.notificationService = notificationService;
        this.accountRepository = accountRepository;
    }

    private java.time.LocalDate parseLocalDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        // Try yyyy-MM-dd
        try {
            return java.time.LocalDate.parse(dateStr.trim());
        } catch (Exception e) {
            // Ignore
        }
        // Try MM/dd/yyyy
        try {
            return java.time.LocalDate.parse(dateStr.trim(), java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (Exception e) {
            // Ignore
        }
        // Try dd/MM/yyyy
        try {
            return java.time.LocalDate.parse(dateStr.trim(), java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }

    @GetMapping
    public String viewNotificationsDashboard(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            Model model) {
        
        System.out.println("--- viewNotificationsDashboard ---");
        System.out.println("Raw fromDate: " + fromDate);
        System.out.println("Raw toDate: " + toDate);

        List<Notification> allNotifications = notificationService.findAll();
        
        java.time.LocalDate from = parseLocalDate(fromDate);
        java.time.LocalDate to = parseLocalDate(toDate);
        
        System.out.println("Parsed from: " + from);
        System.out.println("Parsed to: " + to);

        if (from != null && to != null && to.isBefore(from)) {
            model.addAttribute("message", "To Date must not be earlier than From Date.");
            model.addAttribute("messageType", "error");
        } else {
            final java.time.LocalDate finalFrom = from;
            final java.time.LocalDate finalTo = to;
            
            if (finalFrom != null || finalTo != null) {
                allNotifications = allNotifications.stream().filter(n -> {
                    java.time.LocalDate createdAtDate = n.getCreatedAt().toLocalDate();
                    if (finalFrom != null && createdAtDate.isBefore(finalFrom)) {
                        return false;
                    }
                    if (finalTo != null && createdAtDate.isAfter(finalTo)) {
                        return false;
                    }
                    return true;
                }).collect(java.util.stream.Collectors.toList());
            }
        }

        // Sort notifications: newest first
        allNotifications.sort((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()));

        // Calculate statistics directly from the database size
        long totalSent = allNotifications.size();

        model.addAttribute("notifications", allNotifications);
        model.addAttribute("totalSent", totalSent);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        return "manager/notification/list";
    }

    @GetMapping("/{id}")
    public String viewNotificationDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Notification> notificationOpt = notificationService.findById(id);
        if (notificationOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy thông báo.");
            redirectAttributes.addFlashAttribute("messageType", "warning");
            return "redirect:/manager/notifications";
        }
        model.addAttribute("notification", notificationOpt.get());
        return "manager/notification/detail";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Notification> notificationOpt = notificationService.findById(id);
        if (notificationOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy thông báo.");
            redirectAttributes.addFlashAttribute("messageType", "warning");
            return "redirect:/manager/notifications";
        }
        model.addAttribute("notification", notificationOpt.get());
        return "manager/notification/edit";
    }

    @PostMapping("/{id}/edit")
    public String editNotification(@PathVariable Long id, 
                                   @RequestParam String title, 
                                   @RequestParam String content, 
                                   @RequestParam Byte notificationType, 
                                   @RequestParam String relatedEntityType, 
                                   RedirectAttributes redirectAttributes) {
        Optional<Notification> notificationOpt = notificationService.findById(id);
        if (notificationOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy thông báo.");
            redirectAttributes.addFlashAttribute("messageType", "warning");
            return "redirect:/manager/notifications";
        }
        Notification notification = notificationOpt.get();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setNotificationType(notificationType);
        notification.setRelatedEntityType(relatedEntityType);
        notificationService.save(notification);

        redirectAttributes.addFlashAttribute("message", "Cập nhật thông báo thành công.");
        return "redirect:/manager/notifications/" + id + "/edit";
    }

    @PostMapping("/{id}/delete")
    public String deleteNotification(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Notification> notificationOpt = notificationService.findById(id);
        if (notificationOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy thông báo.");
            redirectAttributes.addFlashAttribute("messageType", "warning");
            return "redirect:/manager/notifications";
        }
        notificationService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Xóa thông báo thành công.");
        return "redirect:/manager/notifications";
    }

    @GetMapping("/new")
    public String showCreateForm(HttpSession session) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null) {
            currentUser = accountRepository.findByUsername("jane.doe").orElse(null);
            if (currentUser == null) {
                List<Account> accounts = accountRepository.findAll();
                if (accounts.isEmpty()) {
                    return "redirect:/login";
                }
            }
        }
        return "manager/notification/new";
    }

    @PostMapping("/new")
    public String createNotification(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Byte notificationType,
            @RequestParam String relatedEntityType,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null) {
            currentUser = accountRepository.findByUsername("jane.doe").orElse(null);
            if (currentUser == null) {
                List<Account> accounts = accountRepository.findAll();
                if (!accounts.isEmpty()) {
                    currentUser = accounts.get(0);
                } else {
                    return "redirect:/login";
                }
            }
        }

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setNotificationType(notificationType);
        notification.setRelatedEntityType(relatedEntityType);
        notification.setCreatedBy(currentUser);
        notification.setCreatedAt(java.time.LocalDateTime.now());

        notificationService.save(notification);

        redirectAttributes.addFlashAttribute("message", "Tạo và gửi thông báo thành công.");
        return "redirect:/manager/notifications";
    }
}
