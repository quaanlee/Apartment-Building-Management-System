package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
