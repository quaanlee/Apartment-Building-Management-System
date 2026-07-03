package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.MaintenanceTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MaintenanceTaskRepository extends JpaRepository<MaintenanceTask, Integer> {

    Optional<MaintenanceTask> findByMaintenanceRequestRequestId(Integer requestId);
    List<MaintenanceTask> findByStaffAccountId(Integer staffId);
    List<MaintenanceTask> findAllByOrderByAssignedDateDesc();

    @Query("SELECT t FROM MaintenanceTask t WHERE "
        + "(:status IS NULL OR t.status = :status) "
        + "AND (:staffId IS NULL OR t.staff.accountId = :staffId) "
        + "AND (:fromDate IS NULL OR t.assignedDate >= :fromDate) "
        + "AND (:toDate IS NULL OR t.assignedDate <= :toDate) "
        + "ORDER BY t.assignedDate DESC")
    List<MaintenanceTask> findFiltered(
        @Param("status") Byte status,
        @Param("staffId") Integer staffId,
        @Param("fromDate") LocalDateTime fromDate,
        @Param("toDate") LocalDateTime toDate);
}
