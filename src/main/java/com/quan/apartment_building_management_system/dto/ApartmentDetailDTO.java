package com.quan.apartment_building_management_system.dto;

import java.time.LocalDate;
import java.util.List;

public class ApartmentDetailDTO extends ApartmentDTO {
    private List<ResidentInfo> currentResidents;
    private Integer availableSlots;
    private List<String> imageUrls;

    public ApartmentDetailDTO() {}

    public List<ResidentInfo> getCurrentResidents() { return currentResidents; }
    public void setCurrentResidents(List<ResidentInfo> currentResidents) {
        this.currentResidents = currentResidents;
    }

    public Integer getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(Integer availableSlots) { this.availableSlots = availableSlots; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    // Inner class
    public static class ResidentInfo {
        private Integer profileId;
        private String fullName;
        private String phoneNumber;
        private String email;
        private Boolean isHouseholdOwner;
        private LocalDate moveInDate;
        private String citizenId;
        private String relationshipToOwner;

        public Integer getProfileId() { return profileId; }
        public void setProfileId(Integer profileId) { this.profileId = profileId; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public Boolean getIsHouseholdOwner() { return isHouseholdOwner; }
        public void setIsHouseholdOwner(Boolean isHouseholdOwner) { this.isHouseholdOwner = isHouseholdOwner; }

        public LocalDate getMoveInDate() { return moveInDate; }
        public void setMoveInDate(LocalDate moveInDate) { this.moveInDate = moveInDate; }

        public String getCitizenId() { return citizenId; }
        public void setCitizenId(String citizenId) { this.citizenId = citizenId; }

        public String getRelationshipToOwner() { return relationshipToOwner; }
        public void setRelationshipToOwner(String relationshipToOwner) { this.relationshipToOwner = relationshipToOwner; }

        public String getOwnerBadge() {
            return isHouseholdOwner != null && isHouseholdOwner ? "👑 Owner" : "Member";
        }
    }
}