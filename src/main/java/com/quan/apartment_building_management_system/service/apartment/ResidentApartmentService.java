package com.quan.apartment_building_management_system.service.apartment;

import com.quan.apartment_building_management_system.entity.ResidentApartment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ResidentApartmentService {

    List<ResidentApartment> findAll();

    Optional<ResidentApartment> findById(Integer id);

    List<ResidentApartment> findByProfileId(Integer profileId);

    List<ResidentApartment> findByApartmentId(Integer apartmentId);

    ResidentApartment save(ResidentApartment residentApartment);

    void deleteById(Integer id);
}
