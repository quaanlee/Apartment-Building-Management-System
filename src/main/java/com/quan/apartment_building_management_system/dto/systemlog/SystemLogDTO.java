package com.quan.apartment_building_management_system.dto.systemlog;

public class SystemLogDTO {
    private Long logId;
    private String createdAt;
    private String action;
    private String role;
    private String accountName;
    private String initials;
    private String entityType;
    private Integer entityId;
    private String oldValue;
    private String newValue;
    private String description;

    public SystemLogDTO() {}

    public SystemLogDTO(Long logId, String createdAt, String action, String role,
                        String accountName, String initials, String entityType,
                        Integer entityId, String oldValue, String newValue, String description) {
        this.logId = logId;
        this.createdAt = createdAt;
        this.action = action;
        this.role = role;
        this.accountName = accountName;
        this.initials = initials;
        this.entityType = entityType;
        this.entityId = entityId;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.description = description;
    }

    // Getters & Setters
    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    private String actionName;
    public String getActionName() { return actionName; }
    public void setActionName(String actionName) { this.actionName = actionName; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    private String roleName;
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    
    public String getInitials() { return initials; }
    public void setInitials(String initials) { this.initials = initials; }
    
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    
    private String entityTypeName;
    public String getEntityTypeName() { return entityTypeName; }
    public void setEntityTypeName(String entityTypeName) { this.entityTypeName = entityTypeName; }
    public Integer getEntityId() { return entityId; }
    public void setEntityId(Integer entityId) { this.entityId = entityId; }
    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
