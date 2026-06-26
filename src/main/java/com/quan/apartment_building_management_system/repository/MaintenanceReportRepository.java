package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.MaintenanceReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceReportRepository extends JpaRepository<MaintenanceReport, Long> {

    List<MaintenanceReport> findByMaintenanceTaskTaskId(Integer taskId);
}
