package com.quan.apartment_building_management_system.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MaintenanceRequestDTO {
    private Integer requestId;
    private String residentName;
    private String residentInitials;
    private String apartmentNumber;
    private String title;
    private String description;
    private String imageUrl;
    private LocalDateTime requestDate;
    private Byte status;

    public MaintenanceRequestDTO() {}

    public MaintenanceRequestDTO(Integer requestId, String residentName, String apartmentNumber,
                                  String title, String description, String imageUrl,
                                  LocalDateTime requestDate, Byte status) {
        this.requestId = requestId;
        this.residentName = residentName;
        this.residentInitials = extractInitials(residentName);
        this.apartmentNumber = apartmentNumber;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.requestDate = requestDate;
        this.status = status;
    }

    private String extractInitials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    public Integer getRequestId() { return requestId; }
    public void setRequestId(Integer requestId) { this.requestId = requestId; }
    public String getResidentName() { return residentName; }
    public void setResidentName(String residentName) { this.residentName = residentName; }
    public String getResidentInitials() { return residentInitials; }
    public void setResidentInitials(String residentInitials) { this.residentInitials = residentInitials; }
    public String getApartmentNumber() { return apartmentNumber; }
    public void setApartmentNumber(String apartmentNumber) { this.apartmentNumber = apartmentNumber; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }
    public Byte getStatus() { return status; }
    public void setStatus(Byte status) { this.status = status; }
}
