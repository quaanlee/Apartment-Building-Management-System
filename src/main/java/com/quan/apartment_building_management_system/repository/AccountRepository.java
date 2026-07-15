package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    Optional<Account> findByUsername(String username);

    boolean existsByUsername(String username);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(a) FROM Account a WHERE UPPER(a.role.roleName) = 'RESIDENT' AND a.status = true")
    long countActiveResidents();

    long countByStatus(Boolean status);
}
