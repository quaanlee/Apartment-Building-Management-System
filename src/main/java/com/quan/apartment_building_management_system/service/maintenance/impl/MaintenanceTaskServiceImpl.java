package com.quan.apartment_building_management_system.service.maintenance.impl;

import com.quan.apartment_building_management_system.dto.StaffWorkStatusDTO;
import com.quan.apartment_building_management_system.entity.MaintenanceRequest;
import com.quan.apartment_building_management_system.entity.MaintenanceTask;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.repository.MaintenanceRequestRepository;
import com.quan.apartment_building_management_system.repository.MaintenanceTaskRepository;
import com.quan.apartment_building_management_system.repository.ProfileRepository;
import com.quan.apartment_building_management_system.service.maintenance.MaintenanceTaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MaintenanceTaskServiceImpl implements MaintenanceTaskService {

    private final MaintenanceTaskRepository maintenanceTaskRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final ProfileRepository profileRepository;

    public MaintenanceTaskServiceImpl(MaintenanceTaskRepository maintenanceTaskRepository,
                                      MaintenanceRequestRepository maintenanceRequestRepository,
                                      ProfileRepository profileRepository) {
        this.maintenanceTaskRepository = maintenanceTaskRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.profileRepository = profileRepository;
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

    @Override
    @Transactional
    public MaintenanceTask assignTask(Integer requestId, Integer staffProfileId, LocalDateTime deadline, Integer managerAccountId) {
        MaintenanceRequest request = maintenanceRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("MaintenanceRequest not found with ID: " + requestId));

        Profile staff = profileRepository.findById(staffProfileId)
                .orElseThrow(() -> new IllegalArgumentException("Staff Profile not found with ID: " + staffProfileId));

        Profile manager = profileRepository.findByAccountAccountId(managerAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Manager Profile not found for Account ID: " + managerAccountId));

        MaintenanceTask task = new MaintenanceTask();
        task.setMaintenanceRequest(request);
        task.setStaff(staff.getAccount());
        task.setAssignedBy(manager.getAccount());
        task.setAssignedDate(LocalDateTime.now());
        task.setDeadline(deadline);
        task.setStatus((byte) 1); // 1 = ASSIGNED
        task = maintenanceTaskRepository.save(task);

        request.setStatus((byte) 1); // 1 = ASSIGNED
        maintenanceRequestRepository.save(request);

        return task;
    }

    @Override
    public List<StaffWorkStatusDTO> getActiveMaintenanceStaffWithWorkStatus() {
        List<Profile> staffs = profileRepository.findActiveMaintenanceStaffs();
        return staffs.stream().map(profile -> {
            String workStatus = "available";
            if (profile.getAccount() != null) {
                List<MaintenanceTask> tasks = maintenanceTaskRepository.findByStaffAccountId(profile.getAccount().getAccountId());
                boolean hasIncompleteTask = tasks.stream().anyMatch(task -> task.getStatus() == 1);
                if (hasIncompleteTask) {
                    workStatus = "busy";
                }
            }
            return new StaffWorkStatusDTO(profile, workStatus);
        }).toList();
    }

    @Override
    public String getStaffWorkStatus(Integer accountId) {
        if (accountId == null) return "available";
        List<MaintenanceTask> tasks = maintenanceTaskRepository.findByStaffAccountId(accountId);
        boolean hasIncompleteTask = tasks.stream().anyMatch(task -> task.getStatus() == 1);
        return hasIncompleteTask ? "busy" : "available";
    }
}
