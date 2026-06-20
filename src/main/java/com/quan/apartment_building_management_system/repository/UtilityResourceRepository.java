package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.UtilityResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UtilityResourceRepository extends JpaRepository<UtilityResource, Integer> {

    List<UtilityResource> findByUtilityUtilityId(Integer utilityId);
}
