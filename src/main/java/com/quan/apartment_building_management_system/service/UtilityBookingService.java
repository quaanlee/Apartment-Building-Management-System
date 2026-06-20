package com.quan.apartment_building_management_system.service;

import com.quan.apartment_building_management_system.entity.UtilityBooking;

import java.util.List;
import java.util.Optional;

public interface UtilityBookingService {

    List<UtilityBooking> findAll();

    Optional<UtilityBooking> findById(Integer id);

    List<UtilityBooking> findByProfileId(Integer profileId);

    List<UtilityBooking> findByResourceId(Integer resourceId);

    UtilityBooking save(UtilityBooking utilityBooking);

    void deleteById(Integer id);
}
