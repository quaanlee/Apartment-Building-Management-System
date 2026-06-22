package com.quan.apartment_building_management_system.service.utility.impl;

import com.quan.apartment_building_management_system.entity.Utility;
import com.quan.apartment_building_management_system.repository.UtilityRepository;
import com.quan.apartment_building_management_system.service.utility.UtilityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UtilityServiceImpl implements UtilityService {

    private final UtilityRepository utilityRepository;

    public UtilityServiceImpl(UtilityRepository utilityRepository) {
        this.utilityRepository = utilityRepository;
    }

    @Override
    public List<Utility> findAll() {
        return utilityRepository.findAll();
    }

    @Override
    public Optional<Utility> findById(Integer id) {
        return utilityRepository.findById(id);
    }

    @Override
    public Optional<Utility> findByUtilityName(String utilityName) {
        return utilityRepository.findByUtilityName(utilityName);
    }

    @Override
    @Transactional
    public Utility save(Utility utility) {
        return utilityRepository.save(utility);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        utilityRepository.deleteById(id);
    }
}
