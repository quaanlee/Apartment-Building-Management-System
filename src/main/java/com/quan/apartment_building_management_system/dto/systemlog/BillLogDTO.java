package com.quan.apartment_building_management_system.dto.systemlog;

public class BillLogDTO {
    private Integer billId;
    private String apartmentNumber;
    private String createdBy;
    private Byte billMonth;
    private Short billYear;
    private java.math.BigDecimal totalAmount;
    private Byte status;
    private String dueDate;
    private String paidDate;

    public BillLogDTO() {}

    public BillLogDTO(Integer billId, String apartmentNumber, String createdBy, Byte billMonth, Short billYear, java.math.BigDecimal totalAmount, Byte status, String dueDate, String paidDate) {
        this.billId = billId;
        this.apartmentNumber = apartmentNumber;
        this.createdBy = createdBy;
        this.billMonth = billMonth;
        this.billYear = billYear;
        this.totalAmount = totalAmount;
        this.status = status;
        this.dueDate = dueDate;
        this.paidDate = paidDate;
    }

    public static BillLogDTO fromEntity(com.quan.apartment_building_management_system.entity.Bill bill) {
        if (bill == null) return null;
        return new BillLogDTO(
            bill.getBillId(),
            bill.getApartment() != null ? bill.getApartment().getApartmentNumber() : null,
            bill.getCreatedBy() != null ? bill.getCreatedBy().getUsername() : null,
            bill.getBillMonth(),
            bill.getBillYear(),
            bill.getTotalAmount(),
            bill.getStatus(),
            bill.getDueDate() != null ? bill.getDueDate().toString() : null,
            bill.getPaidDate() != null ? bill.getPaidDate().toString() : null
        );
    }

    // Getters and Setters
    public Integer getBillId() { return billId; }
    public void setBillId(Integer billId) { this.billId = billId; }
    public String getApartmentNumber() { return apartmentNumber; }
    public void setApartmentNumber(String apartmentNumber) { this.apartmentNumber = apartmentNumber; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public Byte getBillMonth() { return billMonth; }
    public void setBillMonth(Byte billMonth) { this.billMonth = billMonth; }
    public Short getBillYear() { return billYear; }
    public void setBillYear(Short billYear) { this.billYear = billYear; }
    public java.math.BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(java.math.BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public Byte getStatus() { return status; }
    public void setStatus(Byte status) { this.status = status; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public String getPaidDate() { return paidDate; }
    public void setPaidDate(String paidDate) { this.paidDate = paidDate; }
}
