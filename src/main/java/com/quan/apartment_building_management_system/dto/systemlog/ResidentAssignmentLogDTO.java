package com.quan.apartment_building_management_system.dto.systemlog;

/**
 * DTO ghi log cho hành động gán cư dân (ASSIGN_RESIDENT / MOVE_OUT_RESIDENT).
 * Đây là đối tượng kết hợp giữa Profile và Apartment.
 */
public class ResidentAssignmentLogDTO {
    private Integer profileId;
    private String residentFullName;
    private String residentEmail;
    private Integer apartmentId;
    private String apartmentNumber;
    private String moveInDate;
    private String moveOutDate;
    private Boolean isHouseholdOwner;
    private Byte residentStatus;

    public ResidentAssignmentLogDTO() {}

    public ResidentAssignmentLogDTO(Integer profileId, String residentFullName, String residentEmail,
                                    Integer apartmentId, String apartmentNumber,
                                    String moveInDate, String moveOutDate,
                                    Boolean isHouseholdOwner, Byte residentStatus) {
        this.profileId = profileId;
        this.residentFullName = residentFullName;
        this.residentEmail = residentEmail;
        this.apartmentId = apartmentId;
        this.apartmentNumber = apartmentNumber;
        this.moveInDate = moveInDate;
        this.moveOutDate = moveOutDate;
        this.isHouseholdOwner = isHouseholdOwner;
        this.residentStatus = residentStatus;
    }

    /**
     * Tạo DTO từ Profile và Apartment tại thời điểm GÁN (assign).
     */
    public static ResidentAssignmentLogDTO fromAssign(
            com.quan.apartment_building_management_system.entity.Profile profile,
            com.quan.apartment_building_management_system.entity.Apartment apartment,
            java.time.LocalDate moveInDate,
            Boolean isHouseholdOwner) {
        if (profile == null) return new ResidentAssignmentLogDTO();
        return new ResidentAssignmentLogDTO(
                profile.getProfileId(),
                profile.getFullName(),
                profile.getEmail(),
                apartment != null ? apartment.getApartmentId() : null,
                apartment != null ? apartment.getApartmentNumber() : null,
                moveInDate != null ? moveInDate.toString() : null,
                null,
                isHouseholdOwner,
                (byte) 1
        );
    }

    /**
     * Tạo DTO từ Profile tại thời điểm TRƯỚC KHI gán (trạng thái ban đầu của cư dân).
     */
    public static ResidentAssignmentLogDTO fromProfileBefore(
            com.quan.apartment_building_management_system.entity.Profile profile) {
        if (profile == null) return new ResidentAssignmentLogDTO();
        com.quan.apartment_building_management_system.entity.Apartment apt = profile.getApartment();
        return new ResidentAssignmentLogDTO(
                profile.getProfileId(),
                profile.getFullName(),
                profile.getEmail(),
                apt != null ? apt.getApartmentId() : null,
                apt != null ? apt.getApartmentNumber() : null,
                profile.getMoveInDate() != null ? profile.getMoveInDate().toString() : null,
                profile.getMoveOutDate() != null ? profile.getMoveOutDate().toString() : null,
                profile.getIsHouseholdOwner(),
                profile.getResidentStatus()
        );
    }

    public Integer getProfileId() { return profileId; }
    public void setProfileId(Integer profileId) { this.profileId = profileId; }
    public String getResidentFullName() { return residentFullName; }
    public void setResidentFullName(String residentFullName) { this.residentFullName = residentFullName; }
    public String getResidentEmail() { return residentEmail; }
    public void setResidentEmail(String residentEmail) { this.residentEmail = residentEmail; }
    public Integer getApartmentId() { return apartmentId; }
    public void setApartmentId(Integer apartmentId) { this.apartmentId = apartmentId; }
    public String getApartmentNumber() { return apartmentNumber; }
    public void setApartmentNumber(String apartmentNumber) { this.apartmentNumber = apartmentNumber; }
    public String getMoveInDate() { return moveInDate; }
    public void setMoveInDate(String moveInDate) { this.moveInDate = moveInDate; }
    public String getMoveOutDate() { return moveOutDate; }
    public void setMoveOutDate(String moveOutDate) { this.moveOutDate = moveOutDate; }
    public Boolean getIsHouseholdOwner() { return isHouseholdOwner; }
    public void setIsHouseholdOwner(Boolean isHouseholdOwner) { this.isHouseholdOwner = isHouseholdOwner; }
    public Byte getResidentStatus() { return residentStatus; }
    public void setResidentStatus(Byte residentStatus) { this.residentStatus = residentStatus; }
}
