package com.quan.apartment_building_management_system.service.maintenance;

import com.quan.apartment_building_management_system.entity.MaintenanceReport;

import java.util.List;
import java.util.Optional;

public interface MaintenanceReportService {

    List<MaintenanceReport> findAll();

    Optional<MaintenanceReport> findById(Long id);

    List<MaintenanceReport> findByTaskId(Integer taskId);

    MaintenanceReport save(MaintenanceReport maintenanceReport);

    void deleteById(Long id);
}
