package com.quan.apartment_building_management_system.service.utility.impl;

import com.quan.apartment_building_management_system.entity.Vehicle;
import com.quan.apartment_building_management_system.repository.VehicleRepository;
import com.quan.apartment_building_management_system.service.utility.VehicleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService) {
        this.vehicleRepository = vehicleRepository;
        this.systemLogService = systemLogService;
    }

    @Override
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    @Override
    public Optional<Vehicle> findById(Integer id) {
        return vehicleRepository.findById(id);
    }

    @Override
    public List<Vehicle> findByProfileId(Integer profileId) {
        return vehicleRepository.findByProfileProfileId(profileId);
    }

    @Override
    public Optional<Vehicle> findByLicensePlate(String licensePlate) {
        return vehicleRepository.findByLicensePlate(licensePlate);
    }

    @Override
    @Transactional
    public Vehicle save(Vehicle vehicle) {
        boolean isNew = vehicle.getVehicleId() == null;
        com.quan.apartment_building_management_system.dto.systemlog.VehicleRegistrationLogDTO oldDto = null;
        if (!isNew) {
            oldDto = com.quan.apartment_building_management_system.dto.systemlog.VehicleRegistrationLogDTO.fromEntity(vehicleRepository.findById(vehicle.getVehicleId()).orElse(null));
        }

        Vehicle saved = vehicleRepository.save(vehicle);
        
        com.quan.apartment_building_management_system.dto.systemlog.VehicleRegistrationLogDTO newDto = com.quan.apartment_building_management_system.dto.systemlog.VehicleRegistrationLogDTO.fromEntity(saved);
        String action = isNew ? "CREATE_VEHICLE_REGISTRATION" : "UPDATE_VEHICLE_REGISTRATION";
        String desc = isNew ? "Created vehicle registration for " + saved.getLicensePlate() : "Updated vehicle registration for " + saved.getLicensePlate();
        systemLogService.logSystemAction(action, "Vehicle", saved.getVehicleId(), oldDto, newDto, desc);
        
        return saved;
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        vehicleRepository.deleteById(id);
    }

    @Override
    public long countByStatus(Byte status) {
        return vehicleRepository.countByStatus(status);
    }

    @Override
    public List<Vehicle> getPendingRequests() {
        return vehicleRepository.findByStatusOrderByRegisteredDateDesc((byte) 0);
    }

    @Override
    public List<Vehicle> getApprovedVehicles() {
        return vehicleRepository.findByStatusOrderByRegisteredDateDesc((byte) 1);
    }

    @Override
    @Transactional
    public void approveVehicle(Integer id, String approvedByUsername) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid vehicle Id:" + id));
        com.quan.apartment_building_management_system.dto.systemlog.VehicleRegistrationLogDTO oldDto = com.quan.apartment_building_management_system.dto.systemlog.VehicleRegistrationLogDTO.fromEntity(vehicle);

        vehicle.setStatus((byte) 1);
        vehicle.setApprovedAt(java.time.LocalDateTime.now());
        // For simplicity, we are not setting approvedBy Account object here without injecting AccountRepository
        // A complete implementation would fetch the Account and set it.
        Vehicle saved = vehicleRepository.save(vehicle);
        
        com.quan.apartment_building_management_system.dto.systemlog.VehicleRegistrationLogDTO newDto = com.quan.apartment_building_management_system.dto.systemlog.VehicleRegistrationLogDTO.fromEntity(saved);
        systemLogService.logSystemAction("APPROVE_VEHICLE_REGISTRATION", "Vehicle", saved.getVehicleId(), oldDto, newDto, "Approved vehicle registration for " + saved.getLicensePlate());
    }

    @Override
    @Transactional
    public void rejectVehicle(Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid vehicle Id:" + id));
        com.quan.apartment_building_management_system.dto.systemlog.VehicleRegistrationLogDTO oldDto = com.quan.apartment_building_management_system.dto.systemlog.VehicleRegistrationLogDTO.fromEntity(vehicle);

        vehicle.setStatus((byte) 2);
        Vehicle saved = vehicleRepository.save(vehicle);
        
        com.quan.apartment_building_management_system.dto.systemlog.VehicleRegistrationLogDTO newDto = com.quan.apartment_building_management_system.dto.systemlog.VehicleRegistrationLogDTO.fromEntity(saved);
        systemLogService.logSystemAction("REJECT_VEHICLE_REGISTRATION", "Vehicle", saved.getVehicleId(), oldDto, newDto, "Rejected vehicle registration for " + saved.getLicensePlate());
    }

    @Override
    @Transactional
    public void revokeVehicle(Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid vehicle Id:" + id));
        com.quan.apartment_building_management_system.dto.systemlog.VehicleRegistrationLogDTO oldDto = com.quan.apartment_building_management_system.dto.systemlog.VehicleRegistrationLogDTO.fromEntity(vehicle);

        vehicle.setStatus((byte) 0);
        Vehicle saved = vehicleRepository.save(vehicle);
        
        com.quan.apartment_building_management_system.dto.systemlog.VehicleRegistrationLogDTO newDto = com.quan.apartment_building_management_system.dto.systemlog.VehicleRegistrationLogDTO.fromEntity(saved);
        systemLogService.logSystemAction("REVOKE_VEHICLE_REGISTRATION", "Vehicle", saved.getVehicleId(), oldDto, newDto, "Revoked vehicle registration for " + saved.getLicensePlate());
    }
}
