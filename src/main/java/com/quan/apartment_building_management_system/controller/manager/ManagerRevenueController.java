package com.quan.apartment_building_management_system.controller.manager;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager/revenue")
public class ManagerRevenueController {

    private static final int PAGE_SIZE = 15;
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DT_FMT_DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat VND_FMT = new DecimalFormat("#,###,###,###",
            DecimalFormatSymbols.getInstance(new Locale("vi", "VN")));

    private final BillRepository billRepository;

    public ManagerRevenueController(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String revenueReport(
            @RequestParam(value = "fromDate",    required = false) String fromDateStr,
            @RequestParam(value = "toDate",      required = false) String toDateStr,
            @RequestParam(value = "status",      required = false) String statusStr,
            @RequestParam(value = "page",        defaultValue = "0") int page,
            @RequestParam(value = "month", required = false) Integer month,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Validate: fromDate must be before toDate
        if (fromDateStr != null && !fromDateStr.isBlank()
                && toDateStr != null && !toDateStr.isBlank()) {
            LocalDate from = parseLocalDate(fromDateStr, null);
            LocalDate to = parseLocalDate(toDateStr, null);
            if (from != null && to != null && from.isAfter(to)) {
                redirectAttributes.addFlashAttribute("message", "Từ ngày phải trước hoặc bằng Đến ngày.");
                redirectAttributes.addFlashAttribute("messageType", "error");
                return "redirect:/manager/revenue";
            }
        }

        Byte statusByte = parseStatus(statusStr);

        LocalDate fromParsed = parseLocalDate(fromDateStr, LocalDate.now().minusMonths(1));
        LocalDate toParsed   = parseLocalDate(toDateStr,   LocalDate.now());
        Integer fromYear = fromParsed.getYear();
        Integer fromMonth = fromParsed.getMonthValue();
        Integer toYear = toParsed.getYear();
        Integer toMonth = toParsed.getMonthValue();

        BigDecimal totalRev     = billRepository.sumTotalByDateRange(fromYear, fromMonth, toYear, toMonth, statusByte, null);
        BigDecimal collected    = billRepository.sumCollectedByDateRange(fromYear, fromMonth, toYear, toMonth, null);
        BigDecimal outstanding  = billRepository.sumOutstandingByDateRange(fromYear, fromMonth, toYear, toMonth, null);
        BigDecimal overdue      = billRepository.sumOverdueByDateRange(fromYear, fromMonth, toYear, toMonth, null);

        model.addAttribute("totalRevenue",  VND_FMT.format(totalRev) + " VND");
        model.addAttribute("collected",     VND_FMT.format(collected) + " VND");
        model.addAttribute("outstanding",   VND_FMT.format(outstanding) + " VND");
        model.addAttribute("overdue",       VND_FMT.format(overdue) + " VND");

        Byte monthByte = (month != null) ? month.byteValue() : null;
        Page<Bill> billPage = billRepository.findBillsWithDetails(fromYear, fromMonth, toYear, toMonth, statusByte, null, monthByte,
                PageRequest.of(page, PAGE_SIZE));

        List<RevenueRecordDTO> records = billPage.getContent().stream()
                .map(this::toRecordDTO)
                .collect(Collectors.toList());

        model.addAttribute("records", records);
        model.addAttribute("currentPage", billPage.getNumber());
        model.addAttribute("totalPages", billPage.getTotalPages());
        model.addAttribute("totalRecords", billPage.getTotalElements());

        model.addAttribute("fromDate",    fromDateStr);
        model.addAttribute("toDate",      toDateStr);
        model.addAttribute("status",      statusStr);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("hasActiveFilters",
                (fromDateStr != null && !fromDateStr.isBlank())
                || (toDateStr != null && !toDateStr.isBlank())
                || (statusStr != null && !statusStr.isBlank()));

        return "manager/revenue/revenue_report";
    }

    @GetMapping("/detail/{id}")
    @Transactional(readOnly = true)
    public String revenueDetail(@PathVariable("id") Integer billId, Model model) {
        Optional<Bill> billOpt = billRepository.findById(billId);
        if (billOpt.isEmpty()) {
            return "redirect:/manager/revenue";
        }
        Bill bill = billOpt.get();

        String aptNum = bill.getApartment() != null
                ? bill.getApartment().getApartmentNumber() : "\u2014";
        String residentName = "\u2014";
        if (bill.getApartment() != null && bill.getApartment().getResidentApartments() != null
                && !bill.getApartment().getResidentApartments().isEmpty()) {
            ResidentApartment ra = bill.getApartment().getResidentApartments().get(0);
            if (ra.getProfile() != null) {
                residentName = ra.getProfile().getFullName();
            }
        }

        String type = "\u2014";
        String note = "";
        if (bill.getBillDetails() != null && !bill.getBillDetails().isEmpty()) {
            var bd = bill.getBillDetails().get(0);
            type = Optional.ofNullable(bd.getServiceItem())
                    .map(s -> s.getServiceType()).orElse("\u2014");
            note = bd.getDescription() != null ? bd.getDescription() : "";
        }

        String invId = String.format("INV-%04d-%02d", bill.getBillYear(), bill.getBillMonth())
                + "-" + String.format("%04d", bill.getBillId());

        String statusLabel;
        switch (bill.getStatus()) {
            case 0:  statusLabel = "PENDING";   break;
            case 1:  statusLabel = "PAID";      break;
            case 2:  statusLabel = "OVERDUE";   break;
            default: statusLabel = "PENDING";
        }

        model.addAttribute("invoiceId", invId);
        model.addAttribute("residentName", residentName);
        model.addAttribute("aptNum", aptNum);
        model.addAttribute("revenueType", type);
        model.addAttribute("totalAmount", VND_FMT.format(bill.getTotalAmount()) + " VND");
        model.addAttribute("statusLabel", statusLabel);
        model.addAttribute("statusByte", bill.getStatus());
        model.addAttribute("dueDate", bill.getDueDate() != null
                ? bill.getDueDate().format(DT_FMT_DISPLAY) : null);
        model.addAttribute("paidDate", bill.getPaidDate() != null
                ? bill.getPaidDate().format(DT_FMT_DISPLAY) : null);
        model.addAttribute("createdDate", bill.getCreatedDate() != null
                ? bill.getCreatedDate().format(DT_FMT_DISPLAY) : null);
        model.addAttribute("note", note);
        model.addAttribute("billDetails", bill.getBillDetails());
        model.addAttribute("payments", bill.getPayments());
        model.addAttribute("billMonth", bill.getBillMonth() + "/" + bill.getBillYear());

        return "manager/revenue/detail";
    }

    private RevenueRecordDTO toRecordDTO(Bill bill) {
        String aptNum = bill.getApartment() != null
                ? bill.getApartment().getApartmentNumber() : "\u2014";
        String residentName = "\u2014";
        String initials = "?";
        if (bill.getApartment() != null && bill.getApartment().getResidentApartments() != null
                && !bill.getApartment().getResidentApartments().isEmpty()) {
            ResidentApartment ra = bill.getApartment().getResidentApartments().get(0);
            if (ra.getProfile() != null) {
                residentName = ra.getProfile().getFullName();
                initials = extractInitials(residentName);
            }
        }

        String type = "\u2014";
        String note = "";
        if (bill.getBillDetails() != null && !bill.getBillDetails().isEmpty()) {
            var bd = bill.getBillDetails().get(0);
            type = Optional.ofNullable(bd.getServiceItem())
                    .map(s -> s.getServiceType()).orElse("\u2014");
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

        String dueDateStr  = bill.getDueDate() != null ? bill.getDueDate().format(DT_FMT) : null;
        String paidDateStr = bill.getPaidDate() != null ? bill.getPaidDate().format(DT_FMT) : null;

        return new RevenueRecordDTO(
                bill.getBillId(), invId, residentName, initials, aptNum, type,
                VND_FMT.format(bill.getTotalAmount()) + " VND",
                statusLabel, dueDateStr, paidDateStr, note
        );
    }

    private String extractInitials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    private LocalDate parseLocalDate(String str, LocalDate fallback) {
        if (str == null || str.isBlank()) return fallback;
        try {
            return LocalDate.parse(str.trim());
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
            
            default -> null;
        };
    }
}