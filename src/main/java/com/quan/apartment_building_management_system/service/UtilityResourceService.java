package com.quan.apartment_building_management_system.service;

import com.quan.apartment_building_management_system.entity.UtilityResource;

import java.util.List;
import java.util.Optional;

public interface UtilityResourceService {

    List<UtilityResource> findAll();

    Optional<UtilityResource> findById(Integer id);

    List<UtilityResource> findByUtilityId(Integer utilityId);

    UtilityResource save(UtilityResource utilityResource);

    void deleteById(Integer id);
}
