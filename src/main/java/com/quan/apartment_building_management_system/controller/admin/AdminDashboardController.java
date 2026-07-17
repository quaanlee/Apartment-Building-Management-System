package com.quan.apartment_building_management_system.controller.admin;

import com.quan.apartment_building_management_system.dto.admin.AdminDashboardStatsDto;
import com.quan.apartment_building_management_system.service.admin.AdminDashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import com.quan.apartment_building_management_system.entity.Account;

@Controller
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    private boolean isUnauthorized(HttpSession session) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        return currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole().getRoleName());
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (isUnauthorized(session)) {
            return "redirect:/login";
        }

        AdminDashboardStatsDto stats = adminDashboardService.getDashboardStats();
        model.addAttribute("stats", stats);

        return "admin/dashboard/dashboard";
    }
}
