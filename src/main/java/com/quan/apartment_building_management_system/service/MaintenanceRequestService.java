package com.quan.apartment_building_management_system.service;

import com.quan.apartment_building_management_system.entity.MaintenanceRequest;

import java.util.List;
import java.util.Optional;

public interface MaintenanceRequestService {

    List<MaintenanceRequest> findAll();

    Optional<MaintenanceRequest> findById(Integer id);

    List<MaintenanceRequest> findByProfileId(Integer profileId);

    List<MaintenanceRequest> findByApartmentId(Integer apartmentId);

    MaintenanceRequest save(MaintenanceRequest maintenanceRequest);

    void deleteById(Integer id);
}
