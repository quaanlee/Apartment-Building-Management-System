package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Integer> {

    List<MaintenanceRequest> findByProfileProfileId(Integer profileId);

    List<MaintenanceRequest> findByApartmentApartmentId(Integer apartmentId);

    @Query("SELECT r FROM MaintenanceRequest r " +
           "JOIN FETCH r.profile p " +
           "JOIN FETCH r.apartment a " +
           "WHERE (:keyword IS NULL OR :keyword = '' " +
           "OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.apartmentNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:status IS NULL OR r.status = :status) " +
           "AND (CAST(:fromDate AS java.time.LocalDateTime) IS NULL OR r.requestDate >= :fromDate) " +
           "AND (CAST(:toDate AS java.time.LocalDateTime) IS NULL OR r.requestDate <= :toDate)")
    Page<MaintenanceRequest> searchRequests(
            @Param("keyword") String keyword,
            @Param("status") Byte status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    List<MaintenanceRequest> findAllByOrderByRequestDateDesc();

    @Query("SELECT r FROM MaintenanceRequest r WHERE "
        + "(:status IS NULL OR r.status = :status) "
        + "AND (:apartmentId IS NULL OR r.apartment.apartmentId = :apartmentId) "
        + "AND (:fromDate IS NULL OR r.requestDate >= :fromDate) "
        + "AND (:toDate IS NULL OR r.requestDate <= :toDate) "
        + "ORDER BY r.requestDate DESC")
    List<MaintenanceRequest> findFiltered(
        @Param("status") Byte status,
        @Param("apartmentId") Integer apartmentId,
        @Param("fromDate") LocalDateTime fromDate,
        @Param("toDate") LocalDateTime toDate);
}
