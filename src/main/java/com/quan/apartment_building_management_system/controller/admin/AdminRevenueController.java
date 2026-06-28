package com.quan.apartment_building_management_system.controller.admin;

import com.quan.apartment_building_management_system.dto.RevenueRecordDTO;
import com.quan.apartment_building_management_system.entity.Bill;
import com.quan.apartment_building_management_system.entity.ResidentApartment;
import com.quan.apartment_building_management_system.repository.BillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/revenue")
public class AdminRevenueController {

    private static final int PAGE_SIZE = 15;
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DecimalFormat VND_FMT = new DecimalFormat("#,###,###,###",
            DecimalFormatSymbols.getInstance(Locale.of("vi", "VN")));

    private final BillRepository billRepository;

    public AdminRevenueController(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String revenueReport(
            @RequestParam(value = "fromDate",    required = false) String fromDateStr,
            @RequestParam(value = "toDate",      required = false) String toDateStr,
            @RequestParam(value = "revenueType", required = false) String revenueType,
            @RequestParam(value = "status",      required = false) String statusStr,
            @RequestParam(value = "page",        defaultValue = "0") int page,
            Model model) {

        LocalDateTime fromDate = parseDate(fromDateStr, LocalDate.now().minusMonths(1).atStartOfDay());
        LocalDateTime toDate   = parseDate(toDateStr,   LocalDateTime.now());
        Byte statusByte = parseStatus(statusStr);

        // ── Stats ──────────────────────────────────────────────────
        BigDecimal totalRev     = billRepository.sumTotalByDateRange(fromDate, toDate);
        BigDecimal collected    = billRepository.sumCollectedByDateRange(fromDate, toDate);
        BigDecimal outstanding  = billRepository.sumOutstandingByDateRange(fromDate, toDate);
        BigDecimal overdue      = billRepository.sumOverdueByDateRange(fromDate, toDate);

        model.addAttribute("totalRevenue",  VND_FMT.format(totalRev) + " VND");
        model.addAttribute("collected",     VND_FMT.format(collected) + " VND");
        model.addAttribute("outstanding",   VND_FMT.format(outstanding) + " VND");
        model.addAttribute("overdue",       VND_FMT.format(overdue) + " VND");

        // ── Transaction Records ────────────────────────────────────
        Page<Bill> billPage = billRepository.findBillsWithDetails(fromDate, toDate, statusByte,
                PageRequest.of(page, PAGE_SIZE));

        List<RevenueRecordDTO> records = billPage.getContent().stream()
                .map(this::toRecordDTO)
                .collect(Collectors.toList());

        model.addAttribute("records", records);
        model.addAttribute("currentPage", billPage.getNumber() + 1);
        model.addAttribute("totalPages", billPage.getTotalPages());
        model.addAttribute("pageSize", PAGE_SIZE);
        model.addAttribute("totalRecords", billPage.getTotalElements());

        // ── Preserve filters ───────────────────────────────────────
        model.addAttribute("fromDate",    fromDateStr);
        model.addAttribute("toDate",      toDateStr);
        model.addAttribute("revenueType", revenueType);
        model.addAttribute("status",      statusStr);

        return "admin/revenue/list";
    }

    // ── helpers ──────────────────────────────────────────────────

    private RevenueRecordDTO toRecordDTO(Bill bill) {
        String aptNum = bill.getApartment() != null
                ? bill.getApartment().getApartmentNumber() : "—";
        String residentName = "—";
        String initials = "?";
        if (bill.getApartment() != null && bill.getApartment().getResidentApartments() != null
                && !bill.getApartment().getResidentApartments().isEmpty()) {
            ResidentApartment ra = bill.getApartment().getResidentApartments().get(0);
            if (ra.getProfile() != null) {
                residentName = ra.getProfile().getFullName();
                initials = extractInitials(residentName);
            }
        }

        String type = "—";
        String note = "";
        if (bill.getBillDetails() != null && !bill.getBillDetails().isEmpty()) {
            var bd = bill.getBillDetails().get(0);
            type = Optional.ofNullable(bd.getServiceItem())
                    .map(s -> s.getServiceType()).orElse("—");
            note = bd.getDescription() != null ? bd.getDescription() : "";
        }

        String statusLabel;
        switch (bill.getStatus()) {
            case 0:  statusLabel = "PENDING";   break;
            case 1:  statusLabel = "PAID";      break;
            case 2:  statusLabel = "OVERDUE";   break;
            default: statusLabel = "UNKNOWN";
        }

        String invId = String.format("INV-%04d-%02d", bill.getBillYear(), bill.getBillMonth())
                + "-" + String.format("%04d", bill.getBillId());

        String dueDateStr  = bill.getDueDate() != null ? bill.getDueDate().format(DT_FMT) : "—";
        String paidDateStr = bill.getPaidDate() != null ? bill.getPaidDate().format(DT_FMT) : null;

        return new RevenueRecordDTO(
                invId,
                residentName,
                initials,
                aptNum,
                type,
                VND_FMT.format(bill.getTotalAmount()) + " VND",
                statusLabel,
                dueDateStr,
                paidDateStr,
                note
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

    private Byte parseStatus(String s) {
        if (s == null || s.isBlank()) return null;
        return switch (s.toUpperCase()) {
            case "PAID"      -> 1;
            case "PENDING"   -> 0;
            case "OVERDUE"   -> 2;
            case "CANCELLED" -> 3;
            default -> null;
        };
    }
}
