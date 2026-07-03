package com.quan.apartment_building_management_system.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ApartmentPriceHistory")
public class ApartmentPriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PriceHistoryID")
    private Integer priceHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ApartmentID", nullable = false)
    private Apartment apartment;

    @Column(name = "Price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "EffectiveDate", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "EndDate")
    private LocalDate endDate;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "Note", length = 255)
    private String note;

    public ApartmentPriceHistory() {}

    public Integer getPriceHistoryId() {
        return priceHistoryId;
    }

    public void setPriceHistoryId(Integer priceHistoryId) {
        this.priceHistoryId = priceHistoryId;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
