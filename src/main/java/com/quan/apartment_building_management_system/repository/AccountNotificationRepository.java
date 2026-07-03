package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.AccountNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountNotificationRepository extends JpaRepository<AccountNotification, Long> {

    List<AccountNotification> findByAccountAccountId(Integer accountId);

    List<AccountNotification> findByAccountAccountIdAndIsRead(Integer accountId, Boolean isRead);
}
