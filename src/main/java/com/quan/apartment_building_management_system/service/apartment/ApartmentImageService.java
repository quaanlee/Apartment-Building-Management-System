package com.quan.apartment_building_management_system.service.apartment;

import com.quan.apartment_building_management_system.entity.ApartmentImage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ApartmentImageService {

    List<ApartmentImage> findAll();

    Optional<ApartmentImage> findById(Integer id);

    List<ApartmentImage> findByApartmentID(Integer apartmentID);

    ApartmentImage save(ApartmentImage apartmentImage);

    void deleteById(Integer id);
}

