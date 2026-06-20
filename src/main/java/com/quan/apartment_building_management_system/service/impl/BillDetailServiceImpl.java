package com.quan.apartment_building_management_system.service.impl;

import com.quan.apartment_building_management_system.entity.BillDetail;
import com.quan.apartment_building_management_system.repository.BillDetailRepository;
import com.quan.apartment_building_management_system.service.BillDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BillDetailServiceImpl implements BillDetailService {

    private final BillDetailRepository billDetailRepository;

    public BillDetailServiceImpl(BillDetailRepository billDetailRepository) {
        this.billDetailRepository = billDetailRepository;
    }

    @Override
    public List<BillDetail> findAll() {
        return billDetailRepository.findAll();
    }

    @Override
    public Optional<BillDetail> findById(Long id) {
        return billDetailRepository.findById(id);
    }

    @Override
    public List<BillDetail> findByBillId(Integer billId) {
        return billDetailRepository.findByBillBillId(billId);
    }

    @Override
    @Transactional
    public BillDetail save(BillDetail billDetail) {
        return billDetailRepository.save(billDetail);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        billDetailRepository.deleteById(id);
    }
}
