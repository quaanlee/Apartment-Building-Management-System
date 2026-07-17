package com.quan.apartment_building_management_system.service.user;

import com.quan.apartment_building_management_system.entity.Account;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.quan.apartment_building_management_system.dto.user.UserDTO;

public interface AccountService {

    List<Account> findAll();

    Optional<Account> findById(Integer id);

    Optional<Account> findByUsername(String username);

    boolean existsByUsername(String username);

    Account save(Account account);

    void deleteById(Integer id);

    Page<UserDTO> findFilteredAccounts(String search, Integer roleId, Boolean status, Pageable pageable);

    List<UserDTO> findActiveMaintenanceStaffs();

    @org.springframework.transaction.annotation.Transactional
    boolean changePassword(Integer accountId, String oldPassword, String newPassword);

    void resetPassword(String email, String newPassword);
}