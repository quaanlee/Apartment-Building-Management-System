package com.quan.apartment_building_management_system.service.maintenance;

import com.quan.apartment_building_management_system.entity.MaintenanceRequestImage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface MaintenanceRequestImageService {

    List<MaintenanceRequestImage> findAll();

    Optional<MaintenanceRequestImage> findById(Integer id);

    List<MaintenanceRequestImage> findByRequestID(Integer requestID);

    MaintenanceRequestImage save(MaintenanceRequestImage image);

    void deleteById(Integer id);
}

