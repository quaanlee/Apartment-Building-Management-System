package com.quan.apartment_building_management_system.service.maintenance;

import com.quan.apartment_building_management_system.entity.MaintenanceRequestImage;
import java.util.List;
import java.util.Optional;

public interface MaintenanceRequestImageService {
    List<MaintenanceRequestImage> findAll();
    Optional<MaintenanceRequestImage> findById(Integer id);
    MaintenanceRequestImage save(MaintenanceRequestImage maintenanceRequestImage);
    void deleteById(Integer id);
}
