package com.quan.apartment_building_management_system.service.utility;

import com.quan.apartment_building_management_system.dto.utility.BookingRequestDTO;
import com.quan.apartment_building_management_system.dto.utility.UtilityBookingHistoryDTO;
import com.quan.apartment_building_management_system.dto.utility.UtilityDTO;
import com.quan.apartment_building_management_system.dto.utility.UtilityMembershipHistoryDTO;
import com.quan.apartment_building_management_system.entity.*;
import com.quan.apartment_building_management_system.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResidentUtilityService {

    private final UtilityRepository utilityRepository;
    private final UtilityResourceRepository resourceRepository;
    private final UtilityPriceRepository priceRepository;
    private final UtilityBookingRepository bookingRepository;
    private final UtilityMembershipRepository membershipRepository;
    private final ProfileRepository profileRepository;
    private final UtilityImageRepository imageRepository;
    private final UtilityBookingService utilityBookingService;
    private final com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService;

    public ResidentUtilityService(UtilityRepository utilityRepository,
            UtilityResourceRepository resourceRepository,
            UtilityPriceRepository priceRepository,
            UtilityBookingRepository bookingRepository,
            UtilityMembershipRepository membershipRepository,
            ProfileRepository profileRepository,
            UtilityImageRepository imageRepository,
            UtilityBookingService utilityBookingService,
            com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService) {
        this.utilityRepository = utilityRepository;
        this.resourceRepository = resourceRepository;
        this.priceRepository = priceRepository;
        this.bookingRepository = bookingRepository;
        this.membershipRepository = membershipRepository;
        this.profileRepository = profileRepository;
        this.imageRepository = imageRepository;
        this.utilityBookingService = utilityBookingService;
        this.systemLogService = systemLogService;
    }

    public List<UtilityDTO> getActiveUtilities(String query) {
        String normalizedQuery = removeAccents(query != null ? query.trim().toLowerCase() : "");
        return utilityRepository.findAll().stream()
                .filter(Utility::getStatus)
                .filter(u -> {
                    if (normalizedQuery.isEmpty()) return true;
                    String name = u.getUtilityName() != null ? removeAccents(u.getUtilityName().toLowerCase()) : "";
                    return name.contains(normalizedQuery);
                })
                .map(u -> {
                    UtilityDTO dto = new UtilityDTO(u.getUtilityId(), u.getUtilityName(), u.getDescription(), u.getStatus(), u.getType());
                    dto.setImageUrl(u.getImageUrl());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private String removeAccents(String s) {
        if (s == null) return "";
        String temp = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }

    public UtilityDTO getUtility(Integer utilityId) {
        Utility u = utilityRepository.findById(utilityId)
                .orElseThrow(() -> new IllegalArgumentException("Utility not found"));
        UtilityDTO dto = new UtilityDTO(u.getUtilityId(), u.getUtilityName(), u.getDescription(), u.getStatus(), u.getType());
        dto.setImageUrl(u.getImageUrl());
        return dto;
    }

    public List<UtilityDTO.Resource> getActiveResources(Integer utilityId) {
        return resourceRepository.findByUtilityUtilityId(utilityId).stream()
                .filter(UtilityResource::getStatus)
                .map(r -> new UtilityDTO.Resource(
                        r.getResourceId(), r.getUtility().getUtilityId(), r.getUtility().getUtilityName(),
                        r.getResourceName(), r.getLocation(), r.getStatus()))
                .collect(Collectors.toList());
    }

    public UtilityDTO.Resource getResourceDetail(Integer resourceId) {
        UtilityResource r = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found"));

        List<UtilityImage> images = imageRepository.findByResourceId(resourceId);
        String primaryImage = images.stream().filter(UtilityImage::getPrimary).map(UtilityImage::getImageUrl)
                .findFirst().orElse(null);
        List<String> secondaryImages = images.stream().filter(img -> !img.getPrimary()).map(UtilityImage::getImageUrl)
                .collect(Collectors.toList());

        List<UtilityDTO.Price> prices = priceRepository.findByResourceResourceId(resourceId).stream()
                .map(p -> new UtilityDTO.Price(p.getUtilityPriceId(), null, null,
                        new UtilityDTO.Unit(p.getUnit().getUnitId(), p.getUnit().getUnitName()), p.getPrice()))
                .collect(Collectors.toList());

        return new UtilityDTO.Resource(
                r.getResourceId(), r.getUtility().getUtilityId(), r.getUtility().getUtilityName(),
                r.getResourceName(), r.getLocation(), r.getDescription(), r.getStatus(),
                primaryImage, secondaryImages, prices);
    }

    public boolean hasActiveMembership(Integer accountId, Integer utilityId) {
        Profile profile = profileRepository.findByAccountAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
        return membershipRepository
                .existsByProfileProfileIdAndUtilityUtilityIdAndStatusAndPaymentStatusAndEndDateGreaterThanEqual(
                        profile.getProfileId(), utilityId, true, true, LocalDate.now());
    }

    public UtilityMembership getActiveMembership(Integer accountId, Integer utilityId) {
        Profile profile = profileRepository.findByAccountAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
        return membershipRepository
                .findFirstByProfileProfileIdAndUtilityUtilityIdAndStatusAndPaymentStatusAndEndDateGreaterThanEqual(
                        profile.getProfileId(), utilityId, true, true, LocalDate.now());
    }

    @Transactional
    public UtilityBooking submitBookingRequest(Integer accountId, BookingRequestDTO req) {
        Profile profile = profileRepository.findByAccountAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        Utility utility = utilityRepository.findById(req.getUtilityId())
                .orElseThrow(() -> new IllegalArgumentException("Utility not found"));

        UtilityResource resource = resourceRepository.findById(req.getResourceId())
                .orElseThrow(() -> new IllegalArgumentException("Resource not found"));

        UtilityPrice price = priceRepository.findById(req.getPriceId())
                .orElseThrow(() -> new IllegalArgumentException("Price not found"));

        if (!utility.getStatus())
            throw new IllegalArgumentException("Utility is inactive");
        if (!resource.getStatus())
            throw new IllegalArgumentException("Resource is under maintenance");

        if (Boolean.TRUE.equals(utility.getType())) {
            // RESERVABLE
            LocalDateTime start = req.getBookingDate().atTime(req.getStartTime());
            LocalDateTime end = req.getBookingDate().atTime(req.getEndTime());

            validateBookingTime(resource.getResourceId(), start, end);

            // Calculate total price based on duration and unit price
            BigDecimal durationHours = BigDecimal.valueOf(Duration.between(start, end).toMinutes() / 60.0);
            BigDecimal totalPrice = price.getPrice().multiply(durationHours);

            UtilityBooking booking = new UtilityBooking();
            booking.setProfile(profile);
            booking.setResource(resource);
            booking.setUtilityPrice(price);
            booking.setStartTime(start);
            booking.setEndTime(end);

            boolean hasMembership = hasActiveMembership(accountId, req.getUtilityId());
            if (hasMembership) {
                booking.setTotalPrice(BigDecimal.ZERO);
                booking.setPaymentStatus(true);
            } else {
                booking.setTotalPrice(totalPrice);
                // Both Cash and Online start as Unpaid
                booking.setPaymentStatus(false);
            }

            if (utilityBookingService.isAutoApproveEnabled()) {
                booking.setStatus((byte) 1); // Approved
            } else {
                booking.setStatus((byte) 0); // Pending
            }
            booking.setCreatedAt(LocalDateTime.now());
            return bookingRepository.save(booking);

        } else {
            // FREE_USE
            String unitName = price.getUnit().getUnitName().toLowerCase();
            if (unitName.contains("hour") || unitName.contains("giờ") || unitName.contains("day")
                    || unitName.contains("ngày")) {
                UtilityBooking booking = new UtilityBooking();
                booking.setProfile(profile);
                booking.setResource(resource);
                booking.setUtilityPrice(price);
                booking.setStartTime(LocalDateTime.now());
                if (unitName.contains("hour") || unitName.contains("giờ")) {
                    booking.setEndTime(LocalDateTime.now().plusHours(1));
                } else {
                    booking.setEndTime(LocalDateTime.now().plusDays(1));
                }
                booking.setTotalPrice(price.getPrice());
                booking.setStatus((byte) 0); // Pending
                booking.setPaymentStatus(false);
                booking.setCreatedAt(LocalDateTime.now());
                bookingRepository.save(booking);
            } else {
                UtilityMembership membership = new UtilityMembership();
                membership.setProfile(profile);
                membership.setUtility(utility);
                membership.setUtilityPrice(price);
                membership.setStartDate(LocalDate.now());
                if (unitName.contains("month") || unitName.contains("tháng")) {
                    membership.setEndDate(LocalDate.now().plusMonths(1));
                } else {
                    membership.setEndDate(LocalDate.now().plusYears(1));
                }
                membership.setStatus(true); // Active immediately for membership
                membership.setPaymentStatus(false);
                membership.setCreatedAt(LocalDateTime.now());
                membershipRepository.save(membership);
            }
        }
        return null;
    }

    @Transactional
    public UtilityBooking submitBookingByManager(BookingRequestDTO req) {
        Profile profile = profileRepository.findByPhoneNumber(req.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cư dân với số điện thoại này."));

        if (profile.getAccount() == null) {
            throw new IllegalArgumentException("Cư dân này chưa có tài khoản hệ thống.");
        }

        return submitBookingRequest(profile.getAccount().getAccountId(), req);
    }

    public List<UtilityBooking> getBookingsForDate(Integer resourceId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        return bookingRepository.findByResourceResourceIdAndStatusInAndStartTimeBetween(
                resourceId, List.of((byte) 0, (byte) 1), startOfDay, endOfDay);
    }

    public void validateBookingTime(Integer resourceId, LocalDateTime start, LocalDateTime end) {
        if (start.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Thời gian đặt bắt đầu không được ở trong quá khứ.");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Thời gian kết thúc phải sau thời gian bắt đầu.");
        }

        // Add operating hours validation (06:00 to 22:00)
        int startHour = start.getHour();
        int endHour = end.getHour();
        int endMinute = end.getMinute();

        if (startHour < 6 || startHour >= 22 || endHour < 6 || (endHour > 22 || (endHour == 22 && endMinute > 0))) {
            throw new IllegalArgumentException("Giờ hoạt động của tiện ích là từ 06:00 đến 22:00.");
        }

        boolean hasOverlap = bookingRepository
                .existsByResourceResourceIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                        resourceId, List.of((byte) 0, (byte) 1), end, start);
        if (hasOverlap) {
            throw new IllegalArgumentException("Khung giờ này đã được đặt.");
        }
    }

    public List<UtilityBookingHistoryDTO> getBookingHistory(Integer accountId) {
        Profile profile = profileRepository.findByAccountAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        return bookingRepository.findByProfileProfileId(profile.getProfileId()).stream()
                .map(b -> {
                    UtilityBookingHistoryDTO dto = new UtilityBookingHistoryDTO();
                    dto.setBookingId(b.getBookingId());
                    dto.setUtilityName(b.getResource().getUtility().getUtilityName());
                    dto.setResourceName(b.getResource().getResourceName());
                    dto.setStartTime(b.getStartTime());
                    dto.setEndTime(b.getEndTime());
                    dto.setTotalPrice(b.getTotalPrice());
                    dto.setStatus(b.getStatus());
                    dto.setPaymentStatus(b.getPaymentStatus());
                    dto.setCreatedAt(b.getCreatedAt());
                    return dto;
                })
                .sorted((d1, d2) -> d2.getCreatedAt().compareTo(d1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public List<UtilityMembershipHistoryDTO> getMembershipHistory(Integer accountId) {
        Profile profile = profileRepository.findByAccountAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        return membershipRepository.findByProfileProfileId(profile.getProfileId()).stream()
                .map(m -> {
                    UtilityMembershipHistoryDTO dto = new UtilityMembershipHistoryDTO();
                    dto.setMembershipId(m.getMembershipId());
                    dto.setUtilityName(m.getUtility().getUtilityName());
                    dto.setPackageName(m.getUtilityPrice().getUnit().getUnitName());
                    dto.setPrice(m.getUtilityPrice().getPrice());
                    dto.setStartDate(m.getStartDate());
                    dto.setEndDate(m.getEndDate());
                    dto.setStatus(m.getStatus());
                    dto.setPaymentStatus(m.getPaymentStatus());
                    dto.setCreatedAt(m.getCreatedAt());
                    return dto;
                })
                .sorted((d1, d2) -> d2.getCreatedAt().compareTo(d1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelBooking(Integer accountId, Integer bookingId, String reason) {
        Profile profile = profileRepository.findByAccountAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        UtilityBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getProfile().getProfileId().equals(profile.getProfileId())) {
            throw new IllegalArgumentException("You don't have permission to cancel this booking");
        }

        if (booking.getStatus() != 0) { // Not Pending
            throw new IllegalArgumentException("Only Pending bookings can be cancelled");
        }

        com.quan.apartment_building_management_system.dto.systemlog.UtilityBookingLogDTO oldDto = com.quan.apartment_building_management_system.dto.systemlog.UtilityBookingLogDTO
                .fromEntity(booking);

        booking.setStatus((byte) 3); // Cancelled
        booking.setCancelledAt(LocalDateTime.now());
        booking.setCancelReason(reason);
        UtilityBooking saved = bookingRepository.save(booking);

        com.quan.apartment_building_management_system.dto.systemlog.UtilityBookingLogDTO newDto = com.quan.apartment_building_management_system.dto.systemlog.UtilityBookingLogDTO
                .fromEntity(saved);
        String resourceName = saved.getResource() != null ? saved.getResource().getResourceName() : "Unknown";
        systemLogService.logSystemAction("CANCEL_BOOKING", "UtilityBooking", saved.getBookingId(),
                oldDto, newDto, "Resident cancelled booking for " + resourceName + ". Reason: " + reason);
    }
}
