package com.quan.apartment_building_management_system.service.impl;

import com.quan.apartment_building_management_system.entity.MaintenanceRequest;
import com.quan.apartment_building_management_system.repository.MaintenanceRequestRepository;
import com.quan.apartment_building_management_system.service.MaintenanceRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MaintenanceRequestServiceImpl implements MaintenanceRequestService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;

    public MaintenanceRequestServiceImpl(MaintenanceRequestRepository maintenanceRequestRepository) {
        this.maintenanceRequestRepository = maintenanceRequestRepository;
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
    public MaintenanceRequest save(MaintenanceRequest maintenanceRequest) {
        return maintenanceRequestRepository.save(maintenanceRequest);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        maintenanceRequestRepository.deleteById(id);
    }
}
