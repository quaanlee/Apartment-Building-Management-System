package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    List<Vehicle> findByProfileProfileId(Integer profileId);

    Optional<Vehicle> findByLicensePlate(String licensePlate);
}
