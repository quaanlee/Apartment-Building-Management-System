package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.UtilityImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilityImageRepository extends JpaRepository<UtilityImage, Integer> {
}
