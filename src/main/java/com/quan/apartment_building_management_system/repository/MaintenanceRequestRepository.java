package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Integer> {

    List<MaintenanceRequest> findByProfileProfileId(Integer profileId);

    List<MaintenanceRequest> findByApartmentApartmentId(Integer apartmentId);
}
