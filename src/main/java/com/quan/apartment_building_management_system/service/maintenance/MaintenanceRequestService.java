package com.quan.apartment_building_management_system.service.maintenance;

import com.quan.apartment_building_management_system.entity.MaintenanceRequest;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MaintenanceRequestService {

    List<MaintenanceRequest> findAll();

    Optional<MaintenanceRequest> findById(Integer id);

    List<MaintenanceRequest> findByProfileId(Integer profileId);

    List<MaintenanceRequest> findByApartmentId(Integer apartmentId);

    MaintenanceRequest save(MaintenanceRequest maintenanceRequest);

    void deleteById(Integer id);

    Page<MaintenanceRequest> searchRequests(String keyword, Byte status, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
}
