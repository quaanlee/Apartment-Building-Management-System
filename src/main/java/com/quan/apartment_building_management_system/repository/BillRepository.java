package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {

    List<Bill> findByApartmentApartmentId(Integer apartmentId);

    List<Bill> findByBillYearAndBillMonth(Short billYear, Byte billMonth);
}
