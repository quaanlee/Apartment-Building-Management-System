package com.quan.apartment_building_management_system.dto.systemlog;

import java.math.BigDecimal;

public class PaymentLogDTO {
    private Integer paymentId;
    private Integer billId;
    private String apartmentNumber;
    private String paidByUsername;
    private String paymentMethod;
    private String transactionCode;
    private BigDecimal amount;
    private String paymentDate;
    private Byte status;

    public PaymentLogDTO() {}

    public PaymentLogDTO(Integer paymentId, Integer billId, String apartmentNumber,
                         String paidByUsername, String paymentMethod, String transactionCode,
                         BigDecimal amount, String paymentDate, Byte status) {
        this.paymentId = paymentId;
        this.billId = billId;
        this.apartmentNumber = apartmentNumber;
        this.paidByUsername = paidByUsername;
        this.paymentMethod = paymentMethod;
        this.transactionCode = transactionCode;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;
    }

    public static PaymentLogDTO fromEntity(com.quan.apartment_building_management_system.entity.Payment payment) {
        if (payment == null) return new PaymentLogDTO();
        return new PaymentLogDTO(
                payment.getPaymentId(),
                payment.getBill() != null ? payment.getBill().getBillId() : null,
                (payment.getBill() != null && payment.getBill().getApartment() != null)
                        ? payment.getBill().getApartment().getApartmentNumber() : null,
                payment.getPaidBy() != null ? payment.getPaidBy().getUsername() : null,
                payment.getPaymentMethod() != null ? payment.getPaymentMethod().getMethodName() : null,
                payment.getTransactionCode(),
                payment.getAmount(),
                payment.getPaymentDate() != null ? payment.getPaymentDate().toString() : null,
                payment.getStatus()
        );
    }

    public static PaymentLogDTO empty() {
        return new PaymentLogDTO();
    }

    public Integer getPaymentId() { return paymentId; }
    public void setPaymentId(Integer paymentId) { this.paymentId = paymentId; }
    public Integer getBillId() { return billId; }
    public void setBillId(Integer billId) { this.billId = billId; }
    public String getApartmentNumber() { return apartmentNumber; }
    public void setApartmentNumber(String apartmentNumber) { this.apartmentNumber = apartmentNumber; }
    public String getPaidByUsername() { return paidByUsername; }
    public void setPaidByUsername(String paidByUsername) { this.paidByUsername = paidByUsername; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getTransactionCode() { return transactionCode; }
    public void setTransactionCode(String transactionCode) { this.transactionCode = transactionCode; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
    public Byte getStatus() { return status; }
    public void setStatus(Byte status) { this.status = status; }
}
