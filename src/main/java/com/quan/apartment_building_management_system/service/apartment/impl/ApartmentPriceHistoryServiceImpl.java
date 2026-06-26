package com.quan.apartment_building_management_system.service.apartment.impl;

import com.quan.apartment_building_management_system.entity.ApartmentPriceHistory;
import com.quan.apartment_building_management_system.repository.ApartmentPriceHistoryRepository;
import com.quan.apartment_building_management_system.service.apartment.ApartmentPriceHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ApartmentPriceHistoryServiceImpl implements ApartmentPriceHistoryService {

    private final ApartmentPriceHistoryRepository repository;

    public ApartmentPriceHistoryServiceImpl(ApartmentPriceHistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ApartmentPriceHistory> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<ApartmentPriceHistory> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<ApartmentPriceHistory> findByApartmentID(Integer apartmentID) {
        return repository.findByApartmentID(apartmentID);
    }

    @Override
    @Transactional
    public ApartmentPriceHistory save(ApartmentPriceHistory history) {
        return repository.save(history);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}

