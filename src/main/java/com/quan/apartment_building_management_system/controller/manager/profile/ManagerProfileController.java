package com.quan.apartment_building_management_system.controller.manager.profile;

import com.quan.apartment_building_management_system.dto.user.UserDTO;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.entity.Role;
import com.quan.apartment_building_management_system.service.user.ProfileService;
import com.quan.apartment_building_management_system.service.user.RoleService;
import com.quan.apartment_building_management_system.repository.VehicleRepository;
import com.quan.apartment_building_management_system.repository.BillRepository;
import com.quan.apartment_building_management_system.repository.UtilityBookingRepository;
import com.quan.apartment_building_management_system.repository.ProfileRepository;
import com.quan.apartment_building_management_system.repository.MaintenanceRequestRepository;
import com.quan.apartment_building_management_system.repository.MaintenanceTaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class ManagerProfileController {

    private final ProfileService profileService;
    private final RoleService roleService;
    private final ProfileRepository profileRepository;
    private final VehicleRepository vehicleRepository;
    private final BillRepository billRepository;
    private final UtilityBookingRepository utilityBookingRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final MaintenanceTaskRepository maintenanceTaskRepository;

    public ManagerProfileController(ProfileService profileService, 
                               RoleService roleService,
                               ProfileRepository profileRepository,
                               VehicleRepository vehicleRepository,
                               BillRepository billRepository,
                               UtilityBookingRepository utilityBookingRepository,
                               MaintenanceRequestRepository maintenanceRequestRepository,
                               MaintenanceTaskRepository maintenanceTaskRepository) {
        this.profileService = profileService;
        this.roleService = roleService;
        this.profileRepository = profileRepository;
        this.vehicleRepository = vehicleRepository;
        this.billRepository = billRepository;
        this.utilityBookingRepository = utilityBookingRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.maintenanceTaskRepository = maintenanceTaskRepository;
    }

    @GetMapping("/manager/profiles")
    public String listProfiles(
            @RequestParam(value = "type", defaultValue = "resident") String type,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {

        String roleName = "resident".equalsIgnoreCase(type) ? "RESIDENT" : "MAINTENANCE_STAFF";
        Optional<Role> roleOpt = roleService.findByRoleName(roleName);
        
        Integer roleId = null;
        if (roleOpt.isPresent()) {
            roleId = roleOpt.get().getRoleId();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> userPage = profileService.findFiltered(search, roleId, null, pageable);

        List<Profile> allProfiles = profileService.findAll();
        long totalResidents = allProfiles.stream()
                .filter(p -> p.getAccount() != null && "RESIDENT".equalsIgnoreCase(p.getAccount().getRole().getRoleName()))
                .count();
        long totalMaintenance = allProfiles.stream()
                .filter(p -> p.getAccount() != null && "MAINTENANCE_STAFF".equalsIgnoreCase(p.getAccount().getRole().getRoleName()))
                .count();

        model.addAttribute("profiles", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("type", type);

        model.addAttribute("totalResidents", totalResidents);
        model.addAttribute("totalMaintenance", totalMaintenance);
        model.addAttribute("activeTab", "profiles");
        model.addAttribute("pageTitle", "Profile Directory");

        return "manager/profiles/list";
    }

    @GetMapping("/manager/profiles/{id}")
    public String viewDetail(@PathVariable("id") Integer id, Model model) {
        Optional<Profile> profileOpt = profileService.findById(id);
        if (profileOpt.isEmpty()) {
            return "redirect:/manager/profiles";
        }
        
        Profile profile = profileOpt.get();
        model.addAttribute("profile", profile);
        model.addAttribute("activeTab", "profiles");
        model.addAttribute("pageTitle", "Profile Details");
        
        // Determine role and load operational data dynamically
        boolean isResident = false;
        if (profile.getAccount() != null && profile.getAccount().getRole() != null) {
            isResident = "RESIDENT".equalsIgnoreCase(profile.getAccount().getRole().getRoleName());
        }
        
        model.addAttribute("isResident", isResident);
        
        if (isResident) {
            // Load vehicles
            model.addAttribute("vehicles", vehicleRepository.findByProfileProfileId(id));
            
            // Load utility bookings
            model.addAttribute("bookings", utilityBookingRepository.findByProfileProfileId(id));
            
            // Load bills and household members (if assigned to an apartment)
            if (profile.getApartment() != null) {
                model.addAttribute("bills", billRepository.findByApartmentApartmentId(profile.getApartment().getApartmentId()));
                model.addAttribute("householdMembers", profileRepository.findByApartmentApartmentId(profile.getApartment().getApartmentId()));
            } else {
                model.addAttribute("bills", Collections.emptyList());
                model.addAttribute("householdMembers", Collections.emptyList());
            }
            
            // Load maintenance requests
            model.addAttribute("requests", maintenanceRequestRepository.findByProfileProfileId(id));
        } else {
            // For Maintenance Staff, load tasks assigned to their account
            if (profile.getAccount() != null) {
                model.addAttribute("tasks", maintenanceTaskRepository.findByStaffAccountId(profile.getAccount().getAccountId()));
            } else {
                model.addAttribute("tasks", Collections.emptyList());
            }
        }
        
        return "manager/profiles/detail";
    }
}
