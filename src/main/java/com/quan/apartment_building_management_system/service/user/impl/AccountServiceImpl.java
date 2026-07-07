package com.quan.apartment_building_management_system.service.user.impl;

import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.repository.AccountRepository;
import com.quan.apartment_building_management_system.service.user.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Optional<Account> findById(Integer id) {
        return accountRepository.findById(id);
    }

    @Override
    public Optional<Account> findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return accountRepository.existsByUsername(username);
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
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
}
