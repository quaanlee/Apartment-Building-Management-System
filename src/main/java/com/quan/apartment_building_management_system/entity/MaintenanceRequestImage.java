package com.quan.apartment_building_management_system.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "MaintenanceRequestImage")
public class MaintenanceRequestImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ImageID")
    private Integer imageID;

    @Column(name = "RequestID", nullable = false)
    private Integer requestID;

    @Column(name = "ImageURL", nullable = false, length = 500)
    private String imageURL;

    @Column(name = "Description", length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RequestID", insertable = false, updatable = false)
    private MaintenanceRequest maintenanceRequest;

    // Getters and Setters
    public Integer getImageID() { return imageID; }
    public void setImageID(Integer imageID) { this.imageID = imageID; }

    public Integer getRequestID() { return requestID; }
    public void setRequestID(Integer requestID) { this.requestID = requestID; }

    public String getImageURL() { return imageURL; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public MaintenanceRequest getMaintenanceRequest() { return maintenanceRequest; }
    public void setMaintenanceRequest(MaintenanceRequest maintenanceRequest) { this.maintenanceRequest = maintenanceRequest; }
}