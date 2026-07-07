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

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "UtilityResource")
public class UtilityResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ResourceID")
    private Integer resourceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UtilityID", nullable = false)
    private Utility utility;

    @Column(name = "ResourceName", nullable = false, length = 100)
    private String resourceName;

    @Column(name = "Location", length = 150)
    private String location;

    @Column(name = "Description", length = 255)
    private String description;

    @Column(name = "Status", nullable = false)
    private Boolean status = true;

    @OneToMany(mappedBy = "resource")
    private List<UtilityBooking> utilityBookings = new ArrayList<>();

    @OneToMany(mappedBy = "resource")
    private List<UtilityPrice> utilityPrices = new ArrayList<>();

    @OneToMany(mappedBy = "resource")
    private List<UtilityImage> utilityImages = new ArrayList<>();

    public UtilityResource() {
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Utility getUtility() {
        return utility;
    }

    public void setUtility(Utility utility) {
        this.utility = utility;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<UtilityBooking> getUtilityBookings() {
        return utilityBookings;
    }

    public void setUtilityBookings(List<UtilityBooking> utilityBookings) {
        this.utilityBookings = utilityBookings;
    }

    public List<UtilityPrice> getUtilityPrices() {
        return utilityPrices;
    }

    public void setUtilityPrices(List<UtilityPrice> utilityPrices) {
        this.utilityPrices = utilityPrices;
    }

    public List<UtilityImage> getUtilityImages() {
        return utilityImages;
    }

    public void setUtilityImages(List<UtilityImage> utilityImages) {
        this.utilityImages = utilityImages;
    }
}
