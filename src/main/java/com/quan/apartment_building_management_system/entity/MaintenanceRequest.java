package com.quan.apartment_building_management_system.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MaintenanceRequest")
public class MaintenanceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RequestID")
    private Integer requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProfileID", nullable = false)
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ApartmentID", nullable = false)
    private Apartment apartment;

    @Column(name = "Title", nullable = false, length = 100)
    private String title;

    @Column(name = "Description", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "RequestDate", nullable = false)
    private LocalDateTime requestDate = LocalDateTime.now();

    @Column(name = "Status", nullable = false)
    private Byte status = 0;

    @OneToOne(mappedBy = "maintenanceRequest", fetch = FetchType.LAZY)
    private MaintenanceTask maintenanceTask;

    @OneToMany(mappedBy = "request")
    private java.util.List<MaintenanceRequestImage> images = new java.util.ArrayList<>();

    public MaintenanceRequest() {
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public MaintenanceTask getMaintenanceTask() {
        return maintenanceTask;
    }

    public void setMaintenanceTask(MaintenanceTask maintenanceTask) {
        this.maintenanceTask = maintenanceTask;
    }

    public java.util.List<MaintenanceRequestImage> getImages() {
        return images;
    }

    public void setImages(java.util.List<MaintenanceRequestImage> images) {
        this.images = images;
    }
}
