package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.BillDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillDetailRepository extends JpaRepository<BillDetail, Long> {

    List<BillDetail> findByBillBillId(Integer billId);
}
