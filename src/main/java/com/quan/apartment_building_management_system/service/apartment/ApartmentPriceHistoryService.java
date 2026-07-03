package com.quan.apartment_building_management_system.service.apartment;

import com.quan.apartment_building_management_system.entity.ApartmentPriceHistory;
import java.util.List;
import java.util.Optional;

public interface ApartmentPriceHistoryService {
    List<ApartmentPriceHistory> findAll();
    Optional<ApartmentPriceHistory> findById(Integer id);
    ApartmentPriceHistory save(ApartmentPriceHistory apartmentPriceHistory);
    void deleteById(Integer id);
}
