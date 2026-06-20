package com.quan.apartment_building_management_system.service;

import com.quan.apartment_building_management_system.entity.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleService {

    List<Vehicle> findAll();

    Optional<Vehicle> findById(Integer id);

    List<Vehicle> findByProfileId(Integer profileId);

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    Vehicle save(Vehicle vehicle);

    void deleteById(Integer id);
}
