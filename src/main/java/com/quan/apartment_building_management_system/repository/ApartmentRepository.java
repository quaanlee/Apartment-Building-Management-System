package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Integer> {

    Optional<Apartment> findByApartmentNumber(String apartmentNumber);
}
