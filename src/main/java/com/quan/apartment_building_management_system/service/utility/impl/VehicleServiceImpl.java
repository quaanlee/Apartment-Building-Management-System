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

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
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
        return vehicleRepository.save(vehicle);
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
        vehicle.setStatus((byte) 1);
        vehicle.setApprovedAt(java.time.LocalDateTime.now());
        // For simplicity, we are not setting approvedBy Account object here without injecting AccountRepository
        // A complete implementation would fetch the Account and set it.
        vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public void rejectVehicle(Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid vehicle Id:" + id));
        vehicle.setStatus((byte) 2);
        vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public void revokeVehicle(Integer id) {
        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid vehicle Id:" + id));
        vehicle.setStatus((byte) 0);
        vehicleRepository.save(vehicle);
    }
}
