package com.quan.apartment_building_management_system.controller.manager.profile;

import com.quan.apartment_building_management_system.dto.user.UserDTO;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.entity.Role;
import com.quan.apartment_building_management_system.service.user.AccountService;
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
    private final AccountService accountService;
    private final ProfileRepository profileRepository;
    private final VehicleRepository vehicleRepository;
    private final BillRepository billRepository;
    private final UtilityBookingRepository utilityBookingRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final MaintenanceTaskRepository maintenanceTaskRepository;

    public ManagerProfileController(ProfileService profileService, 
                               RoleService roleService,
                               AccountService accountService,
                               ProfileRepository profileRepository,
                               VehicleRepository vehicleRepository,
                               BillRepository billRepository,
                               UtilityBookingRepository utilityBookingRepository,
                               MaintenanceRequestRepository maintenanceRequestRepository,
                               MaintenanceTaskRepository maintenanceTaskRepository) {
        this.profileService = profileService;
        this.roleService = roleService;
        this.accountService = accountService;
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

        String roleName = "resident".equalsIgnoreCase(type) ? "Resident" : "Maintenance Staff";
        Optional<Role> roleOpt = roleService.findByRoleName(roleName);
        
        Integer roleId = null;
        if (roleOpt.isPresent()) {
            roleId = roleOpt.get().getRoleId();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> userPage = accountService.findFilteredAccounts(search, roleId, null, pageable);

        List<com.quan.apartment_building_management_system.entity.Account> allAccounts = accountService.findAll();
        long totalResidents = allAccounts.stream()
                .filter(a -> a.getRole() != null && "RESIDENT".equalsIgnoreCase(a.getRole().getRoleName().replace(" ", "_")))
                .count();
        long totalMaintenance = allAccounts.stream()
                .filter(a -> a.getRole() != null && "MAINTENANCE_STAFF".equalsIgnoreCase(a.getRole().getRoleName().replace(" ", "_")))
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
        Optional<com.quan.apartment_building_management_system.entity.Account> accountOpt = accountService.findById(id);
        if (accountOpt.isEmpty()) {
            return "redirect:/manager/profiles";
        }
        
        com.quan.apartment_building_management_system.entity.Account account = accountOpt.get();
        UserDTO user = new UserDTO(account);
        
        model.addAttribute("user", user);
        model.addAttribute("account", account);
        model.addAttribute("activeTab", "profiles");
        model.addAttribute("pageTitle", "Profile Details");
        
        // Determine role and load operational data dynamically
        boolean isResident = false;
        if (account.getRole() != null) {
            isResident = "RESIDENT".equalsIgnoreCase(account.getRole().getRoleName());
        }
        
        model.addAttribute("isResident", isResident);
        
        if (isResident) {
            Integer profileId = user.getProfileId();
            if (profileId != null) {
                // Load vehicles
                model.addAttribute("vehicles", vehicleRepository.findByProfileProfileId(profileId));
                
                // Load utility bookings
                model.addAttribute("bookings", utilityBookingRepository.findByProfileProfileId(profileId));
                
                // Load maintenance requests
                model.addAttribute("requests", maintenanceRequestRepository.findByProfileProfileId(profileId));
                
                // Load bills and household members (if assigned to an apartment)
                Optional<Profile> pOpt = profileService.findById(profileId);
                if (pOpt.isPresent() && pOpt.get().getApartment() != null) {
                    Integer aptId = pOpt.get().getApartment().getApartmentId();
                    model.addAttribute("bills", billRepository.findByApartmentApartmentId(aptId));
                    model.addAttribute("householdMembers", profileRepository.findByApartmentApartmentId(aptId));
                    model.addAttribute("apartment", pOpt.get().getApartment());
                } else {
                    model.addAttribute("bills", Collections.emptyList());
                    model.addAttribute("householdMembers", Collections.emptyList());
                }
            } else {
                model.addAttribute("vehicles", Collections.emptyList());
                model.addAttribute("bookings", Collections.emptyList());
                model.addAttribute("requests", Collections.emptyList());
                model.addAttribute("bills", Collections.emptyList());
                model.addAttribute("householdMembers", Collections.emptyList());
            }
        } else {
            // For Maintenance Staff, load tasks assigned to their account
            model.addAttribute("tasks", maintenanceTaskRepository.findByStaffAccountId(id));
        }
        
        return "manager/profiles/detail";
    }
}
