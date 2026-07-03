package com.quan.apartment_building_management_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

    @NotBlank(message = "Apartment number is required")
    @Size(max = 20, message = "Apartment number must be at most 20 characters")
    @Column(name = "ApartmentNumber", nullable = false, unique = true, length = 20)
    private String apartmentNumber;

    @NotNull(message = "Floor is required")
    @Min(value = 1, message = "Floor must be at least 1")
    @Max(value = 100, message = "Floor must be at most 100")
    @Column(name = "Floor", nullable = false)
    private Byte floor;

    @NotNull(message = "Area is required")
    @DecimalMin(value = "10.0", message = "Area must be at least 10.0 m²")
    @DecimalMax(value = "1000.0", message = "Area must be at most 1000.0 m²")
    @Digits(integer = 6, fraction = 2, message = "Area must be a valid decimal number")
    @Column(name = "Area", nullable = false, precision = 8, scale = 2)
    private BigDecimal area;

    @NotBlank(message = "Room type is required")
    @Column(name = "RoomType", nullable = false, length = 50)
    private String roomType;

    @NotNull(message = "Status is required")
    @Column(name = "Status", nullable = false)
    private Byte status = 0;

    @NotNull(message = "Max occupancy is required")
    @Min(value = 1, message = "Max occupancy must be at least 1")
    @Max(value = 20, message = "Max occupancy must be at most 20")
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
