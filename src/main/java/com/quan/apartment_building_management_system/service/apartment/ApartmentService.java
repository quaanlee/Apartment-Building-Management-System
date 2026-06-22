package com.quan.apartment_building_management_system.service.apartment;

import com.quan.apartment_building_management_system.entity.Apartment;

import java.util.List;
import java.util.Optional;

public interface ApartmentService {

    List<Apartment> findAll();

    Optional<Apartment> findById(Integer id);

    Optional<Apartment> findByApartmentNumber(String apartmentNumber);

    Apartment save(Apartment apartment);

    void deleteById(Integer id);
}
