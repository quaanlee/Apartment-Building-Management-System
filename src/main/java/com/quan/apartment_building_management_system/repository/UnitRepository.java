package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Integer> {

    Optional<Unit> findByUnitName(String unitName);
}
