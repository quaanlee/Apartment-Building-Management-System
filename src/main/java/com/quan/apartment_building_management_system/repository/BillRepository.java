package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Integer> {

    List<Bill> findByApartmentApartmentId(Integer apartmentId);
    List<Bill> findByBillYearAndBillMonth(Short billYear, Byte billMonth);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b " +
           "WHERE (:fromYear IS NULL OR b.billYear > :fromYear OR (b.billYear = :fromYear AND b.billMonth >= :fromMonth)) " +
           "AND (:toYear IS NULL OR b.billYear < :toYear OR (b.billYear = :toYear AND b.billMonth <= :toMonth)) " +
           "AND (:status IS NULL OR b.status = :status) " +
           "AND (:revenueType IS NULL OR EXISTS (SELECT 1 FROM BillDetail bd JOIN bd.serviceItem si WHERE bd.bill.billId = b.billId AND si.serviceType = :revenueType))")
    BigDecimal sumTotalByDateRange(@Param("fromYear") Integer fromYear,
                                   @Param("fromMonth") Integer fromMonth,
                                   @Param("toYear") Integer toYear,
                                   @Param("toMonth") Integer toMonth,
                                   @Param("status") Byte status,
                                   @Param("revenueType") String revenueType);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b " +
           "WHERE b.status = 1 " +
           "AND (:fromYear IS NULL OR b.billYear > :fromYear OR (b.billYear = :fromYear AND b.billMonth >= :fromMonth)) " +
           "AND (:toYear IS NULL OR b.billYear < :toYear OR (b.billYear = :toYear AND b.billMonth <= :toMonth)) " +
           "AND (:revenueType IS NULL OR EXISTS (SELECT 1 FROM BillDetail bd JOIN bd.serviceItem si WHERE bd.bill.billId = b.billId AND si.serviceType = :revenueType))")
    BigDecimal sumCollectedByDateRange(@Param("fromYear") Integer fromYear,
                                       @Param("fromMonth") Integer fromMonth,
                                       @Param("toYear") Integer toYear,
                                       @Param("toMonth") Integer toMonth,
                                       @Param("revenueType") String revenueType);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b " +
           "WHERE b.status = 0 " +
           "AND (:fromYear IS NULL OR b.billYear > :fromYear OR (b.billYear = :fromYear AND b.billMonth >= :fromMonth)) " +
           "AND (:toYear IS NULL OR b.billYear < :toYear OR (b.billYear = :toYear AND b.billMonth <= :toMonth)) " +
           "AND (:revenueType IS NULL OR EXISTS (SELECT 1 FROM BillDetail bd JOIN bd.serviceItem si WHERE bd.bill.billId = b.billId AND si.serviceType = :revenueType))")
    BigDecimal sumOutstandingByDateRange(@Param("fromYear") Integer fromYear,
                                         @Param("fromMonth") Integer fromMonth,
                                         @Param("toYear") Integer toYear,
                                         @Param("toMonth") Integer toMonth,
                                         @Param("revenueType") String revenueType);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b " +
           "WHERE b.status = 2 " +
           "AND (:fromYear IS NULL OR b.billYear > :fromYear OR (b.billYear = :fromYear AND b.billMonth >= :fromMonth)) " +
           "AND (:toYear IS NULL OR b.billYear < :toYear OR (b.billYear = :toYear AND b.billMonth <= :toMonth)) " +
           "AND (:revenueType IS NULL OR EXISTS (SELECT 1 FROM BillDetail bd JOIN bd.serviceItem si WHERE bd.bill.billId = b.billId AND si.serviceType = :revenueType))")
    BigDecimal sumOverdueByDateRange(@Param("fromYear") Integer fromYear,
                                     @Param("fromMonth") Integer fromMonth,
                                     @Param("toYear") Integer toYear,
                                     @Param("toMonth") Integer toMonth,
                                     @Param("revenueType") String revenueType);

    @Query("SELECT b FROM Bill b " +
           "LEFT JOIN FETCH b.apartment a " +
           "WHERE (:fromYear IS NULL OR b.billYear > :fromYear OR (b.billYear = :fromYear AND b.billMonth >= :fromMonth)) " +
           "AND (:toYear IS NULL OR b.billYear < :toYear OR (b.billYear = :toYear AND b.billMonth <= :toMonth)) " +
           "AND (:status IS NULL OR b.status = :status) " +
           "AND (:revenueType IS NULL OR EXISTS (SELECT 1 FROM BillDetail bd JOIN bd.serviceItem si WHERE bd.bill.billId = b.billId AND si.serviceType = :revenueType)) " +
           "AND (:month IS NULL OR b.billMonth = :month) " +
           "ORDER BY b.billYear DESC, b.billMonth DESC")
    Page<Bill> findBillsWithDetails(@Param("fromYear") Integer fromYear,
                                    @Param("fromMonth") Integer fromMonth,
                                    @Param("toYear") Integer toYear,
                                    @Param("toMonth") Integer toMonth,
                                    @Param("status") Byte status,
                                    @Param("revenueType") String revenueType,
                                    @Param("month") Byte month,
                                    Pageable pageable);

    @Query("SELECT b.billMonth as month, SUM(b.totalAmount) as total FROM Bill b WHERE b.billYear = :year AND (:month IS NULL OR b.billMonth = :month) AND (:status IS NULL OR b.status = :status) AND (:revenueType IS NULL OR EXISTS (SELECT 1 FROM BillDetail bd JOIN bd.serviceItem si WHERE bd.bill.billId = b.billId AND si.serviceType = :revenueType)) GROUP BY b.billMonth ORDER BY b.billMonth")
    List<Object[]> sumByYear(@Param("year") short year, @Param("month") Byte month, @Param("status") Byte status, @Param("revenueType") String revenueType);

    @Query("SELECT s.serviceType as type, SUM(bd.amount) as total FROM BillDetail bd JOIN bd.serviceItem s JOIN bd.bill b WHERE (:fromYear IS NULL OR b.billYear > :fromYear OR (b.billYear = :fromYear AND b.billMonth >= :fromMonth)) AND (:toYear IS NULL OR b.billYear < :toYear OR (b.billYear = :toYear AND b.billMonth <= :toMonth)) AND (:status IS NULL OR b.status = :status) AND (:revenueType IS NULL OR s.serviceType = :revenueType) AND (:month IS NULL OR b.billMonth = :month) GROUP BY s.serviceType ORDER BY SUM(bd.amount) DESC")
    List<Object[]> sumByServiceType(@Param("fromYear") Integer fromYear, @Param("fromMonth") Integer fromMonth, @Param("toYear") Integer toYear, @Param("toMonth") Integer toMonth, @Param("status") Byte status, @Param("revenueType") String revenueType, @Param("month") Byte month);
}


