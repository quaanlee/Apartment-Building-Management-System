package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Utility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilityRepository extends JpaRepository<Utility, Integer> {

    Optional<Utility> findByUtilityName(String utilityName);
}
