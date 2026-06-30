package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {

    Optional<Profile> findByAccountAccountId(Integer accountId);

    Optional<Profile> findByCitizenId(String citizenId);

    @Query("SELECT p FROM Profile p JOIN p.account a JOIN a.role r WHERE r.roleName = 'MAINTENANCE_STAFF' AND a.status = true")
    List<Profile> findActiveMaintenanceStaffs();
}
