package com.quan.apartment_building_management_system.service.utility;

import com.quan.apartment_building_management_system.entity.Unit;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UnitService {

    List<Unit> findAll();

    Optional<Unit> findById(Integer id);

    Optional<Unit> findByUnitName(String unitName);

    Unit save(Unit unit);

    void deleteById(Integer id);
}
