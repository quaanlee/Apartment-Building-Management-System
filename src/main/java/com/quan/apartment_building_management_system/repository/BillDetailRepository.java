package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.BillDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillDetailRepository extends JpaRepository<BillDetail, Long> {

    List<BillDetail> findByBillBillId(Integer billId);
}
