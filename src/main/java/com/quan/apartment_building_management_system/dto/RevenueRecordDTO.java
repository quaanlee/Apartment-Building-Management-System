package com.quan.apartment_building_management_system.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RevenueRecordDTO {
    private String invoiceId;
    private String residentName;
    private String initials;
    private String unitId;
    private String revenueType;
    private String amount;
    private String status;
    private String dueDate;
    private String paidDate;
    private String note;

    public RevenueRecordDTO() {}

    public RevenueRecordDTO(String invoiceId, String residentName, String initials,
                            String unitId, String revenueType, String amount,
                            String status, String dueDate, String paidDate, String note) {
        this.invoiceId = invoiceId;
        this.residentName = residentName;
        this.initials = initials;
        this.unitId = unitId;
        this.revenueType = revenueType;
        this.amount = amount;
        this.status = status;
        this.dueDate = dueDate;
        this.paidDate = paidDate;
        this.note = note;
    }

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
    public String getResidentName() { return residentName; }
    public void setResidentName(String residentName) { this.residentName = residentName; }
    public String getInitials() { return initials; }
    public void setInitials(String initials) { this.initials = initials; }
    public String getUnitId() { return unitId; }
    public void setUnitId(String unitId) { this.unitId = unitId; }
    public String getRevenueType() { return revenueType; }
    public void setRevenueType(String revenueType) { this.revenueType = revenueType; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public String getPaidDate() { return paidDate; }
    public void setPaidDate(String paidDate) { this.paidDate = paidDate; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
