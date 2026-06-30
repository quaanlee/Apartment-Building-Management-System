package com.quan.apartment_building_management_system.dto.booking;

import java.math.BigDecimal;

/**
 * Detailed DTO for the Booking Detail popup/modal view.
 * Contains resident information, booking details, and payment details.
 */
public class UtilityBookingDetailDto {

    // Booking meta
    private Integer bookingId;
    private Byte bookingStatus;       // 0=Pending | 1=Approved | 2=Rejected | 3=Cancelled

    // Resident information
    private String residentFullName;
    private String residentPhone;
    private String residentEmail;

    // Booking information
    private String utilityName;
    private String resourceName;
    private String resourceLocation;
    private String startTime;         // formatted display string
    private String endTime;
    private long durationHours;
    private String createdAt;

    // Payment details
    private String paymentStatus;     // "Paid" | "Unpaid"
    private BigDecimal amount;
    private String transactionCode;
    private String approvedByName;

    public UtilityBookingDetailDto() {}

    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }

    public Byte getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(Byte bookingStatus) { this.bookingStatus = bookingStatus; }

    public String getResidentFullName() { return residentFullName; }
    public void setResidentFullName(String residentFullName) { this.residentFullName = residentFullName; }

    public String getResidentPhone() { return residentPhone; }
    public void setResidentPhone(String residentPhone) { this.residentPhone = residentPhone; }

    public String getResidentEmail() { return residentEmail; }
    public void setResidentEmail(String residentEmail) { this.residentEmail = residentEmail; }

    public String getUtilityName() { return utilityName; }
    public void setUtilityName(String utilityName) { this.utilityName = utilityName; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public String getResourceLocation() { return resourceLocation; }
    public void setResourceLocation(String resourceLocation) { this.resourceLocation = resourceLocation; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public long getDurationHours() { return durationHours; }
    public void setDurationHours(long durationHours) { this.durationHours = durationHours; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getTransactionCode() { return transactionCode; }
    public void setTransactionCode(String transactionCode) { this.transactionCode = transactionCode; }

    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }
}
