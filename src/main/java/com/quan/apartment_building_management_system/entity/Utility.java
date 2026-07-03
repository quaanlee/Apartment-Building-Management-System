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
@Table(name = "Utility")
public class Utility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UtilityID")
    private Integer utilityId;

    @Column(name = "UtilityName", nullable = false, unique = true, length = 100)
    private String utilityName;

    @Column(name = "Description", length = 255)
    private String description;

    @Column(name = "Status", nullable = false)
    private Boolean status = true;

    @OneToMany(mappedBy = "utility")
    private List<UtilityPrice> utilityPrices = new ArrayList<>();

    @OneToMany(mappedBy = "utility")
    private List<UtilityResource> utilityResources = new ArrayList<>();

    @OneToMany(mappedBy = "utility")
    private List<UtilityImage> utilityImages = new ArrayList<>();

    public Utility() {
    }

    public Integer getUtilityId() {
        return utilityId;
    }

    public void setUtilityId(Integer utilityId) {
        this.utilityId = utilityId;
    }

    public String getUtilityName() {
        return utilityName;
    }

    public void setUtilityName(String utilityName) {
        this.utilityName = utilityName;
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

    public List<UtilityPrice> getUtilityPrices() {
        return utilityPrices;
    }

    public void setUtilityPrices(List<UtilityPrice> utilityPrices) {
        this.utilityPrices = utilityPrices;
    }

    public List<UtilityResource> getUtilityResources() {
        return utilityResources;
    }

    public void setUtilityResources(List<UtilityResource> utilityResources) {
        this.utilityResources = utilityResources;
    }

    public List<UtilityImage> getUtilityImages() {
        return utilityImages;
    }

    public void setUtilityImages(List<UtilityImage> utilityImages) {
        this.utilityImages = utilityImages;
    }
}
