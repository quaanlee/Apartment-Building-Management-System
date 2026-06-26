package com.quan.apartment_building_management_system.service.maintenance;

import com.quan.apartment_building_management_system.entity.MaintenanceReport;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface MaintenanceReportService {

    List<MaintenanceReport> findAll();

    Optional<MaintenanceReport> findById(Long id);

    List<MaintenanceReport> findByTaskId(Integer taskId);

    MaintenanceReport save(MaintenanceReport maintenanceReport);

    void deleteById(Long id);
}
