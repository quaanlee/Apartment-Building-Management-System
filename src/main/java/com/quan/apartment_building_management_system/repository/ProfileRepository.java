package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {

    Optional<Profile> findByAccountAccountId(Integer accountId);

    Optional<Profile> findByCitizenId(String citizenId);

    @Query("SELECT p FROM Profile p LEFT JOIN p.account a WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.citizenId) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:roleId IS NULL OR a.role.roleId = :roleId) AND " +
           "(:status IS NULL OR a.status = :status)")
    Page<Profile> findFiltered(@Param("search") String search, 
                               @Param("roleId") Integer roleId, 
                               @Param("status") Boolean status, 
                               Pageable pageable);
}
