package com.quan.apartment_building_management_system.service;

import com.quan.apartment_building_management_system.entity.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    List<Account> findAll();

    Optional<Account> findById(Integer id);

    Optional<Account> findByUsername(String username);

    boolean existsByUsername(String username);

    Account save(Account account);

    void deleteById(Integer id);
}
