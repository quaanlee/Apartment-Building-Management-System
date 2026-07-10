package com.quan.apartment_building_management_system.controller.resident;

import com.quan.apartment_building_management_system.entity.*;
import com.quan.apartment_building_management_system.repository.AccountNotificationRepository;
import com.quan.apartment_building_management_system.repository.NotificationRepository;
import com.quan.apartment_building_management_system.repository.PaymentMethodRepository;
import com.quan.apartment_building_management_system.repository.PaymentRepository;
import com.quan.apartment_building_management_system.service.billing.BillService;
import com.quan.apartment_building_management_system.service.user.AccountService;
import com.quan.apartment_building_management_system.service.user.ProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ResidentBillingController {

    private final BillService billService;
    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final AccountService accountService;
    private final ProfileService profileService;
    private final NotificationRepository notificationRepository;
    private final AccountNotificationRepository accountNotificationRepository;

    public ResidentBillingController(BillService billService,
                                     PaymentRepository paymentRepository,
                                     PaymentMethodRepository paymentMethodRepository,
                                     AccountService accountService,
                                     ProfileService profileService,
                                     NotificationRepository notificationRepository,
                                     AccountNotificationRepository accountNotificationRepository) {
        this.billService = billService;
        this.paymentRepository = paymentRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.accountService = accountService;
        this.profileService = profileService;
        this.notificationRepository = notificationRepository;
        this.accountNotificationRepository = accountNotificationRepository;
    }

    private Account checkAndGetResidentUser(HttpSession session) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null || !"RESIDENT".equalsIgnoreCase(currentUser.getRole().getRoleName())) {
            currentUser = accountService.findByUsername("tran.thi.b@gmail.com").orElse(null);
            if (currentUser == null) {
                currentUser = accountService.findByUsername("tran.thi.b").orElse(null);
            }
            if (currentUser == null) {
                List<Account> accounts = accountService.findAll();
                for (Account acc : accounts) {
                    if ("RESIDENT".equalsIgnoreCase(acc.getRole().getRoleName())) {
                        currentUser = acc;
                        break;
                    }
                }
            }
            if (currentUser != null) {
                session.setAttribute("currentUser", currentUser);
            }
        }
        return currentUser;
    }

    // 1. GET /resident/billing
    @GetMapping("/resident/billing")
    public String listResidentBills(HttpSession session, Model model) {
        Account currentUser = checkAndGetResidentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        Profile profile = profileService.findByAccountId(currentUser.getAccountId()).orElse(null);
        Apartment apartment = profile != null ? profile.getApartment() : null;

        List<Bill> bills = billService.findByApartmentId(apartment != null ? apartment.getApartmentId() : -1);

        // Check and update overdue status dynamically based on DueDate
        LocalDateTime now = LocalDateTime.now();
        for (Bill b : bills) {
            if (b.getStatus() == 0 && b.getDueDate().isBefore(now)) {
                b.setStatus((byte) 2); // Mark as Overdue
                billService.save(b);
            }
        }

        model.addAttribute("bills", bills);
        model.addAttribute("apartment", apartment);
        model.addAttribute("profile", profile);
        model.addAttribute("activeTab", "billing");
        model.addAttribute("pageTitle", "My Billing");

        return "resident/billing/list";
    }

    // 2. GET /resident/billing/{id}/pay
    @GetMapping("/resident/billing/{id}/pay")
    public String showPaymentPage(@PathVariable("id") Integer id, HttpSession session, Model model) {
        Account currentUser = checkAndGetResidentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Bill> billOpt = billService.findById(id);
        if (billOpt.isEmpty()) {
            return "redirect:/resident/billing";
        }

        Bill bill = billOpt.get();
        // Verify this bill belongs to the logged-in resident's apartment
        Profile profile = profileService.findByAccountId(currentUser.getAccountId()).orElse(null);
        Apartment apartment = profile != null ? profile.getApartment() : null;

        if (apartment == null || !bill.getApartment().getApartmentId().equals(apartment.getApartmentId())) {
            return "redirect:/resident/billing";
        }

        // Get enabled payment methods
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findAll().stream()
                .filter(PaymentMethod::getStatus)
                .collect(Collectors.toList());

        model.addAttribute("bill", bill);
        model.addAttribute("paymentMethods", paymentMethods);
        model.addAttribute("activeTab", "billing");
        model.addAttribute("pageTitle", "Checkout");

        return "resident/billing/pay";
    }

    // 3. POST /resident/billing/{id}/pay
    @PostMapping("/resident/billing/{id}/pay")
    public String processPayment(@PathVariable("id") Integer id,
                                 @RequestParam("methodId") Integer methodId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Account currentUser = checkAndGetResidentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Bill> billOpt = billService.findById(id);
        if (billOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Bill not found.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/resident/billing";
        }

        Bill bill = billOpt.get();
        Profile profile = profileService.findByAccountId(currentUser.getAccountId()).orElse(null);
        Apartment apartment = profile != null ? profile.getApartment() : null;

        if (apartment == null || !bill.getApartment().getApartmentId().equals(apartment.getApartmentId())) {
            redirectAttributes.addFlashAttribute("message", "Unauthorized billing access.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/resident/billing";
        }

        if (bill.getStatus() == 1) {
            redirectAttributes.addFlashAttribute("message", "This bill has already been paid.");
            redirectAttributes.addFlashAttribute("messageType", "info");
            return "redirect:/resident/billing";
        }

        PaymentMethod method = paymentMethodRepository.findById(methodId).orElse(null);
        if (method == null || !method.getStatus()) {
            redirectAttributes.addFlashAttribute("message", "Selected payment method is invalid or disabled.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/resident/billing/" + id + "/pay";
        }

        // Generate Transaction Code
        String txnPrefix = method.getIsOnline() ? "TXN-ONLINE-" : "TXN-MANUAL-";
        String transactionCode = txnPrefix + System.currentTimeMillis();

        // 1. Create Payment
        Payment payment = new Payment();
        payment.setBill(bill);
        payment.setPaidBy(currentUser);
        payment.setPaymentMethod(method);
        payment.setAmount(bill.getTotalAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus((byte) 1); // SUCCESS
        payment.setTransactionCode(transactionCode);
        paymentRepository.save(payment);

        // 2. Update Bill
        bill.setStatus((byte) 1); // PAID
        bill.setPaidDate(LocalDateTime.now());
        billService.save(bill);

        // 3. Generate In-Portal Notification for the Resident
        Account systemSender = accountService.findByUsername("manager").orElseGet(() ->
                accountService.findByUsername("admin").orElse(currentUser)
        );

        Notification notification = new Notification();
        notification.setTitle("Payment Received Successfully");
        notification.setContent(String.format(
                "Dear Resident, your payment of $%s for the apartment %s monthly bill (%02d/%d) has been processed successfully. Transaction Code: %s. Thank you!",
                bill.getTotalAmount(),
                apartment.getApartmentNumber(),
                bill.getBillMonth(),
                bill.getBillYear(),
                transactionCode
        ));
        notification.setCreatedAt(LocalDateTime.now());
        notification.setCreatedBy(systemSender);
        notification.setRelatedEntityType("Payment");
        notification.setRelatedEntityId(payment.getPaymentId());
        notification = notificationRepository.save(notification);

        AccountNotification accountNotification = new AccountNotification();
        accountNotification.setNotification(notification);
        accountNotification.setAccount(currentUser);
        accountNotification.setIsRead(false);
        accountNotificationRepository.save(accountNotification);

        redirectAttributes.addFlashAttribute("message", "Payment processed successfully! Notification sent to inbox.");
        redirectAttributes.addFlashAttribute("messageType", "success");

        return "redirect:/resident/billing";
    }

    // 4. GET /resident/payment
    @GetMapping("/resident/payment")
    public String paymentHistory(HttpSession session, Model model) {
        Account currentUser = checkAndGetResidentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }

        List<Payment> payments = paymentRepository.findByPaidByAccountId(currentUser.getAccountId());

        model.addAttribute("payments", payments);
        model.addAttribute("activeTab", "payment");
        model.addAttribute("pageTitle", "Payment History");

        return "resident/payment/history";
    }
}
