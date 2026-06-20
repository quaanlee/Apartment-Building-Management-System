package com.quan.apartment_building_management_system.service.impl;

import com.quan.apartment_building_management_system.entity.AccountNotification;
import com.quan.apartment_building_management_system.repository.AccountNotificationRepository;
import com.quan.apartment_building_management_system.service.AccountNotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AccountNotificationServiceImpl implements AccountNotificationService {

    private final AccountNotificationRepository accountNotificationRepository;

    public AccountNotificationServiceImpl(AccountNotificationRepository accountNotificationRepository) {
        this.accountNotificationRepository = accountNotificationRepository;
    }

    @Override
    public List<AccountNotification> findAll() {
        return accountNotificationRepository.findAll();
    }

    @Override
    public Optional<AccountNotification> findById(Long id) {
        return accountNotificationRepository.findById(id);
    }

    @Override
    public List<AccountNotification> findByAccountId(Integer accountId) {
        return accountNotificationRepository.findByAccountAccountId(accountId);
    }

    @Override
    public List<AccountNotification> findUnreadByAccountId(Integer accountId) {
        return accountNotificationRepository.findByAccountAccountIdAndIsRead(accountId, false);
    }

    @Override
    @Transactional
    public AccountNotification save(AccountNotification accountNotification) {
        return accountNotificationRepository.save(accountNotification);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        accountNotificationRepository.deleteById(id);
    }
}
