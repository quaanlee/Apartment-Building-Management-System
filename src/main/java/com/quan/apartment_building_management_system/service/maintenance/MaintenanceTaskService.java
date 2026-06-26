package com.quan.apartment_building_management_system.service.maintenance;

import com.quan.apartment_building_management_system.entity.MaintenanceTask;

import java.util.List;
import java.util.Optional;

public interface MaintenanceTaskService {

    List<MaintenanceTask> findAll();

    Optional<MaintenanceTask> findById(Integer id);

    Optional<MaintenanceTask> findByRequestId(Integer requestId);

    List<MaintenanceTask> findByStaffId(Integer staffId);

    MaintenanceTask save(MaintenanceTask maintenanceTask);

    void deleteById(Integer id);
}
