package com.quan.apartment_building_management_system.service.apartment;

import com.quan.apartment_building_management_system.entity.Apartment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ApartmentService {

    List<Apartment> findAll();

    Optional<Apartment> findById(Integer id);

    Optional<Apartment> findByApartmentNumber(String apartmentNumber);

    Apartment save(Apartment apartment);

    void deleteById(Integer id);
}
