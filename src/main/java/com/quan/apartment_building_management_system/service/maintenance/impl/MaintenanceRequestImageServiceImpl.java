package com.quan.apartment_building_management_system.service.maintenance.impl;

import com.quan.apartment_building_management_system.entity.MaintenanceRequestImage;
import com.quan.apartment_building_management_system.repository.MaintenanceRequestImageRepository;
import com.quan.apartment_building_management_system.service.maintenance.MaintenanceRequestImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MaintenanceRequestImageServiceImpl implements MaintenanceRequestImageService {

    private final MaintenanceRequestImageRepository repository;

    public MaintenanceRequestImageServiceImpl(MaintenanceRequestImageRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<MaintenanceRequestImage> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<MaintenanceRequestImage> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<MaintenanceRequestImage> findByRequestID(Integer requestID) {
        return repository.findByRequestID(requestID);
    }

    @Override
    @Transactional
    public MaintenanceRequestImage save(MaintenanceRequestImage image) {
        return repository.save(image);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}

