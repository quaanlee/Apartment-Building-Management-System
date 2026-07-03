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

    @Query("SELECT COUNT(s) FROM SystemLog s LEFT JOIN s.account a LEFT JOIN a.role r WHERE r.roleName = :roleName")
    long countByRoleName(@Param("roleName") String roleName);

    @Query("SELECT s FROM SystemLog s " +
           "LEFT JOIN FETCH s.account a " +
           "LEFT JOIN FETCH a.role r " +
           "LEFT JOIN FETCH a.profile p " +
           "WHERE (COALESCE(:search, '') = '' OR " +
           "       LOWER(a.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "       LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "       LOWER(s.action) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "       LOWER(s.entityType) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (COALESCE(:roleName, '') = '' OR :roleName = 'All Roles' OR " +
           "     (:roleName = 'System' AND s.account IS NULL) OR " +
           "     (r.roleName = :roleName)) " +
           "AND (:fromDate IS NULL OR s.createdAt >= :fromDate) " +
           "AND (:toDate IS NULL OR s.createdAt <= :toDate)")
    Page<SystemLog> searchLogs(@Param("search") String search,
                               @Param("roleName") String roleName,
                               @Param("fromDate") LocalDateTime fromDate,
                               @Param("toDate") LocalDateTime toDate,
                               Pageable pageable);

    Page<SystemLog> findByAccountAccountId(Integer accountId, Pageable pageable);

    @Query("SELECT sl FROM SystemLog sl " +
           "LEFT JOIN FETCH sl.account a " +
           "LEFT JOIN FETCH a.role r " +
           "LEFT JOIN FETCH a.profile p " +
           "WHERE (:fromDate IS NULL OR sl.createdAt >= :fromDate) " +
           "  AND (:toDate IS NULL OR sl.createdAt <= :toDate) " +
           "  AND (:role IS NULL OR r.roleName = :role) " +
           "  AND (:action IS NULL OR sl.action = :action) " +
           "  AND (:search IS NULL OR :search = '' OR " +
           "       LOWER(a.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "       LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "       LOWER(sl.action) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "       LOWER(sl.entityType) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY sl.createdAt DESC")
    Page<SystemLog> findFiltered(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("role") String role,
            @Param("action") String action,
            @Param("search") String search,
            Pageable pageable);
}
