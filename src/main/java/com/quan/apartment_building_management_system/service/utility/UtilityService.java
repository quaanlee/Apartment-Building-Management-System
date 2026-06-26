package com.quan.apartment_building_management_system.service.utility;

import com.quan.apartment_building_management_system.entity.Utility;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UtilityService {

    List<Utility> findAll();

    Optional<Utility> findById(Integer id);

    Optional<Utility> findByUtilityName(String utilityName);

    Utility save(Utility utility);

    void deleteById(Integer id);

    List<Utility> searchUtilities(String query);
}
