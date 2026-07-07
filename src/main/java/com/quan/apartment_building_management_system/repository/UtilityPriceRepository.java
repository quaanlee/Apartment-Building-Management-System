package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.UtilityPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UtilityPriceRepository extends JpaRepository<UtilityPrice, Integer> {

    List<UtilityPrice> findByResourceResourceId(Integer resourceId);

    List<UtilityPrice> findByResourceUtilityUtilityId(Integer utilityId);

    java.util.Optional<UtilityPrice> findByResourceResourceIdAndUnitUnitId(Integer resourceId, Integer unitId);
}
