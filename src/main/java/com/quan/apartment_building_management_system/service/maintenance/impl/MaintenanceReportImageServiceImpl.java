package com.quan.apartment_building_management_system.service.maintenance.impl;

import com.quan.apartment_building_management_system.entity.MaintenanceReportImage;
import com.quan.apartment_building_management_system.repository.MaintenanceReportImageRepository;
import com.quan.apartment_building_management_system.service.maintenance.MaintenanceReportImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceReportImageServiceImpl implements MaintenanceReportImageService {

    private final MaintenanceReportImageRepository repository;

    @Autowired
    public MaintenanceReportImageServiceImpl(MaintenanceReportImageRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<MaintenanceReportImage> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<MaintenanceReportImage> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public MaintenanceReportImage save(MaintenanceReportImage maintenanceReportImage) {
        return repository.save(maintenanceReportImage);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
