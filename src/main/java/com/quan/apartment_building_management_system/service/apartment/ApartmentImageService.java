package com.quan.apartment_building_management_system.service.apartment;

import com.quan.apartment_building_management_system.entity.ApartmentImage;
import java.util.List;
import java.util.Optional;

public interface ApartmentImageService {
    List<ApartmentImage> findAll();
    Optional<ApartmentImage> findById(Integer id);
    ApartmentImage save(ApartmentImage apartmentImage);
    void deleteById(Integer id);
}
