package com.quan.apartment_building_management_system.service.impl;

import com.quan.apartment_building_management_system.entity.Unit;
import com.quan.apartment_building_management_system.repository.UnitRepository;
import com.quan.apartment_building_management_system.service.UnitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;

    public UnitServiceImpl(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    public List<Unit> findAll() {
        return unitRepository.findAll();
    }

    @Override
    public Optional<Unit> findById(Integer id) {
        return unitRepository.findById(id);
    }

    @Override
    public Optional<Unit> findByUnitName(String unitName) {
        return unitRepository.findByUnitName(unitName);
    }

    @Override
    @Transactional
    public Unit save(Unit unit) {
        return unitRepository.save(unit);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        unitRepository.deleteById(id);
    }
}
