package com.quan.apartment_building_management_system.controller.admin;

import com.quan.apartment_building_management_system.dto.SystemLogDTO;
import com.quan.apartment_building_management_system.entity.SystemLog;
import com.quan.apartment_building_management_system.service.system.SystemLogService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/logs")
public class AdminLogController {

    private static final int PAGE_SIZE = 7;
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SystemLogService systemLogService;

    public AdminLogController(SystemLogService systemLogService) {
        this.systemLogService = systemLogService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String listLogs(
            @RequestParam(value = "fromDate",  required = false) String fromDateStr,
            @RequestParam(value = "toDate",    required = false) String toDateStr,
            @RequestParam(value = "userRole",  required = false) String userRole,
            @RequestParam(value = "action",    required = false) String action,
            @RequestParam(value = "search",    required = false) String search,
            @RequestParam(value = "page",      defaultValue = "0") int page,
            Model model) {

        LocalDateTime fromDate = parseDate(fromDateStr, LocalDate.now().minusMonths(1).atStartOfDay());
        LocalDateTime toDate   = parseDate(toDateStr,   LocalDateTime.now());

        Page<SystemLog> logPage = systemLogService.findFiltered(
                fromDate, toDate,
                (userRole != null && !userRole.isEmpty()) ? userRole : null,
                (action != null && !action.isEmpty()) ? action : null,
                (search != null && !search.isEmpty()) ? search : null,
                page, PAGE_SIZE);

        List<SystemLogDTO> dtos = logPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        model.addAttribute("logs", dtos);
        model.addAttribute("totalLogs", logPage.getTotalElements());
        model.addAttribute("currentPage", logPage.getNumber() + 1);
        model.addAttribute("totalPages", logPage.getTotalPages() < 1 ? 1 : logPage.getTotalPages());
        model.addAttribute("pageSize", PAGE_SIZE);

        model.addAttribute("fromDate",  fromDateStr);
        model.addAttribute("toDate",    toDateStr);
        model.addAttribute("userRole",  userRole);
        model.addAttribute("action",    action);
        model.addAttribute("search",    search);

        model.addAttribute("totalEvents",    formatNumber(logPage.getTotalElements()));
        model.addAttribute("residentLogs",   formatNumber(systemLogService.countLogsByRole("Resident")));
        model.addAttribute("managerLogs",    formatNumber(systemLogService.countLogsByRole("Manager")));
        model.addAttribute("maintenanceLogs",formatNumber(systemLogService.countLogsByRole("Maintenance Staff")));

        return "admin/logs/system_logs";
    }

    @GetMapping("/detail/{id}")
    @Transactional(readOnly = true)
    public String viewLogDetail(@org.springframework.web.bind.annotation.PathVariable("id") Long id, Model model) {
        Optional<SystemLog> logOpt = systemLogService.findById(id);
        if (logOpt.isEmpty()) {
            return "redirect:/admin/logs";
        }
        model.addAttribute("log", toDTO(logOpt.get()));
        return "admin/logs/log_detail";
    }

    // ── helpers ────────────────────────────────────────────────

    private SystemLogDTO toDTO(SystemLog log) {
        String roleName = "";
        String fullName = "";
        if (log.getAccount() != null) {
            roleName = Optional.ofNullable(log.getAccount().getRole())
                    .map(r -> r.getRoleName()).orElse("");
            fullName = Optional.ofNullable(log.getAccount().getProfile())
                    .map(p -> p.getFullName()).orElse(log.getAccount().getUsername());
        }
        String initials = extractInitials(fullName);
        String createdStr = log.getCreatedAt() != null ? log.getCreatedAt().format(DT_FMT) : "";

        return new SystemLogDTO(
                log.getSystemLogId(),
                createdStr,
                log.getAction(),
                roleName,
                fullName,
                initials,
                log.getEntityType(),
                log.getEntityId(),
                log.getIpAddress(),
                log.getDetails() != null ? log.getDetails() : "{}"
        );
    }

    private String extractInitials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    private LocalDateTime parseDate(String str, LocalDateTime fallback) {
        if (str == null || str.isBlank()) return fallback;
        try {
            return LocalDate.parse(str.trim()).atStartOfDay();
        } catch (Exception e) {
            return fallback;
        }
    }

    private long countByAction(List<SystemLog> logs, String action) {
        return logs.stream().filter(l -> action.equals(l.getAction())).count();
    }

    private String formatNumber(long n) {
        if (n < 1000) return String.valueOf(n);
        return String.format("%,d", n);
    }
}

