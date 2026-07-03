package com.quan.apartment_building_management_system.service.maintenance.impl;

import com.quan.apartment_building_management_system.entity.MaintenanceRequestImage;
import com.quan.apartment_building_management_system.repository.MaintenanceRequestImageRepository;
import com.quan.apartment_building_management_system.service.maintenance.MaintenanceRequestImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceRequestImageServiceImpl implements MaintenanceRequestImageService {

    private final MaintenanceRequestImageRepository repository;

    @Autowired
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
    public MaintenanceRequestImage save(MaintenanceRequestImage maintenanceRequestImage) {
        return repository.save(maintenanceRequestImage);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}
