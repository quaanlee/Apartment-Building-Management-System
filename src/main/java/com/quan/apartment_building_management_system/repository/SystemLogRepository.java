package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {

    List<SystemLog> findByAccountAccountId(Integer accountId);

    @Query("SELECT s FROM SystemLog s LEFT JOIN s.account a LEFT JOIN a.role r LEFT JOIN a.profile p " +
           "WHERE (:search IS NULL OR :search = '' OR " +
           "       LOWER(a.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "       LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "       LOWER(s.action) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "       LOWER(s.entityType) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:roleName IS NULL OR :roleName = '' OR :roleName = 'All Roles' OR " +
           "     (:roleName = 'System' AND s.account IS NULL) OR " +
           "     (r.roleName = :roleName)) " +
           "AND (:fromDate IS NULL OR s.createdAt >= :fromDate) " +
           "AND (:toDate IS NULL OR s.createdAt <= :toDate)")
    Page<SystemLog> searchLogs(@Param("search") String search,
                               @Param("roleName") String roleName,
                               @Param("fromDate") LocalDateTime fromDate,
                               @Param("toDate") LocalDateTime toDate,
                               Pageable pageable);
}
