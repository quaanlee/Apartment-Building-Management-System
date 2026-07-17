package com.quan.apartment_building_management_system.service.apartment;

import com.quan.apartment_building_management_system.dto.apartment.ApartmentDTO;
import com.quan.apartment_building_management_system.dto.apartment.ApartmentDetailDTO;
import com.quan.apartment_building_management_system.entity.Apartment;
import com.quan.apartment_building_management_system.entity.ApartmentImage;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.entity.ResidentApartment;
import com.quan.apartment_building_management_system.exception.ResourceNotFoundException;
import com.quan.apartment_building_management_system.repository.ApartmentRepository;
import com.quan.apartment_building_management_system.repository.ProfileRepository;
import com.quan.apartment_building_management_system.repository.ResidentApartmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApartmentManagerService {

    private final ApartmentRepository apartmentRepository;
    private final ResidentApartmentRepository residentApartmentRepository;
    private final ProfileRepository profileRepository;
    private final com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService;

    public ApartmentManagerService(ApartmentRepository apartmentRepository,
            ResidentApartmentRepository residentApartmentRepository,
            ProfileRepository profileRepository,
            com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService) {
        this.apartmentRepository = apartmentRepository;
        this.residentApartmentRepository = residentApartmentRepository;
        this.profileRepository = profileRepository;
        this.systemLogService = systemLogService;
    }

    // 1. View Apartment List (Legacy signature)
    public Page<ApartmentDTO> getFilteredApartments(
            String search, Byte floor, Byte status,
            BigDecimal minArea, BigDecimal maxArea,
            Pageable pageable) {
        return getFilteredApartments(search, null, floor, status, minArea, maxArea, pageable);
    }

    // 1. View Apartment List (New signature with roomType)
    public Page<ApartmentDTO> getFilteredApartments(
            String search, String roomType, Byte floor, Byte status,
            BigDecimal minArea, BigDecimal maxArea,
            Pageable pageable) {

        Page<Apartment> apartments = apartmentRepository.findFiltered(
                search, roomType, floor, status, minArea, maxArea, pageable);

        return apartments.map(this::convertToDTO);
    }

    // 2. View Apartment Detail
    public ApartmentDetailDTO getApartmentDetail(Integer apartmentId) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found with id: " + apartmentId));
        return convertToDetailDTO(apartment);
    }

    // 3. Update Apartment Status
    public ApartmentDTO updateApartmentStatus(Integer apartmentId, Byte status) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found with id: " + apartmentId));

        if (status == null || status < 0 || status > 2) {
            throw new IllegalArgumentException("Invalid status. Must be 0, 1, or 2");
        }

        com.quan.apartment_building_management_system.dto.systemlog.ApartmentLogDTO oldDto = com.quan.apartment_building_management_system.dto.systemlog.ApartmentLogDTO
                .fromEntity(apartment);

        String oldStatus = String.valueOf(apartment.getStatus());
        apartment.setStatus(status);
        Apartment updated = apartmentRepository.save(apartment);

        com.quan.apartment_building_management_system.dto.systemlog.ApartmentLogDTO newDto = com.quan.apartment_building_management_system.dto.systemlog.ApartmentLogDTO
                .fromEntity(updated);
        systemLogService.logSystemAction(
                "UPDATE_APARTMENT",
                "Apartment",
                apartmentId,
                oldDto,
                newDto,
                "Updated apartment status from " + oldStatus + " to " + status);

        return convertToDTO(updated);
    }

    // 4. Assign Resident
    @Transactional
    public void assignResidentToApartment(Integer profileId, Integer apartmentId,
            LocalDate moveInDate, Boolean isHouseholdOwner) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found"));

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        List<ResidentApartment> activeRecords = residentApartmentRepository.findByProfileProfileId(profileId)
                .stream()
                .filter(ra -> ra.getMoveOutDate() == null)
                .collect(Collectors.toList());

        // Auto-transfer: If they already have an apartment, move them out first
        if (profile.getApartment() != null || !activeRecords.isEmpty()) {
            Integer oldApartmentId = null;
            if (profile.getApartment() != null) {
                oldApartmentId = profile.getApartment().getApartmentId();
            } else if (!activeRecords.isEmpty()) {
                oldApartmentId = activeRecords.get(0).getApartment().getApartmentId();
            }

            for (ResidentApartment ra : activeRecords) {
                ra.setMoveOutDate(moveInDate != null ? moveInDate : LocalDate.now());
                residentApartmentRepository.save(ra);
            }

            if (oldApartmentId != null) {
                Long oldOccupancy = residentApartmentRepository.countCurrentResidentsByApartment(oldApartmentId);
                Apartment oldApartment = apartmentRepository.findById(oldApartmentId).orElse(null);
                if (oldApartment != null) {
                    if (oldOccupancy == 0) {
                        oldApartment.setStatus((byte) 0);
                    } else {
                        oldApartment.setStatus((byte) 1);
                    }
                    apartmentRepository.save(oldApartment);
                }
            }
        }

        Long currentCount = residentApartmentRepository.countCurrentResidentsByApartment(apartmentId);
        if (currentCount >= apartment.getMaxOccupancy()) {
            throw new IllegalStateException("Apartment is full! Max: " + apartment.getMaxOccupancy());
        }

        ResidentApartment residentApartment = new ResidentApartment();
        com.quan.apartment_building_management_system.dto.systemlog.ResidentAssignmentLogDTO oldDto = com.quan.apartment_building_management_system.dto.systemlog.ResidentAssignmentLogDTO
                .fromProfileBefore(profile);

        residentApartment.setApartment(apartment);
        residentApartment.setProfile(profile);
        residentApartment.setMoveInDate(moveInDate != null ? moveInDate : LocalDate.now());
        residentApartmentRepository.save(residentApartment);

        profile.setApartment(apartment);
        profile.setMoveInDate(moveInDate != null ? moveInDate : LocalDate.now());
        profile.setIsHouseholdOwner(isHouseholdOwner != null && isHouseholdOwner);
        profile.setResidentStatus((byte) 1);
        profileRepository.save(profile);

        if (apartment.getStatus() == 0) {
            apartment.setStatus((byte) 1);
            apartmentRepository.save(apartment);
        }

        com.quan.apartment_building_management_system.dto.systemlog.ResidentAssignmentLogDTO newDto = com.quan.apartment_building_management_system.dto.systemlog.ResidentAssignmentLogDTO
                .fromAssign(
                        profile, apartment, moveInDate != null ? moveInDate : LocalDate.now(), isHouseholdOwner);

        systemLogService.logSystemAction(
                "ASSIGN_RESIDENT",
                "Apartment",
                apartmentId,
                oldDto,
                newDto,
                "Assigned resident " + profile.getFullName() + " to apartment " + apartment.getApartmentNumber());
    }

    // 5. Move out resident
    @Transactional
    public void moveOutResident(Integer profileId, LocalDate moveOutDate) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        LocalDate moveOut = moveOutDate != null ? moveOutDate : LocalDate.now();

        List<ResidentApartment> activeRecords = residentApartmentRepository.findByProfileProfileId(profileId)
                .stream()
                .filter(ra -> ra.getMoveOutDate() == null)
                .collect(Collectors.toList());

        if (profile.getApartment() == null && activeRecords.isEmpty()) {
            throw new IllegalStateException("This resident is not in any apartment!");
        }

        Integer apartmentId = null;
        if (profile.getApartment() != null) {
            apartmentId = profile.getApartment().getApartmentId();
        } else if (!activeRecords.isEmpty()) {
            apartmentId = activeRecords.get(0).getApartment().getApartmentId();
        }

        com.quan.apartment_building_management_system.dto.systemlog.ResidentAssignmentLogDTO oldDto = com.quan.apartment_building_management_system.dto.systemlog.ResidentAssignmentLogDTO
                .fromProfileBefore(profile);

        for (ResidentApartment ra : activeRecords) {
            ra.setMoveOutDate(moveOut);
            residentApartmentRepository.save(ra);
        }

        profile.setApartment(null);
        profile.setMoveOutDate(moveOut);
        profile.setResidentStatus((byte) 2);
        profileRepository.save(profile);

        if (apartmentId != null) {
            Long currentCount = residentApartmentRepository.countCurrentResidentsByApartment(apartmentId);
            Apartment apartment = apartmentRepository.findById(apartmentId).orElse(null);
            if (apartment != null) {
                if (currentCount == 0) {
                    apartment.setStatus((byte) 0);
                } else {
                    apartment.setStatus((byte) 1);
                }
                apartmentRepository.save(apartment);
            }
        }

        com.quan.apartment_building_management_system.dto.systemlog.ResidentAssignmentLogDTO newDto = com.quan.apartment_building_management_system.dto.systemlog.ResidentAssignmentLogDTO
                .fromProfileBefore(profile);

        systemLogService.logSystemAction(
                "MOVE_OUT_RESIDENT",
                "Apartment",
                apartmentId,
                oldDto,
                newDto,
                "Resident " + profile.getFullName() + " moved out of apartment "
                        + (apartmentId != null ? apartmentId : "unknown"));
    }

    // 6. Get available residents
    public List<Profile> getAvailableResidents(String search) {
        Pageable pageable = PageRequest.of(0, 50);
        return profileRepository.findAvailableResidents(
                search != null && !search.isEmpty() ? search : null,
                pageable);
    }

    public Page<Profile> getAvailableResidentsPaged(String search, Integer apartmentId, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return profileRepository.findAvailableResidentsPaged(apartmentId, pageable);
        } else {
            String formattedSearch = "%" + search.trim() + "%";
            return profileRepository.findAvailableResidentsPagedWithSearch(formattedSearch, apartmentId, pageable);
        }
    }

    // ===== STATISTICS =====

    public long getTotalApartments() {
        return apartmentRepository.count();
    }

    public long countByStatus(Byte status) {
        return apartmentRepository.countByStatus(status);
    }

    // ===== HELPER METHODS =====

    private ApartmentDTO convertToDTO(Apartment apartment) {
        Long currentResidents = residentApartmentRepository
                .countCurrentResidentsByApartment(apartment.getApartmentId());

        ApartmentDTO dto = new ApartmentDTO();
        dto.setApartmentId(apartment.getApartmentId());
        dto.setApartmentNumber(apartment.getApartmentNumber());
        dto.setFloor(apartment.getFloor());
        dto.setArea(apartment.getArea());
        dto.setRoomType(apartment.getRoomType());
        dto.setStatus(apartment.getStatus());
        dto.setMaxOccupancy(apartment.getMaxOccupancy());
        dto.setCurrentOccupancy(currentResidents.intValue());
        return dto;
    }

    private ApartmentDetailDTO convertToDetailDTO(Apartment apartment) {
        ApartmentDetailDTO dto = new ApartmentDetailDTO();
        dto.setApartmentId(apartment.getApartmentId());
        dto.setApartmentNumber(apartment.getApartmentNumber());
        dto.setFloor(apartment.getFloor());
        dto.setArea(apartment.getArea());
        dto.setRoomType(apartment.getRoomType());
        dto.setStatus(apartment.getStatus());
        dto.setMaxOccupancy(apartment.getMaxOccupancy());

        List<String> imageUrls = apartment.getApartmentImages().stream()
                .sorted((img1, img2) -> {
                    int primaryCompare = Boolean.compare(img2.getPrimary(), img1.getPrimary());
                    if (primaryCompare != 0) {
                        return primaryCompare;
                    }
                    if (img1.getUploadedAt() != null && img2.getUploadedAt() != null) {
                        return img1.getUploadedAt().compareTo(img2.getUploadedAt());
                    }
                    if (img1.getImageId() != null && img2.getImageId() != null) {
                        return img1.getImageId().compareTo(img2.getImageId());
                    }
                    return 0;
                })
                .map(ApartmentImage::getImageUrl)
                .collect(Collectors.toList());
        dto.setImageUrls(imageUrls);

        List<ResidentApartment> residents = residentApartmentRepository
                .findCurrentResidentsByApartment(apartment.getApartmentId());

        List<ApartmentDetailDTO.ResidentInfo> residentInfos = residents.stream()
                .map(ra -> {
                    ApartmentDetailDTO.ResidentInfo info = new ApartmentDetailDTO.ResidentInfo();
                    Profile p = ra.getProfile();
                    info.setProfileId(p.getProfileId());
                    info.setFullName(p.getFullName());
                    info.setPhoneNumber(p.getPhoneNumber());
                    info.setEmail(p.getEmail());
                    info.setIsHouseholdOwner(p.getIsHouseholdOwner());
                    info.setMoveInDate(ra.getMoveInDate());
                    info.setCitizenId(p.getCitizenId());
                    info.setRelationshipToOwner(p.getRelationshipToOwner());
                    return info;
                })
                .collect(Collectors.toList());

        dto.setCurrentResidents(residentInfos);
        dto.setCurrentOccupancy(residentInfos.size());
        dto.setAvailableSlots(apartment.getMaxOccupancy() - residentInfos.size());

        return dto;
    }
}
