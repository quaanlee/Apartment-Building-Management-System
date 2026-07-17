package com.quan.apartment_building_management_system.dto.systemlog;

public class VehicleRegistrationLogDTO {
    private Integer vehicleId;
    private String licensePlate;
    private String vehicleType;
    private String brand;
    private String color;
    private Byte status;
    private String registeredDate;

    public VehicleRegistrationLogDTO() {}

    public VehicleRegistrationLogDTO(Integer vehicleId, String licensePlate, String vehicleType, String brand, String color, Byte status, String registeredDate) {
        this.vehicleId = vehicleId;
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.brand = brand;
        this.color = color;
        this.status = status;
        this.registeredDate = registeredDate;
    }

    public static VehicleRegistrationLogDTO fromEntity(com.quan.apartment_building_management_system.entity.Vehicle vehicle) {
        if (vehicle == null) return null;
        return new VehicleRegistrationLogDTO(
            vehicle.getVehicleId(),
            vehicle.getLicensePlate(),
            vehicle.getVehicleType(),
            vehicle.getBrand(),
            vehicle.getColor(),
            vehicle.getStatus(),
            vehicle.getRegisteredDate() != null ? vehicle.getRegisteredDate().toString() : null
        );
    }

    public Integer getVehicleId() { return vehicleId; }
    public void setVehicleId(Integer vehicleId) { this.vehicleId = vehicleId; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Byte getStatus() { return status; }
    public void setStatus(Byte status) { this.status = status; }
    public String getRegisteredDate() { return registeredDate; }
    public void setRegisteredDate(String registeredDate) { this.registeredDate = registeredDate; }
}
