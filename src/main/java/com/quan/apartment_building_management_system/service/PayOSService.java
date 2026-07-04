package com.quan.apartment_building_management_system.service;

import com.quan.apartment_building_management_system.entity.*;
import com.quan.apartment_building_management_system.repository.*;

import lombok.NonNull;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.type.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * PayOSService
 * Handles creation of PayOS payment links and processing of webhook callbacks.
 */
@Service
public class PayOSService {

    private final PayOS payOS;
    private final BillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final AccountRepository accountRepository;

    @Value("${payos.return-url}")
    private String returnUrl;

    @Value("${payos.cancel-url}")
    private String cancelUrl;

    public PayOSService(PayOS payOS,
            BillRepository billRepository,
            PaymentRepository paymentRepository,
            PaymentMethodRepository paymentMethodRepository,
            AccountRepository accountRepository) {
        this.payOS = payOS;
        this.billRepository = billRepository;
        this.paymentRepository = paymentRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Creates a PayOS payment link for the given bill.
     * Returns the checkout URL to redirect the resident to.
     *
     * @param billId    ID of the bill to pay
     * @param accountId ID of the resident's account
     * @return PayOS checkout URL string
     */
    public String createPaymentLink(Integer billId, Integer accountId) throws Exception {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found: " + billId));

        if (bill.getStatus() != 0) {
            throw new IllegalStateException("Bill is already paid or overdue-locked.");
        }

        // Generate a completely unique order code using timestamp to prevent PayOS duplicate orderCode errors
        long orderCode = System.currentTimeMillis();

        // Amount must be in VND (integer, no decimal)
        int amount = bill.getTotalAmount().intValue();

        String description = "Bill T" + bill.getBillMonth() + "/" + bill.getBillYear()
                + " - " + bill.getApartment().getApartmentNumber();

        // Build item list for PayOS checkout page display
        List<ItemData> items = new ArrayList<>();
        items.add(ItemData.builder()
                .name(description)
                .quantity(1)
                .price(amount)
                .build());

        // Create PayOS payment data
        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .amount(amount)
                .description(description)
                .items(items)
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .build();

        // Call PayOS API
        CheckoutResponseData response = payOS.createPaymentLink(paymentData);

        // Save a PENDING Payment record immediately so we can track it
        saveInitialPaymentRecord(bill, accountId, String.valueOf(orderCode), new BigDecimal(amount));

        return response.getCheckoutUrl();
    }

    /**
     * Saves an initial PENDING payment record to the DB when a payment link is
     * created.
     */
    private void saveInitialPaymentRecord(Bill bill, Integer accountId, String orderCode, BigDecimal amount) {
        // Avoid duplicate pending records for the same bill
        boolean exists = paymentRepository.findByTransactionCode(orderCode).isPresent();
        if (exists)
            return;

        PaymentMethod payOSMethod = paymentMethodRepository.findByMethodName("PayOS")
                .orElseGet(() -> {
                    PaymentMethod m = new PaymentMethod();
                    m.setMethodName("PayOS");
                    m.setIsOnline(true);
                    m.setStatus(true);
                    return paymentMethodRepository.save(m);
                });

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountId));

        Payment payment = new Payment();
        payment.setBill(bill);
        payment.setPaidBy(account);
        payment.setPaymentMethod(payOSMethod);
        payment.setTransactionCode(orderCode);
        payment.setAmount(amount);
        payment.setStatus((byte) 0); // PENDING
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    /**
     * Processes the PayOS webhook callback.
     * Verifies the signature via PayOS SDK, then updates Bill and Payment status.
     *
     * @param webhookBody Raw webhook payload from PayOS as Map
     */
    @Transactional
    public void processWebhook(Map<String, Object> webhookBody) throws Exception {
        // Build Webhook object from raw map fields for SDK 1.0.3
        String code = String.valueOf(webhookBody.getOrDefault("code", ""));
        String desc = String.valueOf(webhookBody.getOrDefault("desc", ""));
        String signature = String.valueOf(webhookBody.getOrDefault("signature", ""));
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) webhookBody.get("data");

        Webhook webhook = Webhook.builder()
                .code(code)
                .desc(desc)
                .data((@NonNull WebhookData) data)
                .signature(signature)
                .build();

        WebhookData webhookData = payOS.verifyPaymentWebhookData(webhook);

        long orderCode = webhookData.getOrderCode();
        // PayOS sends code "00" for success
        boolean isSuccess = "00".equals(String.valueOf(webhookBody.get("code")));

        // Update Payment record to SUCCESS or FAILED and update associated Bill status
        paymentRepository.findByTransactionCode(String.valueOf(orderCode)).ifPresent(payment -> {
            payment.setStatus(isSuccess ? (byte) 1 : (byte) 2);
            if (isSuccess) {
                payment.setPaymentDate(LocalDateTime.now());
                
                // Update associated Bill to PAID (1)
                Bill bill = payment.getBill();
                if (bill != null) {
                    bill.setStatus((byte) 1);
                    bill.setPaidDate(LocalDateTime.now());
                    billRepository.save(bill);
                }
            }
            paymentRepository.save(payment);
        });
    }
}
