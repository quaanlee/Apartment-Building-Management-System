package com.quan.apartment_building_management_system.controller.admin;

import com.quan.apartment_building_management_system.dto.UserDTO;
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
            @RequestParam(value = "size", defaultValue = "10") int size,
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
                .filter(p -> p.getAccount() != null && "MAINTENANCE_STAFF".equalsIgnoreCase(p.getAccount().getRole().getRoleName()))
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

        return "admin/users";
    }

    @PostMapping("/admin/users/{id}/toggle-lock")
    public String toggleLock(@PathVariable("id") Integer accountId) {
        Optional<Account> accountOpt = accountService.findById(accountId);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.setStatus(!account.getStatus());
            accountService.save(account);
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
        return "admin/create_user";
    }

    @PostMapping("/admin/users/create")
    public String createUser(@Valid @ModelAttribute("userDto") UserDTO userDto, BindingResult bindingResult, Model model) {
        if (accountService.existsByUsername(userDto.getUsername())) {
            bindingResult.rejectValue("username", "error.userDto", "Username already exists!");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("activeTab", "users");
            return "admin/create_user";
        }

        profileService.saveUserDTO(userDto);
        return "redirect:/admin/users";
    }
}
