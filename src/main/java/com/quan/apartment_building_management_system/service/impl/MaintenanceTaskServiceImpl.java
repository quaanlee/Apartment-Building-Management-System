package com.quan.apartment_building_management_system.service.impl;

import com.quan.apartment_building_management_system.entity.MaintenanceTask;
import com.quan.apartment_building_management_system.repository.MaintenanceTaskRepository;
import com.quan.apartment_building_management_system.service.MaintenanceTaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MaintenanceTaskServiceImpl implements MaintenanceTaskService {

    private final MaintenanceTaskRepository maintenanceTaskRepository;

    public MaintenanceTaskServiceImpl(MaintenanceTaskRepository maintenanceTaskRepository) {
        this.maintenanceTaskRepository = maintenanceTaskRepository;
    }

    @Override
    public List<MaintenanceTask> findAll() {
        return maintenanceTaskRepository.findAll();
    }

    @Override
    public Optional<MaintenanceTask> findById(Integer id) {
        return maintenanceTaskRepository.findById(id);
    }

    @Override
    public Optional<MaintenanceTask> findByRequestId(Integer requestId) {
        return maintenanceTaskRepository.findByMaintenanceRequestRequestId(requestId);
    }

    @Override
    public List<MaintenanceTask> findByStaffId(Integer staffId) {
        return maintenanceTaskRepository.findByStaffAccountId(staffId);
    }

    @Override
    @Transactional
    public MaintenanceTask save(MaintenanceTask maintenanceTask) {
        return maintenanceTaskRepository.save(maintenanceTask);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        maintenanceTaskRepository.deleteById(id);
    }
}
