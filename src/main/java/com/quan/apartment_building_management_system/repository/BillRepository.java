package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Integer> {

    List<Bill> findByApartmentApartmentId(Integer apartmentId);

    List<Bill> findByBillYearAndBillMonth(Short billYear, Byte billMonth);
}
