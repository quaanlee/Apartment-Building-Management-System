package com.quan.apartment_building_management_system.controller;

import com.quan.apartment_building_management_system.entity.SystemLog;
import com.quan.apartment_building_management_system.repository.RoleRepository;
import com.quan.apartment_building_management_system.service.system.SystemLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SystemLogController {

    private final SystemLogService systemLogService;
    private final RoleRepository roleRepository;

    public SystemLogController(SystemLogService systemLogService, RoleRepository roleRepository) {
        this.systemLogService = systemLogService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/admin/system-logs")
    public String getSystemLogsPage(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "roleName", required = false) String roleName,
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {

        // Validate date order business logic to avoid empty states or logic errors
        boolean dateSwapped = false;
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            LocalDate temp = fromDate;
            fromDate = toDate;
            toDate = temp;
            dateSwapped = true;
        }

        // Fetch logs sorted by latest first
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<SystemLog> logPage = systemLogService.searchLogs(search, roleName, fromDate, toDate, pageable);

        // Fetch available roles from database to dynamically populate the filter dropdown
        List<String> roles = new ArrayList<>();
        roles.add("All Roles");
        roles.add("System"); // Special virtual role representing system-automated logs (no account)
        
        List<String> dbRoles = roleRepository.findAll().stream()
                .map(r -> r.getRoleName())
                .collect(Collectors.toList());
        roles.addAll(dbRoles);

        // Remove duplicate "System" if it exists in db roles
        roles = roles.stream().distinct().collect(Collectors.toList());

        // Pass variables to view
        model.addAttribute("logs", logPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", logPage.getTotalPages());
        model.addAttribute("totalElements", logPage.getTotalElements());
        model.addAttribute("pageSize", size);
        
        // Pass original queries back for pagination and form state
        model.addAttribute("search", search);
        model.addAttribute("selectedRole", roleName != null ? roleName : "All Roles");
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("roles", roles);
        model.addAttribute("dateSwapped", dateSwapped);

        return "admin/system-logs";
    }
}
