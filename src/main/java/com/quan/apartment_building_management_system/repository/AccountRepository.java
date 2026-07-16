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

    @org.springframework.data.jpa.repository.Query("SELECT a FROM Account a LEFT JOIN FETCH a.role LEFT JOIN FETCH a.profile p LEFT JOIN FETCH a.employeeProfile ep WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.citizenId) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(ep.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(ep.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:roleId IS NULL OR a.role.roleId = :roleId) AND " +
           "(:status IS NULL OR a.status = :status)")
    org.springframework.data.domain.Page<Account> findFilteredAccounts(@org.springframework.data.repository.query.Param("search") String search,
                               @org.springframework.data.repository.query.Param("roleId") Integer roleId,
                               @org.springframework.data.repository.query.Param("status") Boolean status,
                               org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT a FROM Account a JOIN FETCH a.role r LEFT JOIN FETCH a.employeeProfile ep WHERE r.roleName = 'MAINTENANCE_STAFF' AND a.status = true")
    java.util.List<Account> findActiveMaintenanceStaffs();

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Account a SET a.password = :newPassword WHERE a.accountId = :accountId")
    void updatePassword(@org.springframework.data.repository.query.Param("accountId") Integer accountId, @org.springframework.data.repository.query.Param("newPassword") String newPassword);
}
