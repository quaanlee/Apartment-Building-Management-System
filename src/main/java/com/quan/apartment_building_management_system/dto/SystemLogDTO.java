package com.quan.apartment_building_management_system.dto;

public class SystemLogDTO {
    private Long logId;
    private String createdAt;
    private String action;
    private String role;
    private String accountName;
    private String initials;
    private String entityType;
    private Integer entityId;
    private String ipAddress;
    private String details;

    public SystemLogDTO() {}

    public SystemLogDTO(Long logId, String createdAt, String action, String role,
                        String accountName, String initials, String entityType,
                        Integer entityId, String ipAddress, String details) {
        this.logId = logId;
        this.createdAt = createdAt;
        this.action = action;
        this.role = role;
        this.accountName = accountName;
        this.initials = initials;
        this.entityType = entityType;
        this.entityId = entityId;
        this.ipAddress = ipAddress;
        this.details = details;
    }

    // Getters & Setters
    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getInitials() { return initials; }
    public void setInitials(String initials) { this.initials = initials; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public Integer getEntityId() { return entityId; }
    public void setEntityId(Integer entityId) { this.entityId = entityId; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
