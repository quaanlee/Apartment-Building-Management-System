package com.quan.apartment_building_management_system.service.apartment.impl;

import com.quan.apartment_building_management_system.entity.SalesContract;
import com.quan.apartment_building_management_system.repository.SalesContractRepository;
import com.quan.apartment_building_management_system.service.apartment.SalesContractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SalesContractServiceImpl implements SalesContractService {

    private final SalesContractRepository repository;

    public SalesContractServiceImpl(SalesContractRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<SalesContract> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<SalesContract> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<SalesContract> findByApartmentID(Integer apartmentID) {
        return repository.findByApartmentID(apartmentID);
    }

    @Override
    @Transactional
    public SalesContract save(SalesContract salesContract) {
        return repository.save(salesContract);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}

