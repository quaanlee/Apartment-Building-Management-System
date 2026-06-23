package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.MaintenanceTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceTaskRepository extends JpaRepository<MaintenanceTask, Integer> {

    Optional<MaintenanceTask> findByMaintenanceRequestRequestId(Integer requestId);

    List<MaintenanceTask> findByStaffAccountId(Integer staffId);
}
