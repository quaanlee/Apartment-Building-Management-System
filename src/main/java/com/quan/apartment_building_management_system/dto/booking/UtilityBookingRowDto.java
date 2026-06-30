package com.quan.apartment_building_management_system.dto.booking;

/**
 * Flat DTO representing a single row in the Utility Booking management table.
 * All datetime fields are pre-formatted strings for direct display.
 */
public class UtilityBookingRowDto {

    private Integer bookingId;
    private String residentName;
    private String initials;          // e.g. "JD" for Jameson Douglas
    private String createdAt;         // e.g. "Oct 10, 2024 · 09:30"
    private String utilityName;       // e.g. "BBQ Area"
    private String startTime;         // e.g. "Oct 12, 2024 · 16:00"
    private String endTime;
    private long durationHours;
    private Byte bookingStatus;       // 0=Pending | 1=Approved | 2=Rejected | 3=Cancelled
    private String paymentStatus;     // "Paid" | "Unpaid"
    private String approvedByName;

    public UtilityBookingRowDto() {}

    public UtilityBookingRowDto(Integer bookingId, String residentName, String initials,
                                 String createdAt, String utilityName, String startTime,
                                 String endTime, long durationHours, Byte bookingStatus,
                                 String paymentStatus, String approvedByName) {
        this.bookingId = bookingId;
        this.residentName = residentName;
        this.initials = initials;
        this.createdAt = createdAt;
        this.utilityName = utilityName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationHours = durationHours;
        this.bookingStatus = bookingStatus;
        this.paymentStatus = paymentStatus;
        this.approvedByName = approvedByName;
    }

    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }

    public String getResidentName() { return residentName; }
    public void setResidentName(String residentName) { this.residentName = residentName; }

    public String getInitials() { return initials; }
    public void setInitials(String initials) { this.initials = initials; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUtilityName() { return utilityName; }
    public void setUtilityName(String utilityName) { this.utilityName = utilityName; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public long getDurationHours() { return durationHours; }
    public void setDurationHours(long durationHours) { this.durationHours = durationHours; }

    public Byte getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(Byte bookingStatus) { this.bookingStatus = bookingStatus; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }
}
