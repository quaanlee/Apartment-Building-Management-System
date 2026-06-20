package com.quan.apartment_building_management_system.service;

import com.quan.apartment_building_management_system.entity.ResidentApartment;

import java.util.List;
import java.util.Optional;

public interface ResidentApartmentService {

    List<ResidentApartment> findAll();

    Optional<ResidentApartment> findById(Integer id);

    List<ResidentApartment> findByProfileId(Integer profileId);

    List<ResidentApartment> findByApartmentId(Integer apartmentId);

    ResidentApartment save(ResidentApartment residentApartment);

    void deleteById(Integer id);
}
