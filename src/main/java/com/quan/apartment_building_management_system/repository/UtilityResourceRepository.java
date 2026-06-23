package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.UtilityResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UtilityResourceRepository extends JpaRepository<UtilityResource, Integer> {

    List<UtilityResource> findByUtilityUtilityId(Integer utilityId);
}
