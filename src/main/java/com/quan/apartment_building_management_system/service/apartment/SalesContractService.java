package com.quan.apartment_building_management_system.service.apartment;

import com.quan.apartment_building_management_system.entity.SalesContract;
import java.util.List;
import java.util.Optional;

public interface SalesContractService {
    List<SalesContract> findAll();
    Optional<SalesContract> findById(Integer id);
    SalesContract save(SalesContract salesContract);
    void deleteById(Integer id);
}
