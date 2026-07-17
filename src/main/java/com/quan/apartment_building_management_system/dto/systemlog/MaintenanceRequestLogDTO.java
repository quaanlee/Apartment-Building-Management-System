package com.quan.apartment_building_management_system.dto.systemlog;

public class MaintenanceRequestLogDTO {
    private Integer requestId;
    private String title;
    private String apartmentNumber;
    private Byte status;
    private String requestDate;
    private String description;

    public MaintenanceRequestLogDTO() {}

    public MaintenanceRequestLogDTO(Integer requestId, String title, String apartmentNumber, Byte status, String requestDate, String description) {
        this.requestId = requestId;
        this.title = title;
        this.apartmentNumber = apartmentNumber;
        this.status = status;
        this.requestDate = requestDate;
        this.description = description;
    }

    public static MaintenanceRequestLogDTO fromEntity(com.quan.apartment_building_management_system.entity.MaintenanceRequest request) {
        if (request == null) return null;
        return new MaintenanceRequestLogDTO(
            request.getRequestId(),
            request.getTitle(),
            request.getApartment() != null ? request.getApartment().getApartmentNumber() : null,
            request.getStatus(),
            request.getRequestDate() != null ? request.getRequestDate().toString() : null,
            request.getDescription()
        );
    }

    public Integer getRequestId() { return requestId; }
    public void setRequestId(Integer requestId) { this.requestId = requestId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getApartmentNumber() { return apartmentNumber; }
    public void setApartmentNumber(String apartmentNumber) { this.apartmentNumber = apartmentNumber; }
    public Byte getStatus() { return status; }
    public void setStatus(Byte status) { this.status = status; }
    public String getRequestDate() { return requestDate; }
    public void setRequestDate(String requestDate) { this.requestDate = requestDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
