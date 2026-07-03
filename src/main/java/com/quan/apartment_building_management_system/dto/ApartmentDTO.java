package com.quan.apartment_building_management_system.dto;

import java.math.BigDecimal;

public class ApartmentDTO {
    private Integer apartmentId;
    private String apartmentNumber;
    private Byte floor;
    private BigDecimal area;
    private String roomType;
    private Byte status;
    private Byte maxOccupancy;
    private Integer currentOccupancy;

    public ApartmentDTO() {}

    public ApartmentDTO(Integer apartmentId, String apartmentNumber, Byte floor,
                        BigDecimal area, String roomType, Byte status,
                        Byte maxOccupancy, Integer currentOccupancy) {
        this.apartmentId = apartmentId;
        this.apartmentNumber = apartmentNumber;
        this.floor = floor;
        this.area = area;
        this.roomType = roomType;
        this.status = status;
        this.maxOccupancy = maxOccupancy;
        this.currentOccupancy = currentOccupancy;
    }

    // Getters and Setters
    public Integer getApartmentId() { return apartmentId; }
    public void setApartmentId(Integer apartmentId) { this.apartmentId = apartmentId; }

    public String getApartmentNumber() { return apartmentNumber; }
    public void setApartmentNumber(String apartmentNumber) { this.apartmentNumber = apartmentNumber; }

    public Byte getFloor() { return floor; }
    public void setFloor(Byte floor) { this.floor = floor; }

    public BigDecimal getArea() { return area; }
    public void setArea(BigDecimal area) { this.area = area; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public Byte getStatus() { return status; }
    public void setStatus(Byte status) { this.status = status; }

    public Byte getMaxOccupancy() { return maxOccupancy; }
    public void setMaxOccupancy(Byte maxOccupancy) { this.maxOccupancy = maxOccupancy; }

    public Integer getCurrentOccupancy() { return currentOccupancy; }
    public void setCurrentOccupancy(Integer currentOccupancy) { this.currentOccupancy = currentOccupancy; }

    // Helper methods
    public String getStatusDisplay() {
        if (status == null) return "Unknown";
        switch (status) {
            case 0: return "Available";
            case 1: return "Occupied";
            case 2: return "Maintenance";
            default: return "Unknown";
        }
    }

    public String getStatusBadgeClass() {
        if (status == null) return "secondary";
        switch (status) {
            case 0: return "success";
            case 1: return "primary";
            case 2: return "warning";
            default: return "secondary";
        }
    }

    public String getOccupancyDisplay() {
        return (currentOccupancy != null ? currentOccupancy : 0) + "/" + maxOccupancy;
    }
}