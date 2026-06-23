package com.quan.apartment_building_management_system.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "SalesContract")
public class SalesContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SalesContractID")
    private Integer salesContractID;

    @Column(name = "ApartmentID", nullable = false)
    private Integer apartmentID;

    @Column(name = "ProfileID", nullable = false)
    private Integer profileID;

    @Column(name = "SellingPrice", nullable = false, precision = 18, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "MaintenanceFee", nullable = false, precision = 18, scale = 2)
    private BigDecimal maintenanceFee = BigDecimal.ZERO;

    @Column(name = "HandoverDate")
    private LocalDate handoverDate;

    @Column(name = "SignDate", nullable = false)
    private LocalDate signDate;

    @Column(name = "PaymentStatus", nullable = false)
    private Short paymentStatus = 0; // Dùng Short thay cho TINYINT trong Java

    @Column(name = "Note", length = 255)
    private String note;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ApartmentID", insertable = false, updatable = false)
    private Apartment apartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProfileID", insertable = false, updatable = false)
    private Profile profile;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.signDate == null) {
            this.signDate = LocalDate.now();
        }
    }

    // Getters and Setters
    public Integer getSalesContractID() { return salesContractID; }
    public void setSalesContractID(Integer salesContractID) { this.salesContractID = salesContractID; }

    public Integer getApartmentID() { return apartmentID; }
    public void setApartmentID(Integer apartmentID) { this.apartmentID = apartmentID; }

    public Integer getProfileID() { return profileID; }
    public void setProfileID(Integer profileID) { this.profileID = profileID; }

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }

    public BigDecimal getMaintenanceFee() { return maintenanceFee; }
    public void setMaintenanceFee(BigDecimal maintenanceFee) { this.maintenanceFee = maintenanceFee; }

    public LocalDate getHandoverDate() { return handoverDate; }
    public void setHandoverDate(LocalDate handoverDate) { this.handoverDate = handoverDate; }

    public LocalDate getSignDate() { return signDate; }
    public void setSignDate(LocalDate signDate) { this.signDate = signDate; }

    public Short getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(Short paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Apartment getApartment() { return apartment; }
    public void setApartment(Apartment apartment) { this.apartment = apartment; }

    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }
}
