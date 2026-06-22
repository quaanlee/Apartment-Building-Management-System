package com.quan.apartment_building_management_system.service.maintenance.impl;

import com.quan.apartment_building_management_system.entity.MaintenanceReport;
import com.quan.apartment_building_management_system.repository.MaintenanceReportRepository;
import com.quan.apartment_building_management_system.service.maintenance.MaintenanceReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MaintenanceReportServiceImpl implements MaintenanceReportService {

    private final MaintenanceReportRepository maintenanceReportRepository;

    public MaintenanceReportServiceImpl(MaintenanceReportRepository maintenanceReportRepository) {
        this.maintenanceReportRepository = maintenanceReportRepository;
    }

    @Override
    public List<MaintenanceReport> findAll() {
        return maintenanceReportRepository.findAll();
    }

    @Override
    public Optional<MaintenanceReport> findById(Long id) {
        return maintenanceReportRepository.findById(id);
    }

    @Override
    public List<MaintenanceReport> findByTaskId(Integer taskId) {
        return maintenanceReportRepository.findByMaintenanceTaskTaskId(taskId);
    }

    @Override
    @Transactional
    public MaintenanceReport save(MaintenanceReport maintenanceReport) {
        return maintenanceReportRepository.save(maintenanceReport);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        maintenanceReportRepository.deleteById(id);
    }
}
