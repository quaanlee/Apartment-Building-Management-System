package com.quan.apartment_building_management_system.service.impl;

import com.quan.apartment_building_management_system.entity.Vehicle;
import com.quan.apartment_building_management_system.repository.VehicleRepository;
import com.quan.apartment_building_management_system.service.VehicleService;
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
}
