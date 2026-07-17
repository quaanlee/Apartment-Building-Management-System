package com.quan.apartment_building_management_system.dto.systemlog;

public class UtilityBookingLogDTO {
    private Integer bookingId;
    private String resourceName;
    private String startTime;
    private String endTime;
    private java.math.BigDecimal totalPrice;
    private Byte status;
    private Boolean paymentStatus;

    public UtilityBookingLogDTO() {}

    public UtilityBookingLogDTO(Integer bookingId, String resourceName, String startTime, String endTime, java.math.BigDecimal totalPrice, Byte status, Boolean paymentStatus) {
        this.bookingId = bookingId;
        this.resourceName = resourceName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalPrice = totalPrice;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }

    public static UtilityBookingLogDTO fromEntity(com.quan.apartment_building_management_system.entity.UtilityBooking booking) {
        if (booking == null) return null;
        return new UtilityBookingLogDTO(
            booking.getBookingId(),
            booking.getResource() != null ? booking.getResource().getResourceName() : null,
            booking.getStartTime() != null ? booking.getStartTime().toString() : null,
            booking.getEndTime() != null ? booking.getEndTime().toString() : null,
            booking.getTotalPrice(),
            booking.getStatus(),
            booking.getPaymentStatus()
        );
    }

    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }
    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public java.math.BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(java.math.BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public Byte getStatus() { return status; }
    public void setStatus(Byte status) { this.status = status; }
    public Boolean getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(Boolean paymentStatus) { this.paymentStatus = paymentStatus; }
}
