package com.quan.apartment_building_management_system.controller.resident.payment;

import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.Payment;
import com.quan.apartment_building_management_system.repository.PaymentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/resident/payment")
public class ResidentPaymentController {

    private final PaymentRepository paymentRepository;

    public ResidentPaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @GetMapping
    public String paymentHistory(@RequestParam(required = false) String status,
                                 HttpSession session,
                                 Model model) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        List<Payment> payments = paymentRepository
                .findByPaidByAccountIdOrderByPaymentDateDesc(currentUser.getAccountId());

        // Filter by status if provided
        if (status != null && !status.isEmpty()) {
            byte statusCode = switch (status) {
                case "success" -> (byte) 1;
                case "failed"  -> (byte) 2;
                default        -> (byte) 0; // pending
            };
            payments = payments.stream()
                    .filter(p -> p.getStatus() != null && p.getStatus() == statusCode)
                    .collect(Collectors.toList());
        }

        // Summary stats
        BigDecimal totalPaid = payments.stream()
                .filter(p -> p.getStatus() != null && p.getStatus() == 1)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime now = LocalDateTime.now();
        BigDecimal paidThisMonth = payments.stream()
                .filter(p -> p.getStatus() != null && p.getStatus() == 1
                        && p.getPaymentDate() != null
                        && p.getPaymentDate().getYear() == now.getYear()
                        && p.getPaymentDate().getMonthValue() == now.getMonthValue())
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingOrFailed = payments.stream()
                .filter(p -> p.getStatus() != null && p.getStatus() != 1)
                .count();

        model.addAttribute("payments", payments);
        model.addAttribute("totalPaid", totalPaid);
        model.addAttribute("paidThisMonth", paidThisMonth);
        model.addAttribute("pendingOrFailed", pendingOrFailed);
        model.addAttribute("activeStatus", status);

        return "resident/payment/payment";
    }
}
