package com.quan.apartment_building_management_system.service.utility;

import com.quan.apartment_building_management_system.entity.UtilityPrice;

import java.util.List;
import java.util.Optional;

public interface UtilityPriceService {

    List<UtilityPrice> findAll();

    Optional<UtilityPrice> findById(Integer id);

    List<UtilityPrice> findByResourceId(Integer resourceId);

    List<UtilityPrice> findByUtilityId(Integer utilityId);

    Optional<UtilityPrice> findByResourceIdAndUnitId(Integer resourceId, Integer unitId);

    UtilityPrice save(UtilityPrice utilityPrice);

    void deleteById(Integer id);
}
