package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {

    // ===== CODE CŨ GIỮ NGUYÊN =====
    Optional<Profile> findByAccountAccountId(Integer accountId);

    Optional<Profile> findByCitizenId(String citizenId);
    
    Optional<Profile> findByPhoneNumber(String phoneNumber);

    List<Profile> findByApartmentApartmentId(Integer apartmentId);

    @Query("SELECT p FROM Profile p LEFT JOIN FETCH p.account a LEFT JOIN FETCH a.role LEFT JOIN FETCH p.apartment WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.citizenId) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:roleId IS NULL OR a.role.roleId = :roleId) AND " +
           "(:status IS NULL OR a.status = :status)")
    Page<Profile> findFiltered(@Param("search") String search,
                               @Param("roleId") Integer roleId,
                               @Param("status") Boolean status,
                               Pageable pageable);
    @Query("SELECT p FROM Profile p JOIN p.account a JOIN a.role r WHERE r.roleName = 'MAINTENANCE_STAFF' AND a.status = true")
    List<Profile> findActiveMaintenanceStaffs();
    // ===== THÊM MỚI METHOD SAU =====

    @Query("SELECT p FROM Profile p WHERE " +
            "(:search IS NULL OR :search = '' OR " +
            "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.citizenId) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Profile> findAvailableResidents(@Param("search") String search, Pageable pageable);
    @Query("SELECT p FROM Profile p LEFT JOIN p.account a LEFT JOIN a.role r LEFT JOIN p.apartment apt WHERE " +
            "(a IS NULL OR r.roleName = 'RESIDENT') AND " +
            "(apt IS NULL OR apt.apartmentId <> :apartmentId)")
    Page<Profile> findAvailableResidentsPaged(@Param("apartmentId") Integer apartmentId, Pageable pageable);

    @Query("SELECT p FROM Profile p LEFT JOIN p.account a LEFT JOIN a.role r LEFT JOIN p.apartment apt WHERE " +
            "(a IS NULL OR r.roleName = 'RESIDENT') AND " +
            "(apt IS NULL OR apt.apartmentId <> :apartmentId) AND " +
            "(LOWER(p.fullName) LIKE LOWER(:search) OR " +
            "LOWER(p.phoneNumber) LIKE LOWER(:search) OR " +
            "LOWER(p.email) LIKE LOWER(:search))")
    Page<Profile> findAvailableResidentsPagedWithSearch(@Param("search") String search, @Param("apartmentId") Integer apartmentId, Pageable pageable);
}