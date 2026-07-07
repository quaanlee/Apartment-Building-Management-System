package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.UtilityImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UtilityImageRepository extends JpaRepository<UtilityImage, Integer> {
    @Query("SELECT img FROM UtilityImage img WHERE img.resource.resourceId = :resourceId ORDER BY img.isPrimary DESC, img.createdDate ASC")
    List<UtilityImage> findByResourceId(@Param("resourceId") Integer resourceId);
}
