package com.quan.apartment_building_management_system.service.billing;

import com.quan.apartment_building_management_system.entity.Bill;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface BillService {

    List<Bill> findAll();

    Optional<Bill> findById(Integer id);

    List<Bill> findByApartmentId(Integer apartmentId);

    List<Bill> findByYearAndMonth(Short billYear, Byte billMonth);

    Bill save(Bill bill);

    void deleteById(Integer id);
}
