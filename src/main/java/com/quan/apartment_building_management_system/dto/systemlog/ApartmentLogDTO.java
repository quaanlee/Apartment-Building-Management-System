package com.quan.apartment_building_management_system.dto.systemlog;

public class ApartmentLogDTO {
    private Integer apartmentId;
    private String apartmentNumber;
    private Byte floor;
    private java.math.BigDecimal area;
    private String roomType;
    private Byte status;
    private Byte maxOccupancy;

    public ApartmentLogDTO() {}

    public ApartmentLogDTO(Integer apartmentId, String apartmentNumber, Byte floor, java.math.BigDecimal area, String roomType, Byte status, Byte maxOccupancy) {
        this.apartmentId = apartmentId;
        this.apartmentNumber = apartmentNumber;
        this.floor = floor;
        this.area = area;
        this.roomType = roomType;
        this.status = status;
        this.maxOccupancy = maxOccupancy;
    }

    public static ApartmentLogDTO fromEntity(com.quan.apartment_building_management_system.entity.Apartment apartment) {
        if (apartment == null) return null;
        return new ApartmentLogDTO(
            apartment.getApartmentId(),
            apartment.getApartmentNumber(),
            apartment.getFloor(),
            apartment.getArea(),
            apartment.getRoomType(),
            apartment.getStatus(),
            apartment.getMaxOccupancy()
        );
    }

    public Integer getApartmentId() { return apartmentId; }
    public void setApartmentId(Integer apartmentId) { this.apartmentId = apartmentId; }
    public String getApartmentNumber() { return apartmentNumber; }
    public void setApartmentNumber(String apartmentNumber) { this.apartmentNumber = apartmentNumber; }
    public Byte getFloor() { return floor; }
    public void setFloor(Byte floor) { this.floor = floor; }
    public java.math.BigDecimal getArea() { return area; }
    public void setArea(java.math.BigDecimal area) { this.area = area; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public Byte getStatus() { return status; }
    public void setStatus(Byte status) { this.status = status; }
    public Byte getMaxOccupancy() { return maxOccupancy; }
    public void setMaxOccupancy(Byte maxOccupancy) { this.maxOccupancy = maxOccupancy; }
}
