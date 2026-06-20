package com.quan.apartment_building_management_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Apartment")
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ApartmentID")
    private Integer apartmentId;

    @Column(name = "ApartmentNumber", nullable = false, unique = true, length = 20)
    private String apartmentNumber;

    @Column(name = "Floor", nullable = false)
    private Byte floor;

    @Column(name = "Area", nullable = false, precision = 8, scale = 2)
    private BigDecimal area;

    @Column(name = "RoomType", nullable = false, length = 50)
    private String roomType;

    @Column(name = "Status", nullable = false)
    private Byte status = 0;

    @Column(name = "MaxOccupancy", nullable = false)
    private Byte maxOccupancy = 4;

    @OneToMany(mappedBy = "apartment")
    private List<Profile> profiles = new ArrayList<>();

    @OneToMany(mappedBy = "apartment")
    private List<ResidentApartment> residentApartments = new ArrayList<>();

    @OneToMany(mappedBy = "apartment")
    private List<Bill> bills = new ArrayList<>();

    @OneToMany(mappedBy = "apartment")
    private List<MaintenanceRequest> maintenanceRequests = new ArrayList<>();

    public Apartment() {
    }

    public Integer getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(Integer apartmentId) {
        this.apartmentId = apartmentId;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public Byte getFloor() {
        return floor;
    }

    public void setFloor(Byte floor) {
        this.floor = floor;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Byte getMaxOccupancy() {
        return maxOccupancy;
    }

    public void setMaxOccupancy(Byte maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }

    public List<ResidentApartment> getResidentApartments() {
        return residentApartments;
    }

    public void setResidentApartments(List<ResidentApartment> residentApartments) {
        this.residentApartments = residentApartments;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }

    public List<MaintenanceRequest> getMaintenanceRequests() {
        return maintenanceRequests;
    }

    public void setMaintenanceRequests(List<MaintenanceRequest> maintenanceRequests) {
        this.maintenanceRequests = maintenanceRequests;
    }
}
