package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.UtilityPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UtilityPriceRepository extends JpaRepository<UtilityPrice, Integer> {

    List<UtilityPrice> findByUtilityUtilityId(Integer utilityId);

    java.util.Optional<UtilityPrice> findByUtilityUtilityIdAndUnitUnitId(Integer utilityId, Integer unitId);
}
