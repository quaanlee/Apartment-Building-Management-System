package com.quan.apartment_building_management_system.repository.specification;

import com.quan.apartment_building_management_system.entity.BillDetail;
import com.quan.apartment_building_management_system.entity.UtilityBooking;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

/**
 * JPA Specifications for dynamic filtering of UtilityBooking queries.
 * All conditions are combined with AND via Specification.and().
 */
public class UtilityBookingSpecification {

    private UtilityBookingSpecification() {}

    /** Filter by resident's full name (case-insensitive, contains match). */
    public static Specification<UtilityBooking> hasResidentName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return cb.conjunction();
            String pattern = "%" + name.toLowerCase().trim() + "%";
            return cb.like(cb.lower(root.join("profile").get("fullName")), pattern);
        };
    }

    /** Filter by booking status. */
    public static Specification<UtilityBooking> hasBookingStatus(Byte status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction();
            return cb.equal(root.get("status"), status);
        };
    }

    /** Filter by utility ID (through resource → utility chain). */
    public static Specification<UtilityBooking> hasUtility(Integer utilityId) {
        return (root, query, cb) -> {
            if (utilityId == null) return cb.conjunction();
            return cb.equal(root.join("resource").join("utility").get("utilityId"), utilityId);
        };
    }

    /** Filter startTime >= from (inclusive). */
    public static Specification<UtilityBooking> startTimeFrom(LocalDateTime from) {
        return (root, query, cb) -> {
            if (from == null) return cb.conjunction();
            return cb.greaterThanOrEqualTo(root.get("startTime"), from);
        };
    }

    /** Filter startTime <= to (inclusive, end of day). */
    public static Specification<UtilityBooking> startTimeTo(LocalDateTime to) {
        return (root, query, cb) -> {
            if (to == null) return cb.conjunction();
            return cb.lessThanOrEqualTo(root.get("startTime"), to);
        };
    }

    /** Filter createdAt >= from (inclusive). */
    public static Specification<UtilityBooking> createdAtFrom(LocalDateTime from) {
        return (root, query, cb) -> {
            if (from == null) return cb.conjunction();
            return cb.greaterThanOrEqualTo(root.get("createdAt"), from);
        };
    }

    /** Filter createdAt <= to (inclusive, end of day). */
    public static Specification<UtilityBooking> createdAtTo(LocalDateTime to) {
        return (root, query, cb) -> {
            if (to == null) return cb.conjunction();
            return cb.lessThanOrEqualTo(root.get("createdAt"), to);
        };
    }

    /**
     * Filter by payment status using an EXISTS subquery on BillDetail.
     * "Paid"   → booking has at least one BillDetail whose Bill.status = 1
     * "Unpaid" → booking has no such BillDetail
     */
    public static Specification<UtilityBooking> hasPaymentStatus(String paymentStatus) {
        return (root, query, cb) -> {
            if (paymentStatus == null || paymentStatus.isBlank()) return cb.conjunction();

            Subquery<Integer> sub = query.subquery(Integer.class);
            Root<BillDetail> bdRoot = sub.from(BillDetail.class);
            sub.select(bdRoot.get("billDetailId"))
               .where(
                   cb.equal(bdRoot.get("booking"), root),
                   cb.equal(bdRoot.get("bill").get("status"), (byte) 1)
               );

            return "paid".equalsIgnoreCase(paymentStatus)
                    ? cb.exists(sub)
                    : cb.not(cb.exists(sub));
        };
    }
}
