package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Apartment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Integer> {

    Optional<Apartment> findByApartmentNumber(String apartmentNumber);

    @Query("SELECT a FROM Apartment a WHERE " +
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(a.apartmentNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.roomType) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:roomType IS NULL OR :roomType = '' OR a.roomType = :roomType) AND " +
            "(:floor IS NULL OR a.floor = :floor) AND " +
            "(:status IS NULL OR a.status = :status) AND " +
            "(:minArea IS NULL OR a.area >= :minArea) AND " +
            "(:maxArea IS NULL OR a.area <= :maxArea)")
    Page<Apartment> findFiltered(
            @Param("search") String search,
            @Param("roomType") String roomType,
            @Param("floor") Byte floor,
            @Param("status") Byte status,
            @Param("minArea") BigDecimal minArea,
            @Param("maxArea") BigDecimal maxArea,
            Pageable pageable);

    List<Apartment> findByStatus(Byte status);
    List<Apartment> findByFloor(Byte floor);

    // Dem so luong can ho theo trang thai
    long countByStatus(Byte status);
}