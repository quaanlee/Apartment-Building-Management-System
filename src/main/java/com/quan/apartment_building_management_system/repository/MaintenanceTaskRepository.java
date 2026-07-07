package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.MaintenanceTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MaintenanceTaskRepository extends JpaRepository<MaintenanceTask, Integer> {

    Optional<MaintenanceTask> findByMaintenanceRequestRequestId(Integer requestId);

    List<MaintenanceTask> findByStaffAccountId(Integer staffId);

    List<MaintenanceTask> findByStaffAccountIdAndStatusIn(Integer staffId, List<Byte> statuses);

    List<MaintenanceTask> findByStaffAccountIdAndStatus(Integer staffId, Byte status);

    Page<MaintenanceTask> findByStaffAccountIdAndStatusIn(Integer staffId, List<Byte> statuses, Pageable pageable);

    Page<MaintenanceTask> findByStaffAccountIdAndStatus(Integer staffId, Byte status, Pageable pageable);
}
