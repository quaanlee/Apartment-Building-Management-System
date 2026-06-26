package com.quan.apartment_building_management_system.service.apartment;

import com.quan.apartment_building_management_system.entity.SalesContract;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface SalesContractService {

    List<SalesContract> findAll();

    Optional<SalesContract> findById(Integer id);

    List<SalesContract> findByApartmentID(Integer apartmentID);

    SalesContract save(SalesContract salesContract);

    void deleteById(Integer id);
}

