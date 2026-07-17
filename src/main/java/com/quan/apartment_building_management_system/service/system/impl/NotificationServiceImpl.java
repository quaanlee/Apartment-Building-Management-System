package com.quan.apartment_building_management_system.service.system.impl;

import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.AccountNotification;
import com.quan.apartment_building_management_system.entity.Notification;
import com.quan.apartment_building_management_system.repository.AccountRepository;
import com.quan.apartment_building_management_system.repository.AccountNotificationRepository;
import com.quan.apartment_building_management_system.repository.NotificationRepository;
import com.quan.apartment_building_management_system.service.system.NotificationService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final AccountRepository accountRepository;
    private final AccountNotificationRepository accountNotificationRepository;
    private final com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService;

    public NotificationServiceImpl(NotificationRepository notificationRepository, 
                                   JavaMailSender mailSender,
                                   AccountRepository accountRepository,
                                   AccountNotificationRepository accountNotificationRepository,
                                   com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService) {
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
        this.accountRepository = accountRepository;
        this.accountNotificationRepository = accountNotificationRepository;
        this.systemLogService = systemLogService;
    }

    @Override
    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return notificationRepository.findById(id);
    }

    @Override
    @Transactional
    public Notification save(Notification notification) {
        boolean isNew = notification.getNotificationId() == null;
        Notification savedNotification = notificationRepository.save(notification);
        if (isNew) {
            distributeNotification(savedNotification);
        }
        return savedNotification;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        notificationRepository.deleteById(id);
    }
    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your OTP Verification Code | ABM System");
        message.setText("Hello,\n\n"
                + "You have requested to reset your password on the ABM System.\n"
                + "Your OTP verification code is: " + otp + "\n"
                + "This code will expire in 2 minutes.\n\n"
                + "If you did not make this request, please ignore this email.\n\n"
                + "Best regards,\n"
                + "ABM System Support Team");
        mailSender.send(message);
    }

    @Override
    public void sendBookingSuccessEmail(String toEmail, String resourceName, String bookingDate, String amount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Xác nhận thanh toán đặt lịch thành công | ABM System");
        message.setText("Xin chào,\n\n"
                + "Bạn đã thanh toán thành công cho đơn đặt lịch tiện ích của mình.\n\n"
                + "Thông tin đặt lịch:\n"
                + "- Tiện ích: " + resourceName + "\n"
                + "- Ngày đặt: " + bookingDate + "\n"
                + "- Số tiền thanh toán: " + amount + " VNĐ\n\n"
                + "Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi.\n\n"
                + "Trân trọng,\n"
                + "Ban quản lý tòa nhà");
        mailSender.send(message);
    }

    @Override
    public void sendBookingStatusUpdateNotification(com.quan.apartment_building_management_system.entity.UtilityBooking booking, Byte status) {
        try {
            if (status != 1 && status != 2) return; // Only notify for Approved/Rejected

            String statusStr = (status == 1) ? "được phê duyệt" : "bị từ chối";
            String title = (status == 1) ? "Yêu cầu đặt lịch được phê duyệt" : "Yêu cầu đặt lịch bị từ chối";
            String resourceName = booking.getResource() != null ? booking.getResource().getResourceName() : "Tiện ích";
            String bookingDate = booking.getStartTime() != null ? booking.getStartTime().toString() : "";

            // 1. In-app Notification
            Account residentAccount = booking.getProfile() != null ? booking.getProfile().getAccount() : null;
            if (residentAccount != null) {
                Notification notification = new Notification();
                notification.setTitle(title);
                String content = String.format("Đơn đặt lịch %s vào ngày %s của bạn đã %s.", resourceName, bookingDate, statusStr);
                notification.setContent(content);
                notification.setNotificationType((byte) 2); // 2: Utility Booking
                notification.setRelatedEntityType("UtilityBooking");
                notification.setReceiver(residentAccount);
                notification.setRecipient("RESIDENT");
                notification.setCreatedAt(java.time.LocalDateTime.now());

                // Find manager/admin sender (optional, can be null or account id 1)
                Account sender = accountRepository.findById(1).orElse(residentAccount);
                notification.setCreatedBy(sender);

                notification = notificationRepository.save(notification);

                AccountNotification accountNotification = new AccountNotification();
                accountNotification.setNotification(notification);
                accountNotification.setAccount(residentAccount);
                accountNotification.setIsRead(false);
                accountNotification.setReadAt(null);
                accountNotificationRepository.save(accountNotification);
            }

            // 2. Email Notification
            if (booking.getProfile() != null && booking.getProfile().getEmail() != null && !booking.getProfile().getEmail().isEmpty()) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(booking.getProfile().getEmail());
                message.setSubject(title + " | ABM System");
                message.setText("Xin chào,\n\n"
                        + String.format("Đơn đặt lịch %s vào ngày %s của bạn đã %s.\n\n", resourceName, bookingDate, statusStr)
                        + "Vui lòng đăng nhập vào hệ thống để xem chi tiết.\n\n"
                        + "Trân trọng,\n"
                        + "Ban quản lý tòa nhà");
                mailSender.send(message);
            }
        } catch (Exception e) {
            System.err.println("[Notification Error] Failed to create booking status update notification: " + e.getMessage());
        }
    }

    private void distributeNotification(Notification notification) {
        String recipientGroup = notification.getRelatedEntityType();
        if (recipientGroup == null) {
            return;
        }

        List<Account> allAccounts = accountRepository.findAll();
        List<Account> targetAccounts = allAccounts.stream().filter(account -> {
            // Only resident accounts can receive notifications
            if (account.getRole() == null || !"RESIDENT".equalsIgnoreCase(account.getRole().getRoleName())) {
                return false;
            }

            switch (recipientGroup) {
                case "All Residents":
                    return true;
                case "Building A":
                    return account.getProfile() != null 
                        && account.getProfile().getApartment() != null 
                        && account.getProfile().getApartment().getApartmentNumber() != null 
                        && account.getProfile().getApartment().getApartmentNumber().trim().toUpperCase().startsWith("A");
                case "Vehicle Owners":
                    return account.getProfile() != null 
                        && account.getProfile().getVehicles() != null 
                        && !account.getProfile().getVehicles().isEmpty();
                default:
                    return true;
            }
        }).collect(Collectors.toList());

        for (Account account : targetAccounts) {
            AccountNotification accountNotification = new AccountNotification();
            accountNotification.setNotification(notification);
            accountNotification.setAccount(account);
            accountNotification.setIsRead(false);
            accountNotificationRepository.save(accountNotification);
        }
    }
}
