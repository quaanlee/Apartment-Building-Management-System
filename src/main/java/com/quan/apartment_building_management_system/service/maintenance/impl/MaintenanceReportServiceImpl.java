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
    private final com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService;

    public MaintenanceReportServiceImpl(MaintenanceReportRepository maintenanceReportRepository, com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService) {
        this.maintenanceReportRepository = maintenanceReportRepository;
        this.systemLogService = systemLogService;
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
        boolean isNew = maintenanceReport.getReportId() == null;
        com.quan.apartment_building_management_system.dto.systemlog.MaintenanceReportLogDTO oldDto = null;
        if (!isNew) {
            oldDto = com.quan.apartment_building_management_system.dto.systemlog.MaintenanceReportLogDTO.fromEntity(maintenanceReportRepository.findById(maintenanceReport.getReportId()).orElse(null));
        }

        MaintenanceReport saved = maintenanceReportRepository.save(maintenanceReport);
        
        com.quan.apartment_building_management_system.dto.systemlog.MaintenanceReportLogDTO newDto = com.quan.apartment_building_management_system.dto.systemlog.MaintenanceReportLogDTO.fromEntity(saved);
        String action = isNew ? "CREATE_MAINTENANCE_REPORT" : "UPDATE_MAINTENANCE_REPORT";
        String taskIdStr = saved.getMaintenanceTask() != null ? String.valueOf(saved.getMaintenanceTask().getTaskId()) : "Unknown";
        String desc = isNew ? "Created maintenance report for task ID " + taskIdStr : "Updated maintenance report for task ID " + taskIdStr;
        
        // Log ID might be Integer in some places, so handle safely. Log takes Integer entityId. 
        // ReportId is Long, so cast it.
        Integer entityId = saved.getReportId() != null ? saved.getReportId().intValue() : null;
        systemLogService.logSystemAction(action, "MaintenanceReport", entityId, oldDto, newDto, desc);
        
        return saved;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        maintenanceReportRepository.deleteById(id);
    }
}
