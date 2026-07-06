package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    List<Payment> findByBillBillId(Integer billId);

    Optional<Payment> findByTransactionCode(String transactionCode);

    List<Payment> findByPaidByAccountIdOrderByPaymentDateDesc(Integer accountId);

    List<Payment> findByBillApartmentApartmentIdOrderByPaymentDateDesc(Integer apartmentId);

    @Query("SELECT p FROM Payment p WHERE p.bill.apartment.apartmentId = :apartmentId " +
           "AND (:status IS NULL OR p.status = :status) " +
           "AND (cast(:startDate as date) IS NULL OR p.paymentDate >= :startDate) " +
           "AND (cast(:endDate as date) IS NULL OR p.paymentDate <= :endDate) " +
           "ORDER BY p.paymentDate DESC")
    Page<Payment> findByApartmentAndFilter(@Param("apartmentId") Integer apartmentId,
                                           @Param("status") Byte status,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate,
                                           Pageable pageable);
}
