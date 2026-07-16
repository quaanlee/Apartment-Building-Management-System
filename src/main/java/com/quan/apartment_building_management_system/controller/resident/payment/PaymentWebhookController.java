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
            redirectAttributes.addFlashAttribute("error", "Thanh toán thất bại: " + e.getMessage());
            return "redirect:/resident/payment";
        }
    }

    /**
     * Resident initiates payment for a utility membership package.
     */
    @PostMapping("/membership/initiate")
    public String initiateMembershipPayment(@RequestParam Integer utilityId,
                                            @RequestParam Integer resourceId,
                                            @RequestParam Integer utilityPriceId,
                                            HttpSession session,
                                            RedirectAttributes redirectAttributes) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        try {
            String checkoutUrl = payOSService.createMembershipPaymentLink(utilityId, utilityPriceId, currentUser.getAccountId());
            return "redirect:" + checkoutUrl;
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Khởi tạo thanh toán thất bại: " + e.getMessage());
            return "redirect:/resident/utilities/resources/" + resourceId;
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
        boolean isMembership = false;
        boolean isBooking = false;
        if (orderCode != null) {
            try {
                if (orderCode >= 900000000000L) {
                    isMembership = true;
                } else if (orderCode >= 800000000000L) {
                    isBooking = true;
                }
                payOSService.confirmPaymentFromReturn(orderCode, code);
                billId = payOSService.getBillIdByOrderCode(String.valueOf(orderCode));
            } catch (Exception e) {
                System.err.println("[PayOS Success Return Error] " + e.getMessage());
            }
        }
        
        if (isBooking) {
            redirectAttributes.addFlashAttribute("successMessage", "Thanh toán đặt tiện ích thành công!");
            return "redirect:/resident/utilities/history";
        }
        
        if (isMembership) {
            Integer resourceId = null;
            if (orderCode != null) {
                resourceId = payOSService.getResourceIdByMembershipOrderCode(String.valueOf(orderCode));
            }
            redirectAttributes.addFlashAttribute("errorMessage", "Đăng ký gói thành viên thành công!");
            if (resourceId != null) {
                return "redirect:/resident/utilities/resources/" + resourceId;
            }
            return "redirect:/resident/utilities";
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
        boolean isMembership = false;
        boolean isBooking = false;
        if (orderCode != null) {
            try {
                if (orderCode >= 900000000000L) {
                    isMembership = true;
                } else if (orderCode >= 800000000000L) {
                    isBooking = true;
                }
                payOSService.confirmPaymentCancelled(orderCode);
                billId = payOSService.getBillIdByOrderCode(String.valueOf(orderCode));
            } catch (Exception e) {
                System.err.println("[PayOS Cancel Return Error] " + e.getMessage());
            }
        }
        if (isBooking) {
            com.quan.apartment_building_management_system.dto.utility.BookingRequestDTO req = null;
            if (orderCode != null) {
                req = payOSService.getBookingRequestByOrderCode(String.valueOf(orderCode));
            }
            payOSService.confirmPaymentCancelled(orderCode);
            
            redirectAttributes.addFlashAttribute("errorMessage", "Thanh toán bị hủy. Vui lòng thử lại!");
            if (req != null) {
                redirectAttributes.addFlashAttribute("bookingRequest", req);
                return "redirect:/resident/utilities/rebook";
            }
            return "redirect:/resident/utilities/history";
        }
        
        if (isMembership) {
            Integer resourceId = null;
            if (orderCode != null) {
                resourceId = payOSService.getResourceIdByMembershipOrderCode(String.valueOf(orderCode));
            }
            redirectAttributes.addFlashAttribute("errorMessage", "Thanh toán gói thành viên đã bị hủy!");
            if (resourceId != null) {
                return "redirect:/resident/utilities/resources/" + resourceId;
            }
            return "redirect:/resident/utilities";
        }

        redirectAttributes.addFlashAttribute("message", "Giao dịch đã bị hủy!");
        redirectAttributes.addFlashAttribute("messageType", "warning");
        if (billId != null) {
            return "redirect:/resident/billing/detail/" + billId;
        }
        return "redirect:/resident/billing";
    }
}
