package com.quan.apartment_building_management_system.service.apartment.impl;

import com.quan.apartment_building_management_system.entity.ApartmentImage;
import com.quan.apartment_building_management_system.repository.ApartmentImageRepository;
import com.quan.apartment_building_management_system.service.apartment.ApartmentImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApartmentImageServiceImpl implements ApartmentImageService {

    private final ApartmentImageRepository repository;

    @Autowired
    public ApartmentImageServiceImpl(ApartmentImageRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ApartmentImage> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<ApartmentImage> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public ApartmentImage save(ApartmentImage apartmentImage) {
        return repository.save(apartmentImage);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}
