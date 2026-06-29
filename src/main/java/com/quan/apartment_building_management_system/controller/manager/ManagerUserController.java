package com.quan.apartment_building_management_system.controller.manager;

import com.quan.apartment_building_management_system.dto.UserDTO;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.entity.Role;
import com.quan.apartment_building_management_system.service.user.ProfileService;
import com.quan.apartment_building_management_system.service.user.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class ManagerUserController {

    private final ProfileService profileService;
    private final RoleService roleService;

    public ManagerUserController(ProfileService profileService, RoleService roleService) {
        this.profileService = profileService;
        this.roleService = roleService;
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

        return "manager/profiles";
    }

    @GetMapping("/manager/profiles/{id}")
    public String viewDetail(@PathVariable("id") Integer id, Model model) {
        Optional<Profile> profileOpt = profileService.findById(id);
        if (profileOpt.isPresent()) {
            model.addAttribute("profile", profileOpt.get());
        }
        model.addAttribute("activeTab", "profiles");
        model.addAttribute("pageTitle", "Profile Details");
        return "manager/detail_user";
    }
}
