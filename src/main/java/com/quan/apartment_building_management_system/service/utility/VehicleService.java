package com.quan.apartment_building_management_system.service.utility;

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

    long countByStatus(Byte status);

    List<Vehicle> getPendingRequests();

    List<Vehicle> getApprovedVehicles();

    void approveVehicle(Integer id, String approvedByUsername);

    void rejectVehicle(Integer id);

    void revokeVehicle(Integer id);
}
