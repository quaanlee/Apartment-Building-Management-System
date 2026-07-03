package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.ApartmentPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentPriceHistoryRepository extends JpaRepository<ApartmentPriceHistory, Integer> {
}
