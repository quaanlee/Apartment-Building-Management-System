package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.UtilityBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UtilityBookingRepository extends JpaRepository<UtilityBooking, Integer> {

    List<UtilityBooking> findByProfileProfileId(Integer profileId);

    List<UtilityBooking> findByResourceResourceId(Integer resourceId);
}
