package com.quan.apartment_building_management_system.service.system;

import com.quan.apartment_building_management_system.entity.Notification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface NotificationService {

    List<Notification> findAll();

    Optional<Notification> findById(Long id);

    Notification save(Notification notification);

    void deleteById(Long id);

    void sendOtpEmail(String toEmail, String otp);
}
