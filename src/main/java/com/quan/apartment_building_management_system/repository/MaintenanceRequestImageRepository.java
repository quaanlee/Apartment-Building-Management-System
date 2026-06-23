package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.MaintenanceRequestImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRequestImageRepository extends JpaRepository<MaintenanceRequestImage, Integer> {

    List<MaintenanceRequestImage> findByRequestID(Integer requestID);
}

