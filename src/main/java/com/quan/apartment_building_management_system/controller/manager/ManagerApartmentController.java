package com.quan.apartment_building_management_system.controller.manager;

import com.quan.apartment_building_management_system.dto.ApartmentDTO;
import com.quan.apartment_building_management_system.dto.ApartmentDetailDTO;
import com.quan.apartment_building_management_system.dto.AssignResidentRequest;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.service.apartment.ApartmentManagerService;
import com.quan.apartment_building_management_system.service.user.ProfileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/manager/apartments")
public class ManagerApartmentController {

    private final ApartmentManagerService apartmentManagerService;
    private final ProfileService profileService;

    public ManagerApartmentController(ApartmentManagerService apartmentManagerService, ProfileService profileService) {
        this.apartmentManagerService = apartmentManagerService;
        this.profileService = profileService;
    }

    // 1. View Apartment List
    @GetMapping
    public String listApartments(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) Byte floor,
            @RequestParam(required = false) Byte status,
            @RequestParam(required = false) BigDecimal minArea,
            @RequestParam(required = false) BigDecimal maxArea,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "apartmentId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ApartmentDTO> apartmentPage = apartmentManagerService.getFilteredApartments(
                search.isEmpty() ? null : search,
                floor,
                status,
                minArea,
                maxArea,
                pageable);

        model.addAttribute("apartments", apartmentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", apartmentPage.getTotalPages());
        model.addAttribute("totalItems", apartmentPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("floor", floor);
        model.addAttribute("status", status);
        model.addAttribute("minArea", minArea);
        model.addAttribute("maxArea", maxArea);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("size", size);

        // Add Statistics
        model.addAttribute("totalUnits", apartmentManagerService.getTotalApartments());
        model.addAttribute("vacantUnits", apartmentManagerService.countByStatus((byte) 0));
        model.addAttribute("occupiedUnits", apartmentManagerService.countByStatus((byte) 1));
        model.addAttribute("maintenanceUnits", apartmentManagerService.countByStatus((byte) 2));

        return "manager/apartment/apartment-list";
    }

    // 2. View Apartment Detail
    @GetMapping("/{id}")
    public String viewApartmentDetail(@PathVariable Integer id, Model model) {
        ApartmentDetailDTO apartment = apartmentManagerService.getApartmentDetail(id);
        model.addAttribute("apartment", apartment);
        return "manager/apartment/apartment-detail";
    }

    // 3. Update Status - Show Form
    @GetMapping("/{id}/edit-status")
    public String showEditStatusForm(@PathVariable Integer id, Model model) {
        ApartmentDetailDTO apartment = apartmentManagerService.getApartmentDetail(id);
        model.addAttribute("apartment", apartment);
        return "manager/apartment/apartment-edit-status";
    }

    // 3. Update Status - Submit
    @PostMapping("/{id}/update-status")
    public String updateApartmentStatus(
            @PathVariable Integer id,
            @RequestParam Byte status,
            RedirectAttributes redirectAttributes) {

        try {
            apartmentManagerService.updateApartmentStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed: " + e.getMessage());
        }

        return "redirect:/manager/apartments/" + id;
    }

    // 4. Assign Resident - Show Form
    @GetMapping("/{id}/assign-resident")
    public String showAssignResidentForm(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        ApartmentDetailDTO apartment = apartmentManagerService.getApartmentDetail(id);
        model.addAttribute("apartment", apartment);

        Pageable pageable = PageRequest.of(page, size);
        Page<Profile> residentsPage = apartmentManagerService.getAvailableResidentsPaged(search, id, pageable);

        model.addAttribute("residents", residentsPage.getContent());
        for(Profile p : residentsPage.getContent()){
            System.out.println("===================================");
            System.out.println(p);
            System.out.println("===================================");
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", residentsPage.getTotalPages());
        model.addAttribute("totalItems", residentsPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("hasAvailableSlot", apartment.getAvailableSlots() > 0);
        model.addAttribute("size", size);

        return "manager/apartment/apartment-assign-resident";
    }

    // 4. Assign Resident - Submit
    @PostMapping("/{id}/assign-resident")
    public String assignResident(
            @PathVariable Integer id,
            @ModelAttribute AssignResidentRequest request,
            RedirectAttributes redirectAttributes) {

        if (request.getProfileId() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed: Please select a resident.");
            return "redirect:/manager/apartments/" + id + "/assign-resident";
        }
        if (request.getMoveInDate() == null) {
            request.setMoveInDate(LocalDate.now());
        }

        try {
            apartmentManagerService.assignResidentToApartment(
                    request.getProfileId(),
                    id,
                    request.getMoveInDate(),
                    request.getIsHouseholdOwner()
            );
            redirectAttributes.addFlashAttribute("successMessage", "Resident assigned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed: " + e.getMessage());
        }

        return "redirect:/manager/apartments/" + id;
    }

    // 5. Move Out Resident - Submit
    @PostMapping("/{apartmentId}/move-out/{profileId}")
    public String moveOutResident(
            @PathVariable Integer apartmentId,
            @PathVariable Integer profileId,
            RedirectAttributes redirectAttributes) {

        try {
            apartmentManagerService.moveOutResident(profileId, LocalDate.now());
            redirectAttributes.addFlashAttribute("successMessage", "Resident moved out successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed: " + e.getMessage());
        }

        return "redirect:/manager/apartments/" + apartmentId;
    }

    // 6. View Resident Profile Detail
    @GetMapping("/profiles/detail/{id}")
    public String showResidentDetail(@PathVariable("id") Integer id, Model model) {
        Optional<Profile> profileOpt = profileService.findById(id);
        if (profileOpt.isEmpty()) {
            return "redirect:/manager/apartments";
        }
        model.addAttribute("profile", profileOpt.get());
        model.addAttribute("activeTab", "apartments");
        return "manager/apartment/detail_user";
    }
}
