package com.quan.apartment_building_management_system.dto.systemlog;

public class UtilityResourceLogDTO {
    private Integer resourceId;
    private String utilityName;
    private String resourceName;
    private String location;
    private String description;
    private Boolean status;

    public UtilityResourceLogDTO() {}

    public UtilityResourceLogDTO(Integer resourceId, String utilityName, String resourceName, String location, String description, Boolean status) {
        this.resourceId = resourceId;
        this.utilityName = utilityName;
        this.resourceName = resourceName;
        this.location = location;
        this.description = description;
        this.status = status;
    }

    public static UtilityResourceLogDTO fromEntity(com.quan.apartment_building_management_system.entity.UtilityResource resource) {
        if (resource == null) return null;
        return new UtilityResourceLogDTO(
            resource.getResourceId(),
            resource.getUtility() != null ? resource.getUtility().getUtilityName() : null,
            resource.getResourceName(),
            resource.getLocation(),
            resource.getDescription(),
            resource.getStatus()
        );
    }

    public Integer getResourceId() { return resourceId; }
    public void setResourceId(Integer resourceId) { this.resourceId = resourceId; }
    public String getUtilityName() { return utilityName; }
    public void setUtilityName(String utilityName) { this.utilityName = utilityName; }
    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
}
