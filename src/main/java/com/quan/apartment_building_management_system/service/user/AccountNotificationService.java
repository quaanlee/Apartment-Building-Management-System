package com.quan.apartment_building_management_system.service.user;

import com.quan.apartment_building_management_system.entity.AccountNotification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface AccountNotificationService {

    List<AccountNotification> findAll();

    Optional<AccountNotification> findById(Long id);

    List<AccountNotification> findByAccountId(Integer accountId);

    List<AccountNotification> findUnreadByAccountId(Integer accountId);

    AccountNotification save(AccountNotification accountNotification);

    void deleteById(Long id);
}
