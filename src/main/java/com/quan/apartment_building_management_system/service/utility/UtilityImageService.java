package com.quan.apartment_building_management_system.service.utility;

import com.quan.apartment_building_management_system.entity.UtilityImage;
import java.util.List;
import java.util.Optional;

public interface UtilityImageService {
    List<UtilityImage> findAll();
    Optional<UtilityImage> findById(Integer id);
    List<UtilityImage> findByResourceId(Integer resourceId);
    UtilityImage save(UtilityImage utilityImage);
    void deleteById(Integer id);
}
