package com.quan.apartment_building_management_system.dto.maintenance;

import com.quan.apartment_building_management_system.entity.Profile;

public class StaffWorkStatusDTO {
    private Profile profile;
    private String workStatus;

    public StaffWorkStatusDTO() {
    }

    public StaffWorkStatusDTO(Profile profile, String workStatus) {
        this.profile = profile;
        this.workStatus = workStatus;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }
}
