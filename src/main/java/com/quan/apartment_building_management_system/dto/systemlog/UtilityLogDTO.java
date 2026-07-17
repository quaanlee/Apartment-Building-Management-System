package com.quan.apartment_building_management_system.dto.systemlog;

public class UtilityLogDTO {
    private Integer utilityId;
    private String utilityName;
    private String description;
    private String imageUrl;
    private Boolean status;
    private Boolean type;

    public UtilityLogDTO() {}

    public UtilityLogDTO(Integer utilityId, String utilityName, String description, String imageUrl, Boolean status, Boolean type) {
        this.utilityId = utilityId;
        this.utilityName = utilityName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.status = status;
        this.type = type;
    }

    public static UtilityLogDTO fromEntity(com.quan.apartment_building_management_system.entity.Utility utility) {
        if (utility == null) return null;
        return new UtilityLogDTO(
            utility.getUtilityId(),
            utility.getUtilityName(),
            utility.getDescription(),
            utility.getImageUrl(),
            utility.getStatus(),
            utility.getType()
        );
    }

    public Integer getUtilityId() { return utilityId; }
    public void setUtilityId(Integer utilityId) { this.utilityId = utilityId; }
    public String getUtilityName() { return utilityName; }
    public void setUtilityName(String utilityName) { this.utilityName = utilityName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }
    public Boolean getType() { return type; }
    public void setType(Boolean type) { this.type = type; }
}
