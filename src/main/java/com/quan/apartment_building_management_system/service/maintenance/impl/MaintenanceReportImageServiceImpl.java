package com.quan.apartment_building_management_system.service.maintenance.impl;

import com.quan.apartment_building_management_system.entity.MaintenanceReportImage;
import com.quan.apartment_building_management_system.repository.MaintenanceReportImageRepository;
import com.quan.apartment_building_management_system.service.maintenance.MaintenanceReportImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MaintenanceReportImageServiceImpl implements MaintenanceReportImageService {

    private final MaintenanceReportImageRepository repository;

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
    public List<MaintenanceReportImage> findByReportID(Long reportID) {
        return repository.findByReportID(reportID);
    }

    @Override
    @Transactional
    public MaintenanceReportImage save(MaintenanceReportImage image) {
        return repository.save(image);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}

