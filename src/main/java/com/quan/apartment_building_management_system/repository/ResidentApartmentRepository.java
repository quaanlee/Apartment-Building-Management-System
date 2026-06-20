package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.ResidentApartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResidentApartmentRepository extends JpaRepository<ResidentApartment, Integer> {

    List<ResidentApartment> findByProfileProfileId(Integer profileId);

    List<ResidentApartment> findByApartmentApartmentId(Integer apartmentId);
}
