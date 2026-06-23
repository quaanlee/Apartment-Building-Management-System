package com.quan.apartment_building_management_system.service.utility;

import com.quan.apartment_building_management_system.entity.UtilityPrice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UtilityPriceService {

    List<UtilityPrice> findAll();

    Optional<UtilityPrice> findById(Integer id);

    List<UtilityPrice> findByUtilityId(Integer utilityId);

    UtilityPrice save(UtilityPrice utilityPrice);

    void deleteById(Integer id);
}
