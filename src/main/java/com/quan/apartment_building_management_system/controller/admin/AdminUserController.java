package com.quan.apartment_building_management_system.controller.admin;

import com.quan.apartment_building_management_system.dto.user.UserDTO;
import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.entity.Role;
import com.quan.apartment_building_management_system.service.user.AccountService;
import com.quan.apartment_building_management_system.service.user.ProfileService;
import com.quan.apartment_building_management_system.service.user.RoleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class AdminUserController {

    private final ProfileService profileService;
    private final RoleService roleService;
    private final AccountService accountService;

    public AdminUserController(ProfileService profileService, RoleService roleService, AccountService accountService) {
        this.profileService = profileService;
        this.roleService = roleService;
        this.accountService = accountService;
    }

    @GetMapping("/admin/users")
    public String listUsers(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "roleId", required = false) Integer roleId,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> userPage = profileService.findFiltered(search, roleId, status, pageable);

        // Fetch counts for cards
        List<Profile> allProfiles = profileService.findAll();
        long totalUsers = allProfiles.stream().filter(p -> p.getAccount() != null).count();
        long totalResidents = allProfiles.stream()
                .filter(p -> p.getAccount() != null && "RESIDENT".equalsIgnoreCase(p.getAccount().getRole().getRoleName()))
                .count();
        long totalManagers = allProfiles.stream()
                .filter(p -> p.getAccount() != null && "MANAGER".equalsIgnoreCase(p.getAccount().getRole().getRoleName()))
                .count();
        long totalMaintenance = allProfiles.stream()
                .filter(p -> p.getAccount() != null && "MAINTENANCE_STAFF".equalsIgnoreCase(p.getAccount().getRole().getRoleName().replace(" ", "_")))
                .count();

        // Get list of all roles for filter dropdown
        List<Role> roles = roleService.findAll();

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("roleId", roleId);
        model.addAttribute("status", status);
        model.addAttribute("roles", roles);

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalResidents", totalResidents);
        model.addAttribute("totalManagers", totalManagers);
        model.addAttribute("totalMaintenance", totalMaintenance);

        model.addAttribute("activeTab", "users");

        return "admin/list_users";
    }

    @PostMapping("/admin/users/{id}/toggle-lock")
    public String toggleLock(@PathVariable("id") Integer accountId, RedirectAttributes redirectAttributes) {
        Optional<Account> accountOpt = accountService.findById(accountId);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            boolean newStatus = !account.getStatus();
            account.setStatus(newStatus);
            accountService.save(account);
            String statusMsg = newStatus ? "unlocked" : "locked";
            redirectAttributes.addFlashAttribute("message", "Account for " + account.getUsername() + " has been successfully " + statusMsg + "!");
            redirectAttributes.addFlashAttribute("messageType", "info");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/detail/{id}")
    public String showUserDetail(@PathVariable("id") Integer id, Model model) {
        Optional<Profile> profileOpt = profileService.findById(id);
        if (profileOpt.isEmpty()) {
            return "redirect:/admin/users";
        }
        model.addAttribute("profile", profileOpt.get());
        model.addAttribute("activeTab", "users");
        return "admin/detail_user";
    }

    @GetMapping("/admin/users/create")
    public String showCreateForm(Model model) {
        model.addAttribute("userDto", new UserDTO());
        model.addAttribute("activeTab", "users");
        return "admin/form_user";
    }

    @PostMapping("/admin/users/create")
    public String createUser(@Valid @ModelAttribute("userDto") UserDTO userDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (accountService.existsByUsername(userDto.getUsername())) {
            bindingResult.rejectValue("username", "error.userDto", "Username already exists!");
        }

        // Validate duplicate CitizenID
        if (userDto.getCitizenId() != null && !userDto.getCitizenId().isBlank()) {
            Optional<Profile> existingProfile = profileService.findByCitizenId(userDto.getCitizenId());
            if (existingProfile.isPresent()) {
                bindingResult.rejectValue("citizenId", "error.userDto", "Citizen ID already exists!");
            }
        }

        // Validate Move-In vs Move-Out dates
        if (userDto.getMoveInDate() != null && userDto.getMoveOutDate() != null) {
            if (userDto.getMoveInDate().isAfter(userDto.getMoveOutDate())) {
                bindingResult.rejectValue("moveOutDate", "error.userDto", "Move-out date must be after move-in date!");
            }
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeTab", "users");
            return "admin/form_user";
        }

        profileService.saveUserDTO(userDto);
        redirectAttributes.addFlashAttribute("message", "User " + userDto.getFullName() + " created successfully!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Optional<Profile> profileOpt = profileService.findById(id);
        if (profileOpt.isEmpty()) {
            return "redirect:/admin/users";
        }
        UserDTO userDto = new UserDTO(profileOpt.get());
        model.addAttribute("userDto", userDto);
        model.addAttribute("activeTab", "users");
        return "admin/form_user";
    }

    @PostMapping("/admin/users/edit/{id}")
    public String updateUser(@PathVariable("id") Integer id, @Valid @ModelAttribute("userDto") UserDTO userDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        // Validate duplicate CitizenID (excluding the profile currently being edited)
        if (userDto.getCitizenId() != null && !userDto.getCitizenId().isBlank()) {
            Optional<Profile> existingProfile = profileService.findByCitizenId(userDto.getCitizenId());
            if (existingProfile.isPresent() && !existingProfile.get().getProfileId().equals(id)) {
                bindingResult.rejectValue("citizenId", "error.userDto", "Citizen ID already exists!");
            }
        }

        // Validate Move-In vs Move-Out dates
        if (userDto.getMoveInDate() != null && userDto.getMoveOutDate() != null) {
            if (userDto.getMoveInDate().isAfter(userDto.getMoveOutDate())) {
                bindingResult.rejectValue("moveOutDate", "error.userDto", "Move-out date must be after move-in date!");
            }
        }

        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors()
                    .forEach(System.out::println);
            model.addAttribute("activeTab", "users");
            return "admin/form_user";
        }
        profileService.saveUserDTO(userDto);
        redirectAttributes.addFlashAttribute("message", "User " + userDto.getFullName() + " updated successfully!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/admin/users";
    }
}
