package com.quan.apartment_building_management_system.service;

import com.quan.apartment_building_management_system.entity.*;
import com.quan.apartment_building_management_system.repository.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLink;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;
import vn.payos.model.v2.paymentRequests.PaymentLinkStatus;
import vn.payos.model.webhooks.WebhookData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
     */
    public String createPaymentLink(Integer billId, Integer accountId) throws Exception {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found: " + billId));

        if (bill.getStatus() != 0) {
            throw new IllegalStateException("Bill is already paid or overdue-locked.");
        }

        long orderCode = System.currentTimeMillis() / 1000L;
        long amount = bill.getTotalAmount().longValue();

        if (amount < 2000) {
            throw new IllegalStateException("PayOS requires a minimum payment amount of 2000 VND.");
        }

        // PayOS description: alphanumeric only, max 25 characters
        String description = ("Bill" + bill.getBillId()).replaceAll("[^a-zA-Z0-9]", "");
        if (description.length() > 25) {
            description = description.substring(0, 25);
        }

        PaymentLinkItem item = PaymentLinkItem.builder()
                .name(description)
                .quantity(1)
                .price(amount)
                .build();

        CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount(amount)
                .description(description)
                .item(item)
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .build();

        CreatePaymentLinkResponse response = payOS.paymentRequests().create(paymentData);

        saveInitialPaymentRecord(bill, accountId, String.valueOf(orderCode), new BigDecimal(amount));

        return response.getCheckoutUrl();
    }

    private void saveInitialPaymentRecord(Bill bill, Integer accountId, String orderCode, BigDecimal amount) {
        boolean exists = paymentRepository.findByTransactionCode(orderCode).isPresent();
        if (exists) {
            return;
        }

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
        payment.setStatus((byte) 0);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    /**
     * Processes the PayOS webhook callback.
     * Verifies the signature via PayOS SDK, then updates Bill and Payment status.
     */
    @Transactional
    public void processWebhook(Map<String, Object> webhookBody) throws Exception {
        WebhookData webhookData = payOS.webhooks().verify(webhookBody);

        long orderCode = webhookData.getOrderCode();
        if ("00".equals(webhookData.getCode())) {
            markPaymentSuccess(String.valueOf(orderCode));
        } else {
            markPaymentFailed(String.valueOf(orderCode));
        }
    }

    /**
     * Confirms payment when PayOS redirects the user back to returnUrl (no webhook/ngrok needed).
     * PayOS appends orderCode and code=00 to the success URL.
     */
    @Transactional
    public void confirmPaymentFromReturn(long orderCode, String code) throws Exception {
        if (!"00".equals(code)) {
            return;
        }

        try {
            PaymentLink paymentLink = payOS.paymentRequests().get(orderCode);
            PaymentLinkStatus status = paymentLink.getStatus();
            if (status == PaymentLinkStatus.CANCELLED || status == PaymentLinkStatus.FAILED) {
                return;
            }
        } catch (Exception e) {
            System.err.println("[PayOS Return Verify Warning] " + e.getMessage());
        }

        markPaymentSuccess(String.valueOf(orderCode));
    }

    /**
     * Marks payment as cancelled when user is redirected to cancelUrl.
     */
    @Transactional
    public void confirmPaymentCancelled(long orderCode) {
        markPaymentFailed(String.valueOf(orderCode));
    }

    private void markPaymentSuccess(String orderCode) {
        paymentRepository.findByTransactionCode(orderCode).ifPresent(payment -> {
            if (payment.getStatus() != null && payment.getStatus() == 1) {
                return;
            }

            payment.setStatus((byte) 1);
            payment.setPaymentDate(LocalDateTime.now());

            Bill bill = payment.getBill();
            if (bill != null && bill.getStatus() != null && bill.getStatus() == 0) {
                bill.setStatus((byte) 1);
                bill.setPaidDate(LocalDateTime.now());
                billRepository.save(bill);
            }

            paymentRepository.save(payment);
        });
    }

    private void markPaymentFailed(String orderCode) {
        paymentRepository.findByTransactionCode(orderCode).ifPresent(payment -> {
            if (payment.getStatus() != null && payment.getStatus() == 1) {
                return;
            }
            payment.setStatus((byte) 2);
            paymentRepository.save(payment);
        });
    }
}
