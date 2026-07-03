package com.quan.apartment_building_management_system.service.maintenance;

import com.quan.apartment_building_management_system.entity.MaintenanceReportImage;
import java.util.List;
import java.util.Optional;

public interface MaintenanceReportImageService {
    List<MaintenanceReportImage> findAll();
    Optional<MaintenanceReportImage> findById(Long id);
    MaintenanceReportImage save(MaintenanceReportImage maintenanceReportImage);
    void deleteById(Long id);
}
