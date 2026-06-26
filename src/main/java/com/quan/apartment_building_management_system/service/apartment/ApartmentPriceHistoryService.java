package com.quan.apartment_building_management_system.service.apartment;

import com.quan.apartment_building_management_system.entity.ApartmentPriceHistory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ApartmentPriceHistoryService {

    List<ApartmentPriceHistory> findAll();

    Optional<ApartmentPriceHistory> findById(Integer id);

    List<ApartmentPriceHistory> findByApartmentID(Integer apartmentID);

    ApartmentPriceHistory save(ApartmentPriceHistory history);

    void deleteById(Integer id);
}

