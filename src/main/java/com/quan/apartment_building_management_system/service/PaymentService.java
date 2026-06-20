package com.quan.apartment_building_management_system.service;

import com.quan.apartment_building_management_system.entity.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentService {

    List<Payment> findAll();

    Optional<Payment> findById(Integer id);

    List<Payment> findByBillId(Integer billId);

    Optional<Payment> findByTransactionCode(String transactionCode);

    Payment save(Payment payment);

    void deleteById(Integer id);
}
