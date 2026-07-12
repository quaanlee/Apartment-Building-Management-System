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

    public NotificationServiceImpl(NotificationRepository notificationRepository, 
                                   JavaMailSender mailSender,
                                   AccountRepository accountRepository,
                                   AccountNotificationRepository accountNotificationRepository) {
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
        this.accountRepository = accountRepository;
        this.accountNotificationRepository = accountNotificationRepository;
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
