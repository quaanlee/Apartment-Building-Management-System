package com.quan.apartment_building_management_system.controller.admin;

import com.quan.apartment_building_management_system.dto.systemlog.SystemLogDTO;
import com.quan.apartment_building_management_system.entity.SystemLog;
import com.quan.apartment_building_management_system.service.system.SystemLogService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/admin/logs")
public class AdminLogController {

    private static final int PAGE_SIZE = 5; // Tạm thời để 2 cho dễ test phân trang
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Map<String, String> ROLE_MAP = new HashMap<>();
    private static final Map<String, String> ACTION_MAP = new HashMap<>();
    private static final Map<String, String> ENTITY_MAP = new HashMap<>();

    static {
        ROLE_MAP.put("Admin", "Quản trị viên");
        ROLE_MAP.put("Manager", "Quản lý");
        ROLE_MAP.put("Maintenance Staff", "Nhân viên bảo trì");
        ROLE_MAP.put("Resident", "Cư dân");
        ROLE_MAP.put("System", "Hệ thống");

        ACTION_MAP.put("CHANGE_PASSWORD", "Đổi mật khẩu");
        ACTION_MAP.put("UPDATE_ACCOUNT", "Cập nhật tài khoản");
        ACTION_MAP.put("CREATE_ACCOUNT", "Tạo tài khoản");
        ACTION_MAP.put("RESET_PASSWORD", "Khôi phục mật khẩu");
        ACTION_MAP.put("LOCK_ACCOUNT", "Khóa tài khoản");
        ACTION_MAP.put("UNLOCK_ACCOUNT", "Mở khóa tài khoản");
        ACTION_MAP.put("CREATE_PROFILE", "Tạo hồ sơ cư dân");
        ACTION_MAP.put("UPDATE_PROFILE", "Cập nhật hồ sơ cư dân");
        ACTION_MAP.put("CREATE_EMPLOYEE_PROFILE", "Tạo hồ sơ nhân viên");
        ACTION_MAP.put("UPDATE_EMPLOYEE_PROFILE", "Cập nhật hồ sơ nhân viên");
        ACTION_MAP.put("CREATE_APARTMENT", "Tạo căn hộ");
        ACTION_MAP.put("UPDATE_APARTMENT", "Cập nhật căn hộ");
        ACTION_MAP.put("DELETE_APARTMENT", "Xóa / Khóa căn hộ");
        ACTION_MAP.put("ASSIGN_RESIDENT", "Gán cư dân vào phòng");
        ACTION_MAP.put("MOVE_OUT_RESIDENT", "Chuyển cư dân ra khỏi phòng");
        ACTION_MAP.put("CREATE_SERVICE", "Tạo dịch vụ");
        ACTION_MAP.put("UPDATE_SERVICE", "Cập nhật / Thay đổi trạng thái dịch vụ");
        ACTION_MAP.put("CREATE_UTILITY", "Tạo tiện ích");
        ACTION_MAP.put("UPDATE_UTILITY", "Cập nhật / Thay đổi trạng thái tiện ích");
        ACTION_MAP.put("CREATE_UTILITY_RESOURCE", "Tạo tài nguyên tiện ích");
        ACTION_MAP.put("UPDATE_UTILITY_RESOURCE", "Cập nhật / Thay đổi trạng thái tài nguyên tiện ích");
        ACTION_MAP.put("CREATE_UTILITY_PRICE", "Tạo đơn giá tiện ích");
        ACTION_MAP.put("UPDATE_UTILITY_PRICE", "Cập nhật đơn giá tiện ích");
        ACTION_MAP.put("CREATE_UTILITY_BOOKING", "Tạo đặt lịch tiện ích");
        ACTION_MAP.put("UPDATE_UTILITY_BOOKING", "Cập nhật đặt lịch tiện ích");
        ACTION_MAP.put("UPDATE_BOOKING_STATUS", "Cập nhật trạng thái đặt lịch tiện ích");
        ACTION_MAP.put("CANCEL_BOOKING", "Hủy đặt lịch tiện ích");
        ACTION_MAP.put("CREATE_BILL", "Tạo hóa đơn");
        ACTION_MAP.put("UPDATE_BILL", "Cập nhật hóa đơn");
        ACTION_MAP.put("PAYMENT_BILL", "Thanh toán hóa đơn");
        ACTION_MAP.put("CREATE_MAINTENANCE_REQUEST", "Tạo yêu cầu bảo trì");
        ACTION_MAP.put("UPDATE_MAINTENANCE_REQUEST", "Cập nhật yêu cầu bảo trì");
        ACTION_MAP.put("CREATE_MAINTENANCE_REPORT", "Tạo báo cáo bảo trì");
        ACTION_MAP.put("UPDATE_MAINTENANCE_REPORT", "Cập nhật báo cáo bảo trì");
        ACTION_MAP.put("SUBMIT_MAINTENANCE_REPORT", "Nộp báo cáo bảo trì");
        ACTION_MAP.put("CREATE_VEHICLE_REGISTRATION", "Tạo đăng ký xe");
        ACTION_MAP.put("UPDATE_VEHICLE_REGISTRATION", "Cập nhật đăng ký xe");
        ACTION_MAP.put("APPROVE_VEHICLE_REGISTRATION", "Phê duyệt đăng ký xe");
        ACTION_MAP.put("REJECT_VEHICLE_REGISTRATION", "Từ chối đăng ký xe");
        ACTION_MAP.put("REVOKE_VEHICLE_REGISTRATION", "Thu hồi đăng ký xe");
        ACTION_MAP.put("CREATE_NOTIFICATION", "Tạo thông báo");
        ACTION_MAP.put("UPDATE_NOTIFICATION", "Cập nhật thông báo");
        ACTION_MAP.put("DELETE_NOTIFICATION", "Xóa thông báo");

        ENTITY_MAP.put("Account", "Tài khoản");
        ENTITY_MAP.put("Apartment", "Căn hộ");
        ENTITY_MAP.put("Request", "Yêu cầu");
        ENTITY_MAP.put("Utility", "Tiện ích");
        ENTITY_MAP.put("UtilityResource", "Tài nguyên tiện ích");
        ENTITY_MAP.put("UtilityPrice", "Đơn giá tiện ích");
        ENTITY_MAP.put("UtilityBooking", "Đặt lịch tiện ích");
        ENTITY_MAP.put("Resource", "Tài nguyên");
        ENTITY_MAP.put("Booking", "Đặt chỗ");
        ENTITY_MAP.put("Bill", "Hóa đơn");
        ENTITY_MAP.put("Payment", "Thanh toán");
        ENTITY_MAP.put("ServiceItem", "Dịch vụ");
        ENTITY_MAP.put("System", "Hệ thống");
        ENTITY_MAP.put("Profile", "Hồ sơ cư dân");
        ENTITY_MAP.put("EmployeeProfile", "Hồ sơ nhân viên");
        ENTITY_MAP.put("MaintenanceRequest", "Yêu cầu bảo trì");
        ENTITY_MAP.put("MaintenanceTask", "Nhiệm vụ bảo trì");
        ENTITY_MAP.put("MaintenanceReport", "Báo cáo bảo trì");
        ENTITY_MAP.put("Vehicle", "Xe");
        ENTITY_MAP.put("Notification", "Thông báo");
    }

