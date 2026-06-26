package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.AccountNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountNotificationRepository extends JpaRepository<AccountNotification, Long> {

    List<AccountNotification> findByAccountAccountId(Integer accountId);

    List<AccountNotification> findByAccountAccountIdAndIsRead(Integer accountId, Boolean isRead);
}
