package com.quan.apartment_building_management_system.controller.resident.payment;

import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.Payment;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.entity.Apartment;
import com.quan.apartment_building_management_system.repository.PaymentRepository;
import com.quan.apartment_building_management_system.repository.ProfileRepository;
import jakarta.servlet.http.HttpSession;
import com.quan.apartment_building_management_system.service.payment.PayOSService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/resident/payment")
public class ResidentPaymentController {

    private final PaymentRepository paymentRepository;
    private final ProfileRepository profileRepository;
    private final PayOSService payOSService;

    public ResidentPaymentController(PaymentRepository paymentRepository, ProfileRepository profileRepository, PayOSService payOSService) {
        this.paymentRepository = paymentRepository;
        this.profileRepository = profileRepository;
        this.payOSService = payOSService;
    }

    @GetMapping
    public String paymentHistory(@RequestParam(required = false) String status,
                                 @RequestParam(required = false) String startDate,
                                 @RequestParam(required = false) String endDate,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "8") int size,
                                 HttpSession session,
                                 Model model) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        Optional<Profile> profileOpt = profileRepository.findByAccountAccountId(currentUser.getAccountId());
        if (profileOpt.isEmpty() || profileOpt.get().getApartment() == null) {
            model.addAttribute("error", "Your account is not assigned any apartment!");
            model.addAttribute("payments", List.of());
            model.addAttribute("totalPaid", BigDecimal.ZERO);
            model.addAttribute("paidThisMonth", BigDecimal.ZERO);
            model.addAttribute("pendingOrFailed", 0L);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            return "resident/payment/payment";
        }

        Apartment apartment = profileOpt.get().getApartment();

        // Sync pending payments with PayOS on page load
        try {
            payOSService.syncPendingPaymentsForApartment(apartment.getApartmentId());
        } catch (Exception e) {
            System.err.println("[ResidentPaymentController Sync Warning] " + e.getMessage());
        }

        // 1. Calculate overall summary statistics based on ALL payments
        List<Payment> allPayments = paymentRepository.findByBillApartmentApartmentIdOrderByPaymentDateDesc(apartment.getApartmentId());
        
        BigDecimal totalPaid = allPayments.stream()
                .filter(p -> p.getStatus() != null && p.getStatus() == 1)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime now = LocalDateTime.now();
        BigDecimal paidThisMonth = allPayments.stream()
                .filter(p -> p.getStatus() != null && p.getStatus() == 1
                        && p.getPaymentDate() != null
                        && p.getPaymentDate().getYear() == now.getYear()
                        && p.getPaymentDate().getMonthValue() == now.getMonthValue())
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingOrFailed = allPayments.stream()
                .filter(p -> p.getStatus() != null && p.getStatus() != 1)
                .count();

        // 2. Parse date filters
        LocalDateTime startDateTime = null;
        if (startDate != null && !startDate.isEmpty()) {
            startDateTime = LocalDate.parse(startDate).atStartOfDay();
        }
        LocalDateTime endDateTime = null;
        if (endDate != null && !endDate.isEmpty()) {
            endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
        }

        if (startDateTime != null && endDateTime != null && startDateTime.isAfter(endDateTime)) {
            model.addAttribute("error", "To Date must after From Date");
            startDateTime = null;
            endDateTime = null;
        }

        // 3. Status filter code
        Byte statusCode = null;
        if (status != null && !status.isEmpty()) {
            statusCode = switch (status) {
                case "success" -> (byte) 1;
                case "failed"  -> (byte) 2;
                case "pending" -> (byte) 0;
                default        -> null;
            };
        }

        // 4. Retrieve paginated and filtered payments
        Pageable pageable = PageRequest.of(page, size);
        Page<Payment> paymentPage = paymentRepository.findByApartmentAndFilter(
                apartment.getApartmentId(), statusCode, startDateTime, endDateTime, pageable);

        model.addAttribute("payments", paymentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paymentPage.getTotalPages());
        model.addAttribute("totalItems", paymentPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("activeStatus", status);

        model.addAttribute("totalPaid", totalPaid);
        model.addAttribute("paidThisMonth", paidThisMonth);
        model.addAttribute("pendingOrFailed", pendingOrFailed);

        return "resident/payment/payment";
    }
}
