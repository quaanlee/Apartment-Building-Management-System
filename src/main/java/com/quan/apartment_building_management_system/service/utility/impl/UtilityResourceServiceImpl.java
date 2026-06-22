package com.quan.apartment_building_management_system.service.utility.impl;

import com.quan.apartment_building_management_system.entity.UtilityResource;
import com.quan.apartment_building_management_system.repository.UtilityResourceRepository;
import com.quan.apartment_building_management_system.service.utility.UtilityResourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UtilityResourceServiceImpl implements UtilityResourceService {

    private final UtilityResourceRepository utilityResourceRepository;

    public UtilityResourceServiceImpl(UtilityResourceRepository utilityResourceRepository) {
        this.utilityResourceRepository = utilityResourceRepository;
    }

    @Override
    public List<UtilityResource> findAll() {
        return utilityResourceRepository.findAll();
    }

    @Override
    public Optional<UtilityResource> findById(Integer id) {
        return utilityResourceRepository.findById(id);
    }

    @Override
    public List<UtilityResource> findByUtilityId(Integer utilityId) {
        return utilityResourceRepository.findByUtilityUtilityId(utilityId);
    }

    @Override
    @Transactional
    public UtilityResource save(UtilityResource utilityResource) {
        return utilityResourceRepository.save(utilityResource);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        utilityResourceRepository.deleteById(id);
    }
}
