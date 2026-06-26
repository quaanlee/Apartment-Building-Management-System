package com.quan.apartment_building_management_system.service.maintenance;

import com.quan.apartment_building_management_system.entity.MaintenanceReportImage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface MaintenanceReportImageService {

    List<MaintenanceReportImage> findAll();

    Optional<MaintenanceReportImage> findById(Long id);

    List<MaintenanceReportImage> findByReportID(Long reportID);

    MaintenanceReportImage save(MaintenanceReportImage image);

    void deleteById(Long id);
}

