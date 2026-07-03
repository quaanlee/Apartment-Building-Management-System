package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    Optional<Account> findByUsername(String username);

    boolean existsByUsername(String username);
}
