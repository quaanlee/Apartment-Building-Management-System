package com.quan.apartment_building_management_system.service.maintenance.impl;

import com.quan.apartment_building_management_system.entity.MaintenanceRequest;
import com.quan.apartment_building_management_system.repository.MaintenanceRequestRepository;
import com.quan.apartment_building_management_system.service.maintenance.MaintenanceRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Transactional(readOnly = true)
public class MaintenanceRequestServiceImpl implements MaintenanceRequestService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService;

    public MaintenanceRequestServiceImpl(MaintenanceRequestRepository maintenanceRequestRepository, com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService) {
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.systemLogService = systemLogService;
    }

    @Override
    public List<MaintenanceRequest> findAll() {
        return maintenanceRequestRepository.findAll();
    }

    @Override
    public Optional<MaintenanceRequest> findById(Integer id) {
        return maintenanceRequestRepository.findById(id);
    }

    @Override
    public List<MaintenanceRequest> findByProfileId(Integer profileId) {
        return maintenanceRequestRepository.findByProfileProfileId(profileId);
    }

    @Override
    public List<MaintenanceRequest> findByApartmentId(Integer apartmentId) {
        return maintenanceRequestRepository.findByApartmentApartmentId(apartmentId);
    }

    @Override
    @Transactional
    public MaintenanceRequest save(MaintenanceRequest request) {
        return maintenanceRequestRepository.save(request);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        maintenanceRequestRepository.deleteById(id);
    }

    @Override
    public Page<MaintenanceRequest> searchRequests(String keyword, Byte status, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        return maintenanceRequestRepository.searchRequests(keyword, status, fromDate, toDate, pageable);
    }
}
