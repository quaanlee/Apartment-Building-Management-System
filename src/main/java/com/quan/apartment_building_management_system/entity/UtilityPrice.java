package com.quan.apartment_building_management_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "UtilityPrice")
public class UtilityPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UtilityPriceID")
    private Integer utilityPriceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ResourceID", nullable = false)
    private UtilityResource resource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UnitID", nullable = false)
    private Unit unit;

    @Column(name = "Price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @OneToMany(mappedBy = "utilityPrice")
    private List<UtilityBooking> utilityBookings = new ArrayList<>();

    public UtilityPrice() {
    }

    public Integer getUtilityPriceId() {
        return utilityPriceId;
    }

    public void setUtilityPriceId(Integer utilityPriceId) {
        this.utilityPriceId = utilityPriceId;
    }

    public UtilityResource getResource() {
        return resource;
    }

    public void setResource(UtilityResource resource) {
        this.resource = resource;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<UtilityBooking> getUtilityBookings() {
        return utilityBookings;
    }

    public void setUtilityBookings(List<UtilityBooking> utilityBookings) {
        this.utilityBookings = utilityBookings;
    }
}
