package com.quan.apartment_building_management_system.service.utility.impl;

import com.quan.apartment_building_management_system.entity.UtilityImage;
import com.quan.apartment_building_management_system.repository.UtilityImageRepository;
import com.quan.apartment_building_management_system.service.utility.UtilityImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilityImageServiceImpl implements UtilityImageService {

    private final UtilityImageRepository repository;

    @Autowired
    public UtilityImageServiceImpl(UtilityImageRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<UtilityImage> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<UtilityImage> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<UtilityImage> findByResourceId(Integer resourceId) {
        return repository.findByResourceId(resourceId);
    }

    @Override
    public UtilityImage save(UtilityImage utilityImage) {
        return repository.save(utilityImage);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}
