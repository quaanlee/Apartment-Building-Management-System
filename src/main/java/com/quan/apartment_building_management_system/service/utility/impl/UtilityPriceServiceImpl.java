package com.quan.apartment_building_management_system.service.utility.impl;

import com.quan.apartment_building_management_system.entity.UtilityPrice;
import com.quan.apartment_building_management_system.repository.UtilityPriceRepository;
import com.quan.apartment_building_management_system.service.utility.UtilityPriceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UtilityPriceServiceImpl implements UtilityPriceService {

    private final UtilityPriceRepository utilityPriceRepository;

    public UtilityPriceServiceImpl(UtilityPriceRepository utilityPriceRepository) {
        this.utilityPriceRepository = utilityPriceRepository;
    }

    @Override
    public List<UtilityPrice> findAll() {
        return utilityPriceRepository.findAll();
    }

    @Override
    public Optional<UtilityPrice> findById(Integer id) {
        return utilityPriceRepository.findById(id);
    }

    @Override
    public List<UtilityPrice> findByUtilityId(Integer utilityId) {
        return utilityPriceRepository.findByUtilityUtilityId(utilityId);
    }

    @Override
    public Optional<UtilityPrice> findByUtilityIdAndUnitId(Integer utilityId, Integer unitId) {
        return utilityPriceRepository.findByUtilityUtilityIdAndUnitUnitId(utilityId, unitId);
    }

    @Override
    @Transactional
    public UtilityPrice save(UtilityPrice utilityPrice) {
        return utilityPriceRepository.save(utilityPrice);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        utilityPriceRepository.deleteById(id);
    }
}
