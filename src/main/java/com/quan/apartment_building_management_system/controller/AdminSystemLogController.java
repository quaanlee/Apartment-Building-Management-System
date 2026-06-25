package com.quan.apartment_building_management_system.controller;

import com.quan.apartment_building_management_system.dto.SystemLogViewDto;
import com.quan.apartment_building_management_system.service.system.SystemLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

public class AdminSystemLogController {
    private final SystemLogService systemLogService;

    public AdminSystemLogController(SystemLogService systemLogService) {
        this.systemLogService = systemLogService;
    }

    @GetMapping("/admin/system-logs")
    public String showSystemLogsPage(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            String role,
            Model model
    ) {
        if (fromDate == null) {
            fromDate = LocalDate.of(2023, 10, 24);
        }

        if (toDate == null) {
            toDate = LocalDate.of(2023, 10, 31);
        }

        if (role == null || role.isBlank()) {
            role = "All Roles";
        }

        String error = null;

        if (!toDate.isAfter(fromDate)) {
            error = "To Date must be greater than From Date.";
        }

        List<SystemLogViewDto> logs = systemLogService.getSystemLogs(fromDate, toDate, role);

        model.addAttribute("totalEvents", systemLogService.countTotalEvents());
        model.addAttribute("logs", logs);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("role", role);
        model.addAttribute("error", error);

        return "admin/system-logs";
    }
}
