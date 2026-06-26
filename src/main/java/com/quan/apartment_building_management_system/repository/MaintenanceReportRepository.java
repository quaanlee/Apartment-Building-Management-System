package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.MaintenanceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceReportRepository extends JpaRepository<MaintenanceReport, Long> {

    List<MaintenanceReport> findByMaintenanceTaskTaskId(Integer taskId);
}
