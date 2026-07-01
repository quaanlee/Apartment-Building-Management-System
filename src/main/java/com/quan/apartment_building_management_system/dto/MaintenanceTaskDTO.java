package com.quan.apartment_building_management_system.dto;

import com.quan.apartment_building_management_system.entity.MaintenanceReport;
import java.time.LocalDateTime;
import java.util.List;

public class MaintenanceTaskDTO {
    private Integer taskId;
    private Integer requestId;
    private String requestTitle;
    private String requestDescription;
    private String requestImageUrl;
    private LocalDateTime requestDate;
    private String apartmentNumber;
    private String residentName;
    private String staffName;
    private String assignedByName;
    private LocalDateTime assignedDate;
    private LocalDateTime deadline;
    private Byte status;
    private Byte progressPercent;
    private List<MaintenanceReport> reports;

    public MaintenanceTaskDTO() {}

    public MaintenanceTaskDTO(Integer taskId, Integer requestId, String requestTitle,
                              String requestDescription, String requestImageUrl,
                              LocalDateTime requestDate, String apartmentNumber,
                              String residentName, String staffName, String assignedByName,
                              LocalDateTime assignedDate, LocalDateTime deadline,
                              Byte status, Byte progressPercent) {
        this.taskId = taskId;
        this.requestId = requestId;
        this.requestTitle = requestTitle;
        this.requestDescription = requestDescription;
        this.requestImageUrl = requestImageUrl;
        this.requestDate = requestDate;
        this.apartmentNumber = apartmentNumber;
        this.residentName = residentName;
        this.staffName = staffName;
        this.assignedByName = assignedByName;
        this.assignedDate = assignedDate;
        this.deadline = deadline;
        this.status = status;
        this.progressPercent = progressPercent;
    }

    public Integer getTaskId() { return taskId; }
    public void setTaskId(Integer taskId) { this.taskId = taskId; }
    public Integer getRequestId() { return requestId; }
    public void setRequestId(Integer requestId) { this.requestId = requestId; }
    public String getRequestTitle() { return requestTitle; }
    public void setRequestTitle(String requestTitle) { this.requestTitle = requestTitle; }
    public String getRequestDescription() { return requestDescription; }
    public void setRequestDescription(String requestDescription) { this.requestDescription = requestDescription; }
    public String getRequestImageUrl() { return requestImageUrl; }
    public void setRequestImageUrl(String requestImageUrl) { this.requestImageUrl = requestImageUrl; }
    public LocalDateTime getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDateTime requestDate) { this.requestDate = requestDate; }
    public String getApartmentNumber() { return apartmentNumber; }
    public void setApartmentNumber(String apartmentNumber) { this.apartmentNumber = apartmentNumber; }
    public String getResidentName() { return residentName; }
    public void setResidentName(String residentName) { this.residentName = residentName; }
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    public String getAssignedByName() { return assignedByName; }
    public void setAssignedByName(String assignedByName) { this.assignedByName = assignedByName; }
    public LocalDateTime getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDateTime assignedDate) { this.assignedDate = assignedDate; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public Byte getStatus() { return status; }
    public void setStatus(Byte status) { this.status = status; }
    public Byte getProgressPercent() { return progressPercent; }
    public void setProgressPercent(Byte progressPercent) { this.progressPercent = progressPercent; }
    public List<MaintenanceReport> getReports() { return reports; }
    public void setReports(List<MaintenanceReport> reports) { this.reports = reports; }
}
