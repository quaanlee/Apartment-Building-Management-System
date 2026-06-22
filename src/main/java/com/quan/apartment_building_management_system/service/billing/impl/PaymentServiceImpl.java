package com.quan.apartment_building_management_system.service.billing.impl;

import com.quan.apartment_building_management_system.entity.Payment;
import com.quan.apartment_building_management_system.repository.PaymentRepository;
import com.quan.apartment_building_management_system.service.billing.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public Optional<Payment> findById(Integer id) {
        return paymentRepository.findById(id);
    }

    @Override
    public List<Payment> findByBillId(Integer billId) {
        return paymentRepository.findByBillBillId(billId);
    }

    @Override
    public Optional<Payment> findByTransactionCode(String transactionCode) {
        return paymentRepository.findByTransactionCode(transactionCode);
    }

    @Override
    @Transactional
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        paymentRepository.deleteById(id);
    }
}
