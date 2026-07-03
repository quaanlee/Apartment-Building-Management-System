package com.quan.apartment_building_management_system.repository;

import com.quan.apartment_building_management_system.entity.UtilityBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UtilityBookingRepository
        extends JpaRepository<UtilityBooking, Integer>,
                JpaSpecificationExecutor<UtilityBooking> {

    List<UtilityBooking> findByProfileProfileId(Integer profileId);

    List<UtilityBooking> findByResourceResourceId(Integer resourceId);

    /** Count bookings by their status byte value. */
    @Query("SELECT COUNT(ub) FROM UtilityBooking ub WHERE ub.status = :status")
    long countByBookingStatus(@Param("status") Byte status);

    /** Count bookings whose startTime falls within today (used for Today's Schedule stat). */
    @Query("SELECT COUNT(ub) FROM UtilityBooking ub WHERE ub.startTime >= :startOfDay AND ub.startTime < :endOfDay")
    long countTodaySchedule(@Param("startOfDay") LocalDateTime startOfDay,
                            @Param("endOfDay") LocalDateTime endOfDay);
}
