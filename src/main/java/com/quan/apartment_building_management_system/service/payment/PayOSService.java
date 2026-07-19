package com.quan.apartment_building_management_system.service.payment;

import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.Apartment;
import com.quan.apartment_building_management_system.entity.Bill;
import com.quan.apartment_building_management_system.entity.Payment;
import com.quan.apartment_building_management_system.entity.PaymentMethod;
import com.quan.apartment_building_management_system.repository.AccountRepository;
import com.quan.apartment_building_management_system.repository.BillRepository;
import com.quan.apartment_building_management_system.repository.PaymentMethodRepository;
import com.quan.apartment_building_management_system.repository.PaymentRepository;
import com.quan.apartment_building_management_system.repository.UtilityMembershipRepository;
import com.quan.apartment_building_management_system.repository.UtilityPriceRepository;
import com.quan.apartment_building_management_system.repository.UtilityRepository;
import com.quan.apartment_building_management_system.repository.ProfileRepository;
import com.quan.apartment_building_management_system.repository.UtilityBookingRepository;
import com.quan.apartment_building_management_system.repository.NotificationRepository;
import com.quan.apartment_building_management_system.repository.AccountNotificationRepository;
import com.quan.apartment_building_management_system.entity.UtilityMembership;
import com.quan.apartment_building_management_system.entity.UtilityPrice;
import com.quan.apartment_building_management_system.entity.Utility;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.entity.UtilityBooking;
import com.quan.apartment_building_management_system.entity.Notification;
import com.quan.apartment_building_management_system.entity.AccountNotification;
import com.quan.apartment_building_management_system.dto.utility.BookingRequestDTO;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final UtilityMembershipRepository utilityMembershipRepository;
    private final UtilityPriceRepository utilityPriceRepository;
    private final UtilityRepository utilityRepository;
    private final ProfileRepository profileRepository;
    private final UtilityBookingRepository utilityBookingRepository;
    private final NotificationRepository notificationRepository;
    private final AccountNotificationRepository accountNotificationRepository;
    private final com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService;

    @Value("${payos.return-url}")
    private String returnUrl;

    @Value("${payos.cancel-url}")
    private String cancelUrl;

    public PayOSService(PayOS payOS,
            BillRepository billRepository,
            PaymentRepository paymentRepository,
            PaymentMethodRepository paymentMethodRepository,
            AccountRepository accountRepository,
            UtilityMembershipRepository utilityMembershipRepository,
            UtilityPriceRepository utilityPriceRepository,
            UtilityRepository utilityRepository,
            ProfileRepository profileRepository,
            UtilityBookingRepository utilityBookingRepository,
            NotificationRepository notificationRepository,
            AccountNotificationRepository accountNotificationRepository,
            com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService,
            com.quan.apartment_building_management_system.service.system.NotificationService notificationService) {
        this.payOS = payOS;
        this.billRepository = billRepository;
        this.paymentRepository = paymentRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.accountRepository = accountRepository;
        this.utilityMembershipRepository = utilityMembershipRepository;
        this.utilityPriceRepository = utilityPriceRepository;
        this.utilityRepository = utilityRepository;
        this.profileRepository = profileRepository;
        this.utilityBookingRepository = utilityBookingRepository;
        this.notificationRepository = notificationRepository;
        this.accountNotificationRepository = accountNotificationRepository;
        this.systemLogService = systemLogService;
        this.notificationService = notificationService;
    }

    private final com.quan.apartment_building_management_system.service.system.NotificationService notificationService;

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

        long expiredAt = (System.currentTimeMillis() / 1000L) + (2 * 60); // Link expires in 10 minutes

        CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount(amount)
                .description(description)
                .item(item)
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .expiredAt(expiredAt)
                .build();

        CreatePaymentLinkResponse response = payOS.paymentRequests().create(paymentData);

        saveInitialPaymentRecord(bill, accountId, String.valueOf(orderCode), new BigDecimal(amount));

        return response.getCheckoutUrl();
    }

    /**
     * Creates a PayOS payment link for a Utility Membership.
     * Generates a temporary UtilityMembership with PaymentStatus = false.
     * Uses orderCode >= 900000000000L to differentiate from bill payments.
     */
    public String createMembershipPaymentLink(Integer utilityId, Integer utilityPriceId, Integer accountId)
            throws Exception {
        Profile profile = profileRepository.findByAccountAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        Utility utility = utilityRepository.findById(utilityId)
                .orElseThrow(() -> new IllegalArgumentException("Utility not found"));

        UtilityPrice price = utilityPriceRepository.findById(utilityPriceId)
                .orElseThrow(() -> new IllegalArgumentException("Price not found"));

        // Create pending membership
        UtilityMembership membership = new UtilityMembership();
        membership.setProfile(profile);
        membership.setUtility(utility);
        membership.setUtilityPrice(price);
        membership.setStartDate(LocalDate.now());

        String unitName = price.getUnit().getUnitName().toLowerCase();
        if (unitName.contains("hour") || unitName.contains("giờ") || unitName.contains("day") || unitName.contains("ngày")) {
            membership.setEndDate(LocalDate.now());
        } else if (unitName.contains("month") || unitName.contains("tháng")) {
            membership.setEndDate(LocalDate.now().plusMonths(1));
        } else {
            membership.setEndDate(LocalDate.now().plusYears(1));
        }

        membership.setStatus(false);
        membership.setPaymentStatus(false);
        membership.setCreatedAt(LocalDateTime.now());
        membership = utilityMembershipRepository.save(membership);

        long orderCode = 900000000000L + membership.getMembershipId();
        long amount = price.getPrice().longValue();

        if (amount < 2000) {
            throw new IllegalStateException("PayOS requires a minimum payment amount of 2000 VND.");
        }

        String description = ("Pkg " + utility.getUtilityName()).replaceAll("[^a-zA-Z0-9 ]", "");
        if (description.length() > 25) {
            description = description.substring(0, 25);
        }

        PaymentLinkItem item = PaymentLinkItem.builder()
                .name(description)
                .quantity(1)
                .price(amount)
                .build();

        long expiredAt = (System.currentTimeMillis() / 1000L) + (2 * 60);

        CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount(amount)
                .description(description)
                .item(item)
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .expiredAt(expiredAt)
                .build();

        CreatePaymentLinkResponse response = payOS.paymentRequests().create(paymentData);

        return response.getCheckoutUrl();
    }

    public String createBookingPaymentLink(Integer bookingId) throws Exception {
        UtilityBooking booking = utilityBookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        long orderCode = 800000000000L + booking.getBookingId();
        long amount = booking.getTotalPrice().longValue();

        if (amount < 2000) {
            throw new IllegalStateException("PayOS requires a minimum payment amount of 2000 VND.");
        }

        String description = ("Book " + booking.getResource().getResourceName()).replaceAll("[^a-zA-Z0-9 ]", "");
        if (description.length() > 25) {
            description = description.substring(0, 25);
        }

        PaymentLinkItem item = PaymentLinkItem.builder()
                .name(description)
                .quantity(1)
                .price(amount)
                .build();

        long expiredAt = (System.currentTimeMillis() / 1000L) + (5 * 60); // 5 minutes expiration

        CreatePaymentLinkRequest paymentData = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount(amount)
                .description(description)
                .item(item)
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .expiredAt(expiredAt)
                .build();

        CreatePaymentLinkResponse response = payOS.paymentRequests().create(paymentData);
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
     * Confirms payment when PayOS redirects the user back to returnUrl (no
     * webhook/ngrok needed).
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
        long code = Long.parseLong(orderCode);
        if (code >= 900000000000L) {
            Integer membershipId = (int) (code - 900000000000L);
            utilityMembershipRepository.findById(membershipId).ifPresent(membership -> {
                if (membership.getPaymentStatus() != null && membership.getPaymentStatus()) {
                    return;
                }
                membership.setPaymentStatus(true);
                membership.setStatus(true);
                utilityMembershipRepository.save(membership);
            });
            return;
        } else if (code >= 800000000000L) {
            Integer bookingId = (int) (code - 800000000000L);
            utilityBookingRepository.findById(bookingId).ifPresent(booking -> {
                if (booking.getPaymentStatus() != null && booking.getPaymentStatus()) {
                    return;
                }
                booking.setPaymentStatus(true);
                // Status remains unchanged (e.g. 0 - Pending) so manager can approve it later
                utilityBookingRepository.save(booking);
                
                // Send notifications (In-app and Email)
                Account residentAccount = booking.getProfile() != null ? booking.getProfile().getAccount() : null;
                if (residentAccount != null) {
                    sendBookingSuccessNotification(residentAccount, booking);
                    if (booking.getProfile().getEmail() != null && !booking.getProfile().getEmail().isEmpty()) {
                        String resourceName = booking.getResource() != null ? booking.getResource().getResourceName() : "Tiện ích";
                        String amount = String.format("%,.0f", booking.getTotalPrice() != null ? booking.getTotalPrice() : 0.0);
                        notificationService.sendBookingSuccessEmail(booking.getProfile().getEmail(), resourceName, booking.getStartTime().toString(), amount);
                    }
                }
            });
            return;
        }

        paymentRepository.findByTransactionCode(orderCode).ifPresent(payment -> {
            if (payment.getStatus() != null && payment.getStatus() == 1) {
                return;
            }

            com.quan.apartment_building_management_system.dto.systemlog.PaymentLogDTO oldDto =
                    com.quan.apartment_building_management_system.dto.systemlog.PaymentLogDTO.fromEntity(payment);

            payment.setStatus((byte) 1);
            payment.setPaymentDate(LocalDateTime.now());

            Bill bill = payment.getBill();
            if (bill != null && bill.getStatus() != null && bill.getStatus() == 0) {
                bill.setStatus((byte) 1);
                bill.setPaidDate(LocalDateTime.now());
                billRepository.save(bill);

                Account residentAccount = payment.getPaidBy();
                if (residentAccount != null) {
                    sendBillPaymentSuccessNotification(residentAccount, bill);
                }
            }

            paymentRepository.save(payment);

            com.quan.apartment_building_management_system.dto.systemlog.PaymentLogDTO newDto =
                    com.quan.apartment_building_management_system.dto.systemlog.PaymentLogDTO.fromEntity(payment);
            systemLogService.logSystemAction("PAYMENT_BILL", "Payment", payment.getPaymentId(), oldDto, newDto,
                    "Bill payment completed for bill #" + (bill != null ? bill.getBillId() : "?"));
        });
    }

    private void sendBillPaymentSuccessNotification(Account residentAccount, Bill bill) {
        try {
            Account sender = accountRepository.findById(1).orElse(residentAccount);

            Notification notification = new Notification();
            notification.setTitle("Thanh toán hóa đơn thành công");

            String content = String.format(
                    "Hóa đơn kỳ tháng %d/%d cho căn hộ %s đã được thanh toán thành công số tiền %,.0f VNĐ.",
                    bill.getBillMonth(),
                    bill.getBillYear(),
                    bill.getApartment().getApartmentNumber(),
                    bill.getTotalAmount().doubleValue());
            notification.setContent(content);
            notification.setNotificationType((byte) 3); // 3: Hóa đơn
            notification.setCreatedBy(sender);
            notification.setRelatedEntityType("Bill");
            notification.setReceiver(residentAccount);
            notification.setRecipient("RESIDENT");
            notification.setCreatedAt(LocalDateTime.now());

            notification = notificationRepository.save(notification);

            AccountNotification accountNotification = new AccountNotification();
            accountNotification.setNotification(notification);
            accountNotification.setAccount(residentAccount);
            accountNotification.setIsRead(false);
            accountNotification.setReadAt(null);

            accountNotificationRepository.save(accountNotification);
        } catch (Exception e) {
            System.err.println("[Notification Error] Failed to create payment success notification: " + e.getMessage());
        }
    }

    private void sendBookingSuccessNotification(Account residentAccount, com.quan.apartment_building_management_system.entity.UtilityBooking booking) {
        try {
            Account sender = accountRepository.findById(1).orElse(residentAccount);

            Notification notification = new Notification();
            notification.setTitle("Thanh toán đặt lịch thành công");

            String resourceName = booking.getResource() != null ? booking.getResource().getResourceName() : "Tiện ích";
            String content = String.format(
                    "Đơn đặt lịch %s vào ngày %s đã được thanh toán thành công.",
                    resourceName,
                    booking.getStartTime().toString());
            notification.setContent(content);
            notification.setNotificationType((byte) 2); // 2: Utility Booking
            notification.setCreatedBy(sender);
            notification.setRelatedEntityType("UtilityBooking");
            notification.setReceiver(residentAccount);
            notification.setRecipient("RESIDENT");
            notification.setCreatedAt(LocalDateTime.now());

            notification = notificationRepository.save(notification);

            AccountNotification accountNotification = new AccountNotification();
            accountNotification.setNotification(notification);
            accountNotification.setAccount(residentAccount);
            accountNotification.setIsRead(false);
            accountNotification.setReadAt(null);

            accountNotificationRepository.save(accountNotification);
        } catch (Exception e) {
            System.err.println("[Notification Error] Failed to create booking success notification: " + e.getMessage());
        }
    }

    public Integer getResourceIdByMembershipOrderCode(String orderCode) {
        try {
            long code = Long.parseLong(orderCode);
            if (code >= 900000000000L) {
                Integer membershipId = (int) (code - 900000000000L);
                return utilityMembershipRepository.findById(membershipId)
                        .map(m -> m.getUtilityPrice())
                        .map(p -> p.getResource())
                        .map(r -> r.getResourceId())
                        .orElse(null);
            }
        } catch (Exception e) {
            // Ignore format exception or null
        }
        return null;
    }

    public BookingRequestDTO getBookingRequestByOrderCode(String orderCode) {
        try {
            long code = Long.parseLong(orderCode);
            if (code >= 800000000000L && code < 900000000000L) {
                Integer bookingId = (int) (code - 800000000000L);
                return utilityBookingRepository.findById(bookingId).map(b -> {
                    BookingRequestDTO req = new BookingRequestDTO();
                    req.setUtilityId(b.getResource().getUtility().getUtilityId());
                    req.setResourceId(b.getResource().getResourceId());
                    req.setPriceId(b.getUtilityPrice().getUtilityPriceId());
                    req.setBookingDate(b.getStartTime().toLocalDate());
                    req.setStartTime(b.getStartTime().toLocalTime());
                    req.setEndTime(b.getEndTime().toLocalTime());
                    req.setPaymentMethod("ONLINE");
                    return req;
                }).orElse(null);
            }
        } catch (Exception e) {
            // Ignore format exception or null
        }
        return null;
    }

    private void markPaymentFailed(String orderCode) {
        long code = Long.parseLong(orderCode);
        if (code >= 900000000000L) {
            Integer membershipId = (int) (code - 900000000000L);
            // User cancelled the payment -> delete the pending membership
            utilityMembershipRepository.findById(membershipId).ifPresent(membership -> {
                utilityMembershipRepository.delete(membership);
            });
            return;
        } else if (code >= 800000000000L) {
            Integer bookingId = (int) (code - 800000000000L);
            // User cancelled the payment -> delete the pending booking
            utilityBookingRepository.findById(bookingId).ifPresent(booking -> {
                utilityBookingRepository.delete(booking);
            });
            return;
        }

        paymentRepository.findByTransactionCode(orderCode).ifPresent(payment -> {
            if (payment.getStatus() != null && payment.getStatus() == 1) {
                return;
            }
            payment.setStatus((byte) 2);
            paymentRepository.save(payment);
        });
    }

    public Integer getBillIdByOrderCode(String orderCode) {
        return paymentRepository.findByTransactionCode(orderCode)
                .map(payment -> payment.getBill() != null ? payment.getBill().getBillId() : null)
                .orElse(null);
    }

    /**
     * Synchronizes pending payments of an apartment with PayOS.
     * Marks expired or cancelled links as FAILED (status 2).
     */
    @Transactional
    public void syncPendingPaymentsForApartment(Integer apartmentId) {
        List<Payment> pendingPayments = paymentRepository
                .findByBillApartmentApartmentIdOrderByPaymentDateDesc(apartmentId);
        if (pendingPayments == null || pendingPayments.isEmpty()) {
            return;
        }

        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);

        for (Payment p : pendingPayments) {
            if (p.getStatus() == null || p.getStatus() != 0) {
                continue;
            }

            // If it is older than 10 minutes, mark as failed directly to avoid unnecessary
            // API calls
            if (p.getPaymentDate() != null && p.getPaymentDate().isBefore(tenMinutesAgo)) {
                markPaymentFailed(p.getTransactionCode());
                continue;
            }

            try {
                long orderCode = Long.parseLong(p.getTransactionCode());
                PaymentLink paymentLink = payOS.paymentRequests().get(orderCode);
                String statusStr = paymentLink.getStatus().toString();

                if ("PAID".equals(statusStr)) {
                    markPaymentSuccess(p.getTransactionCode());
                } else if ("CANCELLED".equals(statusStr) || "EXPIRED".equals(statusStr) || "FAILED".equals(statusStr)) {
                    markPaymentFailed(p.getTransactionCode());
                }
            } catch (Exception e) {
                System.err.println(
                        "[PayOS Sync Warning] For orderCode " + p.getTransactionCode() + ": " + e.getMessage());
            }
        }
    }
}
