package com.quan.apartment_building_management_system.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "MaintenanceRequestImage")
public class MaintenanceRequestImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ImageID")
    private Integer imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RequestID", nullable = false)
    private MaintenanceRequest request;

    @Column(name = "ImageURL", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "Description", length = 255)
    private String description;

    public MaintenanceRequestImage() {}

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public MaintenanceRequest getRequest() {
        return request;
    }

    public void setRequest(MaintenanceRequest request) {
        this.request = request;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
