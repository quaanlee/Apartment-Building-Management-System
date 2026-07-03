package com.quan.apartment_building_management_system.service.apartment.impl;

import com.quan.apartment_building_management_system.entity.ApartmentPriceHistory;
import com.quan.apartment_building_management_system.repository.ApartmentPriceHistoryRepository;
import com.quan.apartment_building_management_system.service.apartment.ApartmentPriceHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApartmentPriceHistoryServiceImpl implements ApartmentPriceHistoryService {

    private final ApartmentPriceHistoryRepository repository;

    @Autowired
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
    public ApartmentPriceHistory save(ApartmentPriceHistory apartmentPriceHistory) {
        return repository.save(apartmentPriceHistory);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}
