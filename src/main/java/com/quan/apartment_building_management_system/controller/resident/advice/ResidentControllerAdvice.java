package com.quan.apartment_building_management_system.controller.resident.advice;

import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.repository.AccountNotificationRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "com.quan.apartment_building_management_system.controller.resident")
public class ResidentControllerAdvice {

    private final AccountNotificationRepository accountNotificationRepository;

    public ResidentControllerAdvice(AccountNotificationRepository accountNotificationRepository) {
        this.accountNotificationRepository = accountNotificationRepository;
    }

    @ModelAttribute("unreadNotificationCount")
    public long getUnreadNotificationCount(HttpSession session) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser != null && currentUser.getRole() != null && "RESIDENT".equalsIgnoreCase(currentUser.getRole().getRoleName())) {
            return accountNotificationRepository.findByAccountAccountIdAndIsRead(currentUser.getAccountId(), false).size();
        }
        return 0;
    }
}
