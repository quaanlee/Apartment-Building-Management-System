package com.quan.apartment_building_management_system.service.billing.impl;

import com.quan.apartment_building_management_system.entity.Bill;
import com.quan.apartment_building_management_system.repository.BillRepository;
import com.quan.apartment_building_management_system.service.billing.BillService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;

    public BillServiceImpl(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Override
    public List<Bill> findAll() {
        return billRepository.findAll();
    }

    @Override
    public Optional<Bill> findById(Integer id) {
        return billRepository.findById(id);
    }

    @Override
    public List<Bill> findByApartmentId(Integer apartmentId) {
        return billRepository.findByApartmentApartmentId(apartmentId);
    }

    @Override
    public List<Bill> findByYearAndMonth(Short billYear, Byte billMonth) {
        return billRepository.findByBillYearAndBillMonth(billYear, billMonth);
    }

    @Override
    @Transactional
    public Bill save(Bill bill) {
        return billRepository.save(bill);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        billRepository.deleteById(id);
    }
}
