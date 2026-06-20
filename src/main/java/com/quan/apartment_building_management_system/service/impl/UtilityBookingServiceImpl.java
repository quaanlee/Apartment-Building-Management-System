package com.quan.apartment_building_management_system.service.impl;

import com.quan.apartment_building_management_system.entity.UtilityBooking;
import com.quan.apartment_building_management_system.repository.UtilityBookingRepository;
import com.quan.apartment_building_management_system.service.UtilityBookingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UtilityBookingServiceImpl implements UtilityBookingService {

    private final UtilityBookingRepository utilityBookingRepository;

    public UtilityBookingServiceImpl(UtilityBookingRepository utilityBookingRepository) {
        this.utilityBookingRepository = utilityBookingRepository;
    }

    @Override
    public List<UtilityBooking> findAll() {
        return utilityBookingRepository.findAll();
    }

    @Override
    public Optional<UtilityBooking> findById(Integer id) {
        return utilityBookingRepository.findById(id);
    }

    @Override
    public List<UtilityBooking> findByProfileId(Integer profileId) {
        return utilityBookingRepository.findByProfileProfileId(profileId);
    }

    @Override
    public List<UtilityBooking> findByResourceId(Integer resourceId) {
        return utilityBookingRepository.findByResourceResourceId(resourceId);
    }

    @Override
    @Transactional
    public UtilityBooking save(UtilityBooking utilityBooking) {
        return utilityBookingRepository.save(utilityBooking);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        utilityBookingRepository.deleteById(id);
    }
}