    private final SystemLogService systemLogService;

    public AdminLogController(SystemLogService systemLogService) {
        this.systemLogService = systemLogService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String listLogs(
            @RequestParam(value = "fromDate", required = false) String fromDateStr,
            @RequestParam(value = "toDate", required = false) String toDateStr,
            @RequestParam(value = "userRole", required = false) String userRole,
            @RequestParam(value = "action", required = false) String action,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        if (page < 0)
            page = 0;
        LocalDateTime fromDate = parseDate(fromDateStr, LocalDate.now().minusMonths(1).atStartOfDay());
        LocalDateTime toDate = parseDate(toDateStr, LocalDateTime.now());

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

        model.addAttribute("fromDate", fromDateStr);
        model.addAttribute("toDate", toDateStr);
        model.addAttribute("userRole", userRole);
        model.addAttribute("action", action);
        model.addAttribute("search", search);

        model.addAttribute("totalEvents", formatNumber(logPage.getTotalElements()));
        model.addAttribute("residentLogs", formatNumber(systemLogService.countLogsByRole("Resident")));
        model.addAttribute("managerLogs", formatNumber(systemLogService.countLogsByRole("Manager")));
        model.addAttribute("maintenanceLogs", formatNumber(systemLogService.countLogsByRole("Maintenance Staff")));

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

        SystemLogDTO dto = new SystemLogDTO(
                log.getSystemLogId(),
                createdStr,
                log.getAction(),
                roleName,
                fullName,
                initials,
                log.getEntityType(),
                log.getEntityId(),
                log.getOldValue() != null ? log.getOldValue() : "{}",
                log.getNewValue() != null ? log.getNewValue() : "{}",
                log.getDescription() != null ? log.getDescription() : "");
        dto.setActionName(
                ACTION_MAP.getOrDefault(log.getAction(), log.getAction() != null ? log.getAction() : "Không xác định"));
        dto.setRoleName(ROLE_MAP.getOrDefault(roleName, roleName));
        dto.setEntityTypeName(ENTITY_MAP.getOrDefault(log.getEntityType(), log.getEntityType()));
        return dto;
    }

    private String extractInitials(String name) {
        if (name == null || name.isBlank())
            return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1)
            return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    private LocalDateTime parseDate(String str, LocalDateTime fallback) {
        if (str == null || str.isBlank())
            return fallback;
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
        if (n < 1000)
            return String.valueOf(n);
        return String.format("%,d", n);
    }
}
