package com.quan.apartment_building_management_system.service.maintenance.impl;

import com.quan.apartment_building_management_system.entity.MaintenanceTask;
import com.quan.apartment_building_management_system.repository.MaintenanceTaskRepository;
import com.quan.apartment_building_management_system.service.maintenance.MaintenanceTaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public List<MaintenanceTask> findByStaffIdAndStatusIn(Integer staffId, List<Byte> statuses) {
        return maintenanceTaskRepository.findByStaffAccountIdAndStatusIn(staffId, statuses);
    }

    @Override
    public List<MaintenanceTask> findByStaffIdAndStatus(Integer staffId, Byte status) {
        return maintenanceTaskRepository.findByStaffAccountIdAndStatus(staffId, status);
    }

    @Override
    public Page<MaintenanceTask> findByStaffIdAndStatusIn(Integer staffId, List<Byte> statuses, Pageable pageable) {
        return maintenanceTaskRepository.findByStaffAccountIdAndStatusIn(staffId, statuses, pageable);
    }

    @Override
    public Page<MaintenanceTask> findByStaffIdAndStatus(Integer staffId, Byte status, Pageable pageable) {
        return maintenanceTaskRepository.findByStaffAccountIdAndStatus(staffId, status, pageable);
    }

    @Override
    @Transactional
    public MaintenanceTask save(MaintenanceTask maintenanceTask) {
        return maintenanceTaskRepository.save(maintenanceTask);
    }

    @org.springframework.beans.factory.annotation.Autowired
    private com.quan.apartment_building_management_system.repository.MaintenanceRequestRepository maintenanceRequestRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private com.quan.apartment_building_management_system.repository.AccountRepository accountRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private com.quan.apartment_building_management_system.repository.ProfileRepository profileRepository;

    @Override
    @Transactional
    public void assignTask(Integer requestId, Integer staffId, java.time.LocalDateTime deadline, Integer managerId) {
        com.quan.apartment_building_management_system.entity.MaintenanceRequest req = maintenanceRequestRepository.findById(requestId).orElseThrow();
        com.quan.apartment_building_management_system.entity.Account staff = accountRepository.findById(staffId).orElseThrow();
        com.quan.apartment_building_management_system.entity.Account manager = accountRepository.findById(managerId).orElseThrow();

        MaintenanceTask task = new MaintenanceTask();
        task.setMaintenanceRequest(req);
        task.setStaff(staff);
        task.setAssignedBy(manager);
        task.setAssignedDate(java.time.LocalDateTime.now());
        task.setDeadline(deadline);
        task.setStatus((byte) 1); // Assigned
        maintenanceTaskRepository.save(task);

        req.setStatus((byte) 2); // Assigned
        maintenanceRequestRepository.save(req);
    }

    @Override
    public String getStaffWorkStatus(Integer staffId) {
        List<MaintenanceTask> tasks = maintenanceTaskRepository.findByStaffAccountIdAndStatusIn(staffId, List.of((byte)1, (byte)2));
        return tasks.isEmpty() ? "available" : "busy";
    }

    @Override
    public List<com.quan.apartment_building_management_system.dto.maintenance.StaffWorkStatusDTO> getActiveMaintenanceStaffWithWorkStatus() {
        List<com.quan.apartment_building_management_system.dto.maintenance.StaffWorkStatusDTO> result = new java.util.ArrayList<>();
        List<com.quan.apartment_building_management_system.entity.Profile> profiles = profileRepository.findAll();
        for (com.quan.apartment_building_management_system.entity.Profile p : profiles) {
            if (p.getAccount() != null && "MAINTENANCE_STAFF".equalsIgnoreCase(p.getAccount().getRole().getRoleName())) {
                String status = getStaffWorkStatus(p.getAccount().getAccountId());
                result.add(new com.quan.apartment_building_management_system.dto.maintenance.StaffWorkStatusDTO(p, status));
            }
        }
        return result;
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        maintenanceTaskRepository.deleteById(id);
    }
}
