package com.quan.apartment_building_management_system.dto.apartment;

import java.time.LocalDate;

public class AssignResidentRequest {
    private Integer profileId;
    private LocalDate moveInDate;
    private Boolean isHouseholdOwner;

    public AssignResidentRequest() {
        this.moveInDate = LocalDate.now();
        this.isHouseholdOwner = false;
    }

    public Integer getProfileId() { return profileId; }
    public void setProfileId(Integer profileId) { this.profileId = profileId; }

    public LocalDate getMoveInDate() { return moveInDate; }
    public void setMoveInDate(LocalDate moveInDate) { this.moveInDate = moveInDate; }

    public Boolean getIsHouseholdOwner() { return isHouseholdOwner; }
    public void setIsHouseholdOwner(Boolean isHouseholdOwner) { this.isHouseholdOwner = isHouseholdOwner; }
}
