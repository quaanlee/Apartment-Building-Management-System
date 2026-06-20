package com.quan.apartment_building_management_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VehicleID")
    private Integer vehicleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProfileID", nullable = false)
    private Profile profile;

    @Column(name = "LicensePlate", nullable = false, unique = true, length = 20)
    private String licensePlate;

    @Column(name = "VehicleType", nullable = false, length = 50)
    private String vehicleType;

    @Column(name = "Brand", length = 50)
    private String brand;

    @Column(name = "Color", length = 30)
    private String color;

    @Column(name = "RegisteredDate", nullable = false)
    private LocalDate registeredDate = LocalDate.now();

    @Column(name = "Status", nullable = false)
    private Byte status = 0;

    @Column(name = "DocumentURL", length = 500)
    private String documentUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ApprovedBy")
    private Account approvedBy;

    @Column(name = "ApprovedAt")
    private LocalDateTime approvedAt;

    public Vehicle() {
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public LocalDate getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(LocalDate registeredDate) {
        this.registeredDate = registeredDate;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public Account getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Account approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }
}
