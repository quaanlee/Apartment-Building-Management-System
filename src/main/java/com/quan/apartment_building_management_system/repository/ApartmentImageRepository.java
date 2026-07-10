package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.ApartmentImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApartmentImageRepository extends JpaRepository<ApartmentImage, Integer> {
    Optional<ApartmentImage> findByApartmentApartmentIdAndImageTitle(Integer apartmentId, String imageTitle);
    List<ApartmentImage> findByApartmentApartmentIdOrderByIsPrimaryDescUploadedAtAsc(Integer apartmentId);
}
