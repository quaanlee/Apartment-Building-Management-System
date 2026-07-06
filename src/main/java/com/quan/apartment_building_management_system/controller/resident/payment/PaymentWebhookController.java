package com.quan.apartment_building_management_system.controller.resident.payment;

import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.service.payment.PayOSService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

/**
 * PayOS Webhook & Payment Redirect Controller
 */
@Controller
@RequestMapping("/payment")
public class PaymentWebhookController {

    private final PayOSService payOSService;

    public PaymentWebhookController(PayOSService payOSService) {
        this.payOSService = payOSService;
    }

    /**
     * Webhook endpoint called by PayOS after payment success/failure.
     * Must return 200 OK immediately — PayOS will retry if it doesn't receive a response.
     */
    @ResponseBody
    @PostMapping("/webhook")
    public ResponseEntity<Map<String, String>> handleWebhook(@RequestBody(required = false) Map<String, Object> body) {
        if (body != null) {
            try {
                payOSService.processWebhook(body);
            } catch (Exception e) {
                System.err.println("[PayOS Webhook Error] " + e.getMessage());
            }
        }
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    /**
     * Resident initiates payment for a bill.
     * Creates a PayOS payment link and redirects to PayOS checkout page.
     */
    @GetMapping("/bill/{billId}")
    public String initiatePayment(@PathVariable Integer billId,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        try {
            String checkoutUrl = payOSService.createPaymentLink(billId, currentUser.getAccountId());
            return "redirect:" + checkoutUrl;
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Payment failed: " + e.getMessage());
            return "redirect:/resident/payment";
        }
    }

    /**
     * Return page after successful payment — redirected here by PayOS with ?orderCode=...&code=00
     */
    @GetMapping("/success")
    public String paymentSuccess(@RequestParam(required = false) Long orderCode,
                                 @RequestParam(required = false) String code,
                                 RedirectAttributes redirectAttributes) {
        Integer billId = null;
        if (orderCode != null) {
            try {
                payOSService.confirmPaymentFromReturn(orderCode, code);
                billId = payOSService.getBillIdByOrderCode(String.valueOf(orderCode));
            } catch (Exception e) {
                System.err.println("[PayOS Success Return Error] " + e.getMessage());
            }
        }
        redirectAttributes.addFlashAttribute("message", "Thanh toán hóa đơn thành công!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        if (billId != null) {
            return "redirect:/resident/billing/detail/" + billId;
        }
        return "redirect:/resident/billing";
    }

    /**
     * Return page when user cancels on PayOS checkout — redirected here by PayOS with ?orderCode=...
     */
    @GetMapping("/cancel")
    public String paymentCancel(@RequestParam(required = false) Long orderCode,
                                RedirectAttributes redirectAttributes) {
        Integer billId = null;
        if (orderCode != null) {
            try {
                payOSService.confirmPaymentCancelled(orderCode);
                billId = payOSService.getBillIdByOrderCode(String.valueOf(orderCode));
            } catch (Exception e) {
                System.err.println("[PayOS Cancel Return Error] " + e.getMessage());
            }
        }
        redirectAttributes.addFlashAttribute("message", "Giao dịch thanh toán đã bị hủy.");
        redirectAttributes.addFlashAttribute("messageType", "warning");
        if (billId != null) {
            return "redirect:/resident/billing/detail/" + billId;
        }
        return "redirect:/resident/billing";
    }
}
