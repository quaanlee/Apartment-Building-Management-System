package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    List<Payment> findByBillBillId(Integer billId);

    Optional<Payment> findByTransactionCode(String transactionCode);

    List<Payment> findByPaidByAccountId(Integer accountId);
}
