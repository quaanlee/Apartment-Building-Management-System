package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Integer> {

    Optional<Profile> findByAccountAccountId(Integer accountId);

    Optional<Profile> findByCitizenId(String citizenId);
}
