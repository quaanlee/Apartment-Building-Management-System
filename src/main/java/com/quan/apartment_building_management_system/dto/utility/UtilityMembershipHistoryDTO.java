package com.quan.apartment_building_management_system.dto.utility;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class UtilityMembershipHistoryDTO {
    private Integer membershipId;
    private String utilityName;
    private String packageName;
    private BigDecimal price;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean status;
    private Boolean paymentStatus;
    private LocalDateTime createdAt;
    
    public UtilityMembershipHistoryDTO() {}

    public Integer getMembershipId() { return membershipId; }
    public void setMembershipId(Integer membershipId) { this.membershipId = membershipId; }

    public String getUtilityName() { return utilityName; }
    public void setUtilityName(String utilityName) { this.utilityName = utilityName; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    public String getStatusLabel() {
        if (Boolean.TRUE.equals(status) && LocalDate.now().isBefore(endDate.plusDays(1))) {
            return "Đang hoạt động";
        }
        return "Hết hạn";
    }

    public Boolean getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(Boolean paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getPaymentStatusLabel() {
        return Boolean.TRUE.equals(paymentStatus) ? "Đã thanh toán" : "Chưa thanh toán";
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
