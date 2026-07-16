package com.quan.apartment_building_management_system.service.maintenance;

import com.quan.apartment_building_management_system.entity.MaintenanceTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MaintenanceTaskService {

    List<MaintenanceTask> findAll();

    Optional<MaintenanceTask> findById(Integer id);

    Optional<MaintenanceTask> findByRequestId(Integer requestId);

    List<MaintenanceTask> findByStaffId(Integer staffId);

    List<MaintenanceTask> findByStaffIdAndStatusIn(Integer staffId, List<Byte> statuses);

    List<MaintenanceTask> findByStaffIdAndStatus(Integer staffId, Byte status);

    Page<MaintenanceTask> findByStaffIdAndStatusIn(Integer staffId, List<Byte> statuses, Pageable pageable);

    Page<MaintenanceTask> findByStaffIdAndStatus(Integer staffId, Byte status, Pageable pageable);

    MaintenanceTask save(MaintenanceTask maintenanceTask);

    void assignTask(Integer requestId, Integer staffId, java.time.LocalDateTime deadline, com.quan.apartment_building_management_system.entity.Account manager);

    String getStaffWorkStatus(Integer staffId);

    List<com.quan.apartment_building_management_system.dto.maintenance.StaffWorkStatusDTO> getActiveMaintenanceStaffWithWorkStatus();

    void deleteById(Integer id);

    void unassignTask(Integer requestId);
}

