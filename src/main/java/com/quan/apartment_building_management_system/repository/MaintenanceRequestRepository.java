package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Integer> {

    List<MaintenanceRequest> findByProfileProfileId(Integer profileId);

    List<MaintenanceRequest> findByApartmentApartmentId(Integer apartmentId);
}
