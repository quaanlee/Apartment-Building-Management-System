package com.quan.apartment_building_management_system.service.apartment.impl;

import com.quan.apartment_building_management_system.entity.ApartmentImage;
import com.quan.apartment_building_management_system.repository.ApartmentImageRepository;
import com.quan.apartment_building_management_system.service.apartment.ApartmentImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ApartmentImageServiceImpl implements ApartmentImageService {

    private final ApartmentImageRepository repository;

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
    public List<ApartmentImage> findByApartmentID(Integer apartmentID) {
        return repository.findByApartmentID(apartmentID);
    }

    @Override
    @Transactional
    public ApartmentImage save(ApartmentImage apartmentImage) {
        return repository.save(apartmentImage);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}

