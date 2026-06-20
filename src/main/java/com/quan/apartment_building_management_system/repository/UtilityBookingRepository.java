package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.UtilityBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UtilityBookingRepository extends JpaRepository<UtilityBooking, Integer> {

    List<UtilityBooking> findByProfileProfileId(Integer profileId);

    List<UtilityBooking> findByResourceResourceId(Integer resourceId);
}
