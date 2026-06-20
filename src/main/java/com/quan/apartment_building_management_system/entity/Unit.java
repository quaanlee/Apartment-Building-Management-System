package com.quan.apartment_building_management_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Unit")
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UnitID")
    private Integer unitId;

    @Column(name = "UnitName", nullable = false, unique = true, length = 50)
    private String unitName;

    @OneToMany(mappedBy = "unit")
    private List<UtilityPrice> utilityPrices = new ArrayList<>();

    @OneToMany(mappedBy = "unit")
    private List<ServiceItem> serviceItems = new ArrayList<>();

    public Unit() {
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public List<UtilityPrice> getUtilityPrices() {
        return utilityPrices;
    }

    public void setUtilityPrices(List<UtilityPrice> utilityPrices) {
        this.utilityPrices = utilityPrices;
    }

    public List<ServiceItem> getServiceItems() {
        return serviceItems;
    }

    public void setServiceItems(List<ServiceItem> serviceItems) {
        this.serviceItems = serviceItems;
    }
}
