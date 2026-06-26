package com.quan.apartment_building_management_system.service.utility;

import com.quan.apartment_building_management_system.entity.UtilityResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UtilityResourceService {

    List<UtilityResource> findAll();

    Optional<UtilityResource> findById(Integer id);

    List<UtilityResource> findByUtilityId(Integer utilityId);

    UtilityResource save(UtilityResource utilityResource);

    void deleteById(Integer id);
}
