package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.ResidentApartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResidentApartmentRepository extends JpaRepository<ResidentApartment, Integer> {

    List<ResidentApartment> findByProfileProfileId(Integer profileId);

    List<ResidentApartment> findByApartmentApartmentId(Integer apartmentId);
}
