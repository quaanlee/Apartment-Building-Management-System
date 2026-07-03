package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit, Integer> {

    Optional<Unit> findByUnitName(String unitName);
}
