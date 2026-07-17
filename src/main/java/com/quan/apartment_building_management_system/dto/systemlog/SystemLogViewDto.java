package com.quan.apartment_building_management_system.dto.systemlog;

import java.time.LocalDateTime;

public class SystemLogViewDto {
    private LocalDateTime createdAt;
    private String role;
    private String accountName;
    private String action;
    private String entityType;

    public SystemLogViewDto() {
    }

    public SystemLogViewDto(LocalDateTime createdAt, String role, String accountName, String action, String entityType) {
        this.createdAt = createdAt;
        this.role = role;
        this.accountName = accountName;
        this.action = action;
        this.entityType = entityType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
}
