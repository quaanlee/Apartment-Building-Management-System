package com.quan.apartment_building_management_system.service.utility;

import com.quan.apartment_building_management_system.dto.booking.UtilityBookingDetailDto;
import com.quan.apartment_building_management_system.dto.booking.UtilityBookingFilterRequest;
import com.quan.apartment_building_management_system.dto.booking.UtilityBookingRowDto;
import com.quan.apartment_building_management_system.dto.booking.UtilityBookingStatsDto;
import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.UtilityBooking;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UtilityBookingService {

    // ── Existing methods ────────────────────────────────────────────────────────

    List<UtilityBooking> findAll();

    Optional<UtilityBooking> findById(Integer id);

    List<UtilityBooking> findByProfileId(Integer profileId);

    List<UtilityBooking> findByResourceId(Integer resourceId);

    UtilityBooking save(UtilityBooking utilityBooking);

    void deleteById(Integer id);

    // ── Manager Booking Management methods ──────────────────────────────────────

    /** Returns a paginated, filtered list of bookings as row DTOs for the management table. */
    Page<UtilityBookingRowDto> findFiltered(UtilityBookingFilterRequest filter);

    /** Returns summary stats: total, pending approvals, today's schedule count. */
    UtilityBookingStatsDto getStats();

    /** Returns detailed booking information for the view modal. */
    UtilityBookingDetailDto getDetail(Integer bookingId);

    /**
     * Updates the booking status.
     * @param bookingId target booking ID
     * @param newStatus 0=Pending | 1=Approved | 2=Rejected | 3=Cancelled
     * @param actor     the manager/admin account performing the action
     */
    void updateStatus(Integer bookingId, Byte newStatus, Account actor);
}
