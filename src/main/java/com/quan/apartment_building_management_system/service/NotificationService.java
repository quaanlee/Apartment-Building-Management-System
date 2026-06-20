package com.quan.apartment_building_management_system.service;

import com.quan.apartment_building_management_system.entity.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationService {

    List<Notification> findAll();

    Optional<Notification> findById(Long id);

    Notification save(Notification notification);

    void deleteById(Long id);
}
