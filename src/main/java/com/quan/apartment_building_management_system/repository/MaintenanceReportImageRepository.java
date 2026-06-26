package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.MaintenanceReportImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceReportImageRepository extends JpaRepository<MaintenanceReportImage, Long> {

    List<MaintenanceReportImage> findByReportID(Long reportID);
}

