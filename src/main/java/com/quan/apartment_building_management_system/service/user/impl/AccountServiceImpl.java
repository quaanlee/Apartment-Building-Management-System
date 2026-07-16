package com.quan.apartment_building_management_system.service.user.impl;

import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.repository.AccountRepository;
import com.quan.apartment_building_management_system.service.user.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.quan.apartment_building_management_system.dto.user.UserDTO;

@Service
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public List<Account> findAll() {
        List<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            if (account.getRole() != null) {
                account.getRole().getRoleName(); // Eagerly initialize proxy
            }
            if (account.getProfile() != null) {
                account.getProfile().getFullName(); // Eagerly initialize proxy
            }
        }
        return accounts;
    }

    @Override
    public Optional<Account> findById(Integer id) {
        Optional<Account> accountOpt = accountRepository.findById(id);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (account.getRole() != null) {
                account.getRole().getRoleName(); // Eagerly initialize proxy
            }
            if (account.getProfile() != null) {
                account.getProfile().getFullName(); // Eagerly initialize proxy
            }
        }
        return accountOpt;
    }

    @Override
    public Optional<Account> findByUsername(String username) {
        Optional<Account> accountOpt = accountRepository.findByUsername(username);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            if (account.getRole() != null) {
                account.getRole().getRoleName(); // Eagerly initialize proxy
            }
            if (account.getProfile() != null) {
                account.getProfile().getFullName(); // Eagerly initialize proxy
            }
        }
        return accountOpt;
    }

    @Override
    public boolean existsByUsername(String username) {
        return accountRepository.existsByUsername(username);
    }

    @Override
    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        accountRepository.deleteById(id);
    }

    @Override
    public Page<UserDTO> findFilteredAccounts(String search, Integer roleId, Boolean status, Pageable pageable) {
        return accountRepository.findFilteredAccounts(search, roleId, status, pageable).map(UserDTO::new);
    }

    @Override
    public List<UserDTO> findActiveMaintenanceStaffs() {
        return accountRepository.findActiveMaintenanceStaffs().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean changePassword(Integer accountId, String oldPassword, String newPassword) {
        Optional<Account> opt = accountRepository.findById(accountId);
        if (opt.isEmpty()) return false;
        Account account = opt.get();
        if (!account.getPassword().equals(oldPassword)) return false;
        account.setPassword(newPassword);
        accountRepository.save(account);
        return true;
    }}
