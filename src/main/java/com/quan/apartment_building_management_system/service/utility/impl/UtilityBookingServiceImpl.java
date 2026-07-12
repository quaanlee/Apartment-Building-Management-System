package com.quan.apartment_building_management_system.service.utility.impl;

import com.quan.apartment_building_management_system.dto.booking.UtilityBookingDetailDto;
import com.quan.apartment_building_management_system.dto.booking.UtilityBookingFilterRequest;
import com.quan.apartment_building_management_system.dto.booking.UtilityBookingRowDto;
import com.quan.apartment_building_management_system.dto.booking.UtilityBookingStatsDto;
import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.BillDetail;
import com.quan.apartment_building_management_system.entity.UtilityBooking;
import com.quan.apartment_building_management_system.repository.UtilityBookingRepository;
import com.quan.apartment_building_management_system.repository.specification.UtilityBookingSpecification;
import com.quan.apartment_building_management_system.service.utility.UtilityBookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UtilityBookingServiceImpl implements UtilityBookingService {

    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd, yyyy · HH:mm");
    private static final DateTimeFormatter DATE_INPUT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final UtilityBookingRepository utilityBookingRepository;

    public UtilityBookingServiceImpl(UtilityBookingRepository utilityBookingRepository) {
        this.utilityBookingRepository = utilityBookingRepository;
    }

    private static boolean autoApproveEnabled = false;

    // ── Global settings ──────────────────────────────────────────────────────────

    @Override
    public boolean isAutoApproveEnabled() {
        return autoApproveEnabled;
    }

    @Override
    @Transactional
    public void setAutoApproveEnabled(boolean enabled, Account actor) {
        autoApproveEnabled = enabled;
        if (enabled) {
            // Auto approve all pending bookings that are from now into the future
            LocalDateTime now = LocalDateTime.now();
            List<UtilityBooking> pendingFutureBookings = utilityBookingRepository
                    .findByStatusAndStartTimeGreaterThanEqual((byte) 0, now);
            for (UtilityBooking booking : pendingFutureBookings) {
                booking.setStatus((byte) 1);
                booking.setApprovedBy(actor);
                utilityBookingRepository.save(booking);
            }
        }
    }

    // ── Existing methods ─────────────────────────────────────────────────────────

    @Override
    public List<UtilityBooking> findAll() {
        return utilityBookingRepository.findAll();
    }

    @Override
    public Optional<UtilityBooking> findById(Integer id) {
        return utilityBookingRepository.findById(id);
    }

    @Override
    public List<UtilityBooking> findByProfileId(Integer profileId) {
        return utilityBookingRepository.findByProfileProfileId(profileId);
    }

    @Override
    public List<UtilityBooking> findByResourceId(Integer resourceId) {
        return utilityBookingRepository.findByResourceResourceId(resourceId);
    }

    @Override
    @Transactional
    public UtilityBooking save(UtilityBooking utilityBooking) {
        return utilityBookingRepository.save(utilityBooking);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        utilityBookingRepository.deleteById(id);
    }

    // ── Manager Booking Management methods ───────────────────────────────────────

    @Override
    public Page<UtilityBookingRowDto> findFiltered(UtilityBookingFilterRequest filter) {
        Specification<UtilityBooking> spec = buildSpecification(filter);
        PageRequest pageRequest = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return utilityBookingRepository.findAll(spec, pageRequest).map(this::toRowDto);
    }

    @Override
    public UtilityBookingStatsDto getStats() {
        long total = utilityBookingRepository.count();
        long pending = utilityBookingRepository.countByBookingStatus((byte) 0);
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        long todaySchedule = utilityBookingRepository.countTodaySchedule(startOfDay, endOfDay);
        return new UtilityBookingStatsDto(total, pending, todaySchedule);
    }

    @Override
    public UtilityBookingDetailDto getDetail(Integer bookingId) {
        UtilityBooking booking = utilityBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking #" + bookingId + " not found"));
        return toDetailDto(booking);
    }

    @Override
    @Transactional
    public void updateStatus(Integer bookingId, Byte newStatus, Account actor) {
        UtilityBooking booking = utilityBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking #" + bookingId + " not found"));
        booking.setStatus(newStatus);
        if (newStatus == 1) {               // Approved
            booking.setApprovedBy(actor);
            booking.setCancelledAt(null);
            booking.setCancelReason(null);
        } else if (newStatus == 2) {        // Rejected
            booking.setApprovedBy(null);
            booking.setCancelledAt(null);
            booking.setCancelReason(null);
        } else if (newStatus == 3) {        // Cancelled
            booking.setCancelledAt(LocalDateTime.now());
            booking.setApprovedBy(null);
        } else if (newStatus == 0) {        // Cancel Approve/Reject -> revert to Pending
            booking.setApprovedBy(null);
            booking.setCancelledAt(null);
            booking.setCancelReason(null);
        }
        utilityBookingRepository.save(booking);
    }

    // ── Private helpers ──────────────────────────────────────────────────────────

    private Specification<UtilityBooking> buildSpecification(UtilityBookingFilterRequest filter) {
        Specification<UtilityBooking> spec = (root, query, cb) -> cb.conjunction();

        spec = spec.and(UtilityBookingSpecification.hasResidentName(filter.getResidentName()));
        spec = spec.and(UtilityBookingSpecification.hasBookingStatus(filter.getBookingStatus()));
        spec = spec.and(UtilityBookingSpecification.hasUtility(filter.getUtilityId()));
        spec = spec.and(UtilityBookingSpecification.hasPaymentStatus(filter.getPaymentStatus()));
        spec = spec.and(UtilityBookingSpecification.startTimeFrom(parseDate(filter.getStartTimeFrom(), false)));
        spec = spec.and(UtilityBookingSpecification.startTimeTo(parseDate(filter.getStartTimeTo(), true)));
        spec = spec.and(UtilityBookingSpecification.createdAtFrom(parseDate(filter.getCreatedAtFrom(), false)));
        spec = spec.and(UtilityBookingSpecification.createdAtTo(parseDate(filter.getCreatedAtTo(), true)));

        return spec;
    }

    /** Parse a yyyy-MM-dd string to LocalDateTime.
     *  @param endOfDay if true, returns the end of the day (23:59:59) to make the range inclusive. */
    private LocalDateTime parseDate(String dateStr, boolean endOfDay) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            LocalDate date = LocalDate.parse(dateStr, DATE_INPUT_FORMATTER);
            return endOfDay ? date.atTime(23, 59, 59) : date.atStartOfDay();
        } catch (Exception e) {
            return null;
        }
    }

    private UtilityBookingRowDto toRowDto(UtilityBooking booking) {
        String residentName = booking.getProfile().getFullName();
        String utilityName  = booking.getResource().getUtility().getUtilityName();
        long durationHours  = ChronoUnit.HOURS.between(booking.getStartTime(), booking.getEndTime());
        String paymentStatus = resolvePaymentStatus(booking);
        String approvedByName = resolveApprovedByName(booking);

        return new UtilityBookingRowDto(
                booking.getBookingId(),
                residentName,
                buildInitials(residentName),
                format(booking.getCreatedAt()),
                utilityName,
                format(booking.getStartTime()),
                format(booking.getEndTime()),
                durationHours,
                booking.getStatus() != null ? booking.getStatus().intValue() : 0,
                paymentStatus,
                approvedByName
        );
    }

    private UtilityBookingDetailDto toDetailDto(UtilityBooking booking) {
        UtilityBookingDetailDto dto = new UtilityBookingDetailDto();

        dto.setBookingId(booking.getBookingId());
        dto.setBookingStatus(booking.getStatus() != null ? booking.getStatus().intValue() : 0);

        // Resident
        dto.setResidentFullName(booking.getProfile().getFullName());
        dto.setResidentPhone(booking.getProfile().getPhoneNumber());
        dto.setResidentEmail(booking.getProfile().getEmail());

        // Booking info
        dto.setUtilityName(booking.getResource().getUtility().getUtilityName());
        dto.setResourceName(booking.getResource().getResourceName());
        dto.setResourceLocation(booking.getResource().getLocation());
        dto.setStartTime(format(booking.getStartTime()));
        dto.setEndTime(format(booking.getEndTime()));
        dto.setDurationHours(ChronoUnit.HOURS.between(booking.getStartTime(), booking.getEndTime()));
        dto.setCreatedAt(format(booking.getCreatedAt()));

        // Payment
        dto.setPaymentStatus(resolvePaymentStatus(booking));
        resolvePaymentDetails(booking, dto);

        dto.setApprovedByName(resolveApprovedByName(booking));

        return dto;
    }

    /** Determines payment status by checking if any associated bill has been paid (Bill.status = 1). */
    private String resolvePaymentStatus(UtilityBooking booking) {
        return (booking.getPaymentStatus() != null && booking.getPaymentStatus()) ? "Paid" : "Unpaid";
    }

    /** Extracts payment amount and transaction code from associated bill payments. */
    private void resolvePaymentDetails(UtilityBooking booking, UtilityBookingDetailDto dto) {
        if (booking.getBillDetails() == null) return;
        booking.getBillDetails().stream()
                .filter(bd -> bd.getBill() != null)
                .findFirst()
                .ifPresent(bd -> {
                    dto.setAmount(bd.getAmount());
                    bd.getBill().getPayments().stream()
                            .filter(p -> p.getStatus() != null && p.getStatus() == 1)
                            .findFirst()
                            .ifPresent(p -> dto.setTransactionCode(p.getTransactionCode()));
                });
    }

    private String resolveApprovedByName(UtilityBooking booking) {
        Account approver = booking.getApprovedBy();
        if (approver == null) return null;
        if (approver.getProfile() != null && approver.getProfile().getFullName() != null) {
            return approver.getProfile().getFullName();
        }
        return approver.getUsername();
    }

    /** Builds 2-letter initials from a full name (first char of first + last word). */
    private String buildInitials(String fullName) {
        if (fullName == null || fullName.isBlank()) return "??";
        String[] parts = fullName.trim().split("\\s+");
        char first = Character.toUpperCase(parts[0].charAt(0));
        char last  = Character.toUpperCase(parts[parts.length - 1].charAt(0));
        return String.valueOf(first) + last;
    }

    private String format(LocalDateTime dt) {
        return dt != null ? dt.format(DISPLAY_FORMATTER) : "";
    }
}
