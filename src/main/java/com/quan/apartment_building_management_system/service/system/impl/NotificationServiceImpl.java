package com.quan.apartment_building_management_system.service.system.impl;

import com.quan.apartment_building_management_system.entity.Notification;
import com.quan.apartment_building_management_system.repository.NotificationRepository;
import com.quan.apartment_building_management_system.service.system.NotificationService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    public NotificationServiceImpl(NotificationRepository notificationRepository, JavaMailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
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
        return notificationRepository.save(notification);
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
}

