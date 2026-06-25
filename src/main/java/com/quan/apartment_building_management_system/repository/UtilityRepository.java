package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Utility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtilityRepository extends JpaRepository<Utility, Integer> {

    Optional<Utility> findByUtilityName(String utilityName);

    List<Utility> findByUtilityNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String utilityName, String description);
}
