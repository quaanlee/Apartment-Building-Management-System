package com.quan.apartment_building_management_system.dto.utility;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UtilityBookingHistoryDTO {
    private Integer bookingId;
    private String utilityName;
    private String resourceName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalPrice;
    private Byte status;
    private Boolean paymentStatus;
    private LocalDateTime createdAt;
    
    public UtilityBookingHistoryDTO() {}

    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }

    public String getUtilityName() { return utilityName; }
    public void setUtilityName(String utilityName) { this.utilityName = utilityName; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public Byte getStatus() { return status; }
    public void setStatus(Byte status) { this.status = status; }
    
    public String getStatusLabel() {
        if (status == null) return "Không rõ";
        return switch (status) {
            case 0 -> "Chờ duyệt";
            case 1 -> "Đã duyệt";
            case 2 -> "Từ chối";
            case 3 -> "Đã hủy";
            default -> "Không rõ";
        };
    }

    public Boolean getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(Boolean paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getPaymentStatusLabel() {
        return Boolean.TRUE.equals(paymentStatus) ? "Đã thanh toán" : "Chưa thanh toán";
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
