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

    @Column(name = "ImageURL", length = 500)
    private String imageUrl;

    @Column(name = "Status", nullable = false)
    private Boolean status = true;

    @Column(name = "Type", nullable = false)
    private Boolean type = true;

    @OneToMany(mappedBy = "utility")
    private List<UtilityResource> utilityResources = new ArrayList<>();

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public List<UtilityResource> getUtilityResources() {
        return utilityResources;
    }

    public void setUtilityResources(List<UtilityResource> utilityResources) {
        this.utilityResources = utilityResources;
    }
}
