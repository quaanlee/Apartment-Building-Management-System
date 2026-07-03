package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.ResidentApartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResidentApartmentRepository extends JpaRepository<ResidentApartment, Integer> {

    // ===== CODE CŨ GIỮ NGUYÊN =====
    List<ResidentApartment> findByProfileProfileId(Integer profileId);
    List<ResidentApartment> findByApartmentApartmentId(Integer apartmentId);

    // ===== THÊM MỚI CÁC METHOD SAU =====

    @Query("SELECT ra FROM ResidentApartment ra WHERE ra.apartment.apartmentId = :apartmentId AND ra.moveOutDate IS NULL")
    List<ResidentApartment> findCurrentResidentsByApartment(@Param("apartmentId") Integer apartmentId);

    @Query("SELECT COUNT(ra) FROM ResidentApartment ra WHERE ra.apartment.apartmentId = :apartmentId AND ra.moveOutDate IS NULL")
    Long countCurrentResidentsByApartment(@Param("apartmentId") Integer apartmentId);

    @Query("SELECT ra FROM ResidentApartment ra WHERE ra.profile.profileId = :profileId AND ra.moveOutDate IS NULL")
    Optional<ResidentApartment> findCurrentApartmentByProfile(@Param("profileId") Integer profileId);
}