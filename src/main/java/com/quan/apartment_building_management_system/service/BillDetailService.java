package com.quan.apartment_building_management_system.service;

import com.quan.apartment_building_management_system.entity.BillDetail;

import java.util.List;
import java.util.Optional;

public interface BillDetailService {

    List<BillDetail> findAll();

    Optional<BillDetail> findById(Long id);

    List<BillDetail> findByBillId(Integer billId);

    BillDetail save(BillDetail billDetail);

    void deleteById(Long id);
}
