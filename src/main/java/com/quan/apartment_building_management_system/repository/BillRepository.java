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
           "WHERE (:fromDate IS NULL OR b.createdDate >= :fromDate) " +
           "AND (:toDate IS NULL OR b.createdDate <= :toDate)")
    BigDecimal sumTotalByDateRange(@Param("fromDate") LocalDateTime fromDate,
                                   @Param("toDate") LocalDateTime toDate);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b " +
           "WHERE b.status = 1 " +
           "AND (:fromDate IS NULL OR b.createdDate >= :fromDate) " +
           "AND (:toDate IS NULL OR b.createdDate <= :toDate)")
    BigDecimal sumCollectedByDateRange(@Param("fromDate") LocalDateTime fromDate,
                                       @Param("toDate") LocalDateTime toDate);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b " +
           "WHERE b.status = 0 " +
           "AND (:fromDate IS NULL OR b.createdDate >= :fromDate) " +
           "AND (:toDate IS NULL OR b.createdDate <= :toDate)")
    BigDecimal sumOutstandingByDateRange(@Param("fromDate") LocalDateTime fromDate,
                                         @Param("toDate") LocalDateTime toDate);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b " +
           "WHERE b.status = 2 " +
           "AND (:fromDate IS NULL OR b.createdDate >= :fromDate) " +
           "AND (:toDate IS NULL OR b.createdDate <= :toDate)")
    BigDecimal sumOverdueByDateRange(@Param("fromDate") LocalDateTime fromDate,
                                     @Param("toDate") LocalDateTime toDate);

    @Query("SELECT b FROM Bill b " +
           "LEFT JOIN FETCH b.apartment a " +
           "LEFT JOIN FETCH a.residentApartments ra " +
           "LEFT JOIN FETCH ra.profile p " +
           "WHERE (:fromDate IS NULL OR b.createdDate >= :fromDate) " +
           "AND (:toDate IS NULL OR b.createdDate <= :toDate) " +
           "AND (:status IS NULL OR b.status = :status) " +
           "ORDER BY b.createdDate DESC")
    Page<Bill> findBillsWithDetails(@Param("fromDate") LocalDateTime fromDate,
                                    @Param("toDate") LocalDateTime toDate,
                                    @Param("status") Byte status,
                                    Pageable pageable);

    @Query("SELECT b.billMonth as month, SUM(b.totalAmount) as total FROM Bill b WHERE b.billYear = :year GROUP BY b.billMonth ORDER BY b.billMonth")
    List<Object[]> sumByYear(@Param("year") short year);

    @Query("SELECT s.serviceType as type, SUM(bd.amount) as total FROM BillDetail bd JOIN bd.serviceItem s JOIN bd.bill b WHERE (:fromDate IS NULL OR b.createdDate >= :fromDate) AND (:toDate IS NULL OR b.createdDate <= :toDate) GROUP BY s.serviceType ORDER BY SUM(bd.amount) DESC")
    List<Object[]> sumByServiceType(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
}
