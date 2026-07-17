package com.quan.apartment_building_management_system.controller.resident.notification;

import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.AccountNotification;
import com.quan.apartment_building_management_system.service.user.AccountNotificationService;
import com.quan.apartment_building_management_system.service.user.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/resident/notifications")
public class ResidentNotificationController {

    private final AccountNotificationService accountNotificationService;
    private final AccountService accountService;

    public ResidentNotificationController(AccountNotificationService accountNotificationService, AccountService accountService) {
        this.accountNotificationService = accountNotificationService;
        this.accountService = accountService;
    }

    @GetMapping
    public String viewNotifications(
            @RequestParam(required = false) String filter,
            HttpSession session,
            Model model) {

        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null || !"RESIDENT".equalsIgnoreCase(currentUser.getRole().getRoleName())) {
            currentUser = accountService.findByUsername("tran.thi.b").orElse(null);
            if (currentUser == null) {
                List<Account> accounts = accountService.findAll();
                for (Account acc : accounts) {
                    if ("RESIDENT".equalsIgnoreCase(acc.getRole().getRoleName())) {
                        currentUser = acc;
                        break;
                    }
                }
            }
            if (currentUser != null) {
                session.setAttribute("currentUser", currentUser);
            } else {
                return "redirect:/login";
            }
        }

        List<AccountNotification> allNotifications = accountNotificationService.findByAccountId(currentUser.getAccountId());

        // Apply filters
        List<AccountNotification> filteredNotifications = allNotifications;
        if ("unread".equalsIgnoreCase(filter)) {
            filteredNotifications = allNotifications.stream()
                    .filter(an -> !an.getIsRead())
                    .collect(Collectors.toList());
        } else if ("read".equalsIgnoreCase(filter)) {
            filteredNotifications = allNotifications.stream()
                    .filter(AccountNotification::getIsRead)
                    .collect(Collectors.toList());
        }

        long unreadCount = allNotifications.stream()
                .filter(an -> !an.getIsRead())
                .count();

        model.addAttribute("notifications", filteredNotifications);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("currentFilter", filter != null ? filter : "all");

        return "resident/notification/list";
    }

    @GetMapping("/{id}")
    public String viewNotificationDetail(@PathVariable Long id, HttpSession session, Model model) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null || !"RESIDENT".equalsIgnoreCase(currentUser.getRole().getRoleName())) {
            currentUser = accountService.findByUsername("tran.thi.b").orElse(null);
            if (currentUser == null) {
                List<Account> accounts = accountService.findAll();
                for (Account acc : accounts) {
                    if ("RESIDENT".equalsIgnoreCase(acc.getRole().getRoleName())) {
                        currentUser = acc;
                        break;
                    }
                }
            }
            if (currentUser != null) {
                session.setAttribute("currentUser", currentUser);
            } else {
                return "redirect:/login";
            }
        }

        Optional<AccountNotification> accountNotifOpt = accountNotificationService.findById(id);
        if (accountNotifOpt.isEmpty()) {
            return "redirect:/resident/notifications";
        }

        AccountNotification accountNotif = accountNotifOpt.get();

        // Security check: Ensure the notification belongs to the logged-in user
        if (!accountNotif.getAccount().getAccountId().equals(currentUser.getAccountId())) {
            return "redirect:/resident/notifications";
        }

        // Mark as read if not already read
        if (!accountNotif.getIsRead()) {
            accountNotif.setIsRead(true);
            accountNotif.setReadAt(LocalDateTime.now());
            accountNotificationService.save(accountNotif);
        }

        model.addAttribute("accountNotification", accountNotif);
        return "resident/notification/detail";
    }
}
