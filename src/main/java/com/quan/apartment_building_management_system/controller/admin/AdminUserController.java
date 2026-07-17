package com.quan.apartment_building_management_system.controller.admin;

import com.quan.apartment_building_management_system.dto.user.UserDTO;
import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.entity.EmployeeProfile;
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
    private final com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService;

    public AdminUserController(ProfileService profileService, RoleService roleService, AccountService accountService,
            com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService) {
        this.profileService = profileService;
        this.roleService = roleService;
        this.accountService = accountService;
        this.systemLogService = systemLogService;
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
        Page<UserDTO> userPage = accountService.findFilteredAccounts(search, roleId, status, pageable);

        // Fetch counts for cards
        List<Account> allAccounts = accountService.findAll();
        long totalUsers = allAccounts.size();
        long totalResidents = allAccounts.stream()
                .filter(a -> a.getRole() != null && "RESIDENT".equalsIgnoreCase(a.getRole().getRoleName()))
                .count();
        long totalManagers = allAccounts.stream()
                .filter(a -> a.getRole() != null && "MANAGER".equalsIgnoreCase(a.getRole().getRoleName()))
                .count();
        long totalMaintenance = allAccounts.stream()
                .filter(a -> a.getRole() != null && "MAINTENANCE_STAFF".equalsIgnoreCase(a.getRole().getRoleName().replace(" ", "_")))
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

        return "admin/user/list_users";
    }

    @PostMapping("/admin/users/{id}/toggle-lock")
    public String toggleLock(@PathVariable("id") Integer accountId, RedirectAttributes redirectAttributes) {
        Optional<Account> accountOpt = accountService.findById(accountId);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            UserDTO oldDto = new UserDTO(account);
            boolean newStatus = !account.getStatus();
            account.setStatus(newStatus);
            account = accountService.save(account);
            UserDTO newDto = new UserDTO(account);
            
            String actionName = newStatus ? "UNLOCK_ACCOUNT" : "LOCK_ACCOUNT";
            String desc = "Account " + account.getUsername() + " was " + (newStatus ? "unlocked" : "locked");
            systemLogService.logSystemAction(actionName, "Account", account.getAccountId(), oldDto, newDto, desc);
            
            String statusMsg = newStatus ? "mở khóa" : "khóa";
            redirectAttributes.addFlashAttribute("message",
                    "Tài khoản " + account.getUsername() + " đã được " + statusMsg + " thành công!");
            redirectAttributes.addFlashAttribute("messageType", "info");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/detail/{id}")
    public String showUserDetail(@PathVariable("id") Integer id, Model model) {
        Optional<Account> accountOpt = accountService.findById(id);
        if (accountOpt.isEmpty()) {
            return "redirect:/admin/users";
        }
        
        Account account = accountOpt.get();
        UserDTO user = new UserDTO(account);
        
        model.addAttribute("user", user);
        model.addAttribute("account", account);
        model.addAttribute("activeTab", "users");
        return "admin/user/detail_user";
    }

    @GetMapping("/admin/users/create")
    public String showCreateForm(Model model) {
        model.addAttribute("userDto", new UserDTO());
        model.addAttribute("activeTab", "users");
        return "admin/user/form_user";
    }

    @PostMapping("/admin/users/create")
    public String createUser(@Valid @ModelAttribute("userDto") UserDTO userDto, BindingResult bindingResult,
            Model model, RedirectAttributes redirectAttributes) {
        if (accountService.existsByUsername(userDto.getEmail())) {
            bindingResult.rejectValue("email", "error.userDto", "Email (Tên đăng nhập) đã tồn tại trong hệ thống!");
        }

        // Xác thực CCCD cho RESIDENT
        if ("RESIDENT".equalsIgnoreCase(userDto.getRoleName())) {
            if (userDto.getCitizenId() == null || userDto.getCitizenId().isBlank()) {
                bindingResult.rejectValue("citizenId", "error.userDto", "CCCD là bắt buộc đối với Cư dân!");
            } else if (!userDto.getCitizenId().matches("^[0-9]{12}$")) {
                bindingResult.rejectValue("citizenId", "error.userDto", "CCCD phải gồm đúng 12 chữ số!");
            } else {
                Optional<Profile> existingProfile = profileService.findByCitizenId(userDto.getCitizenId());
                if (existingProfile.isPresent()) {
                    bindingResult.rejectValue("citizenId", "error.userDto", "CCCD đã tồn tại trong hệ thống!");
                }
            }
        }

        // Xác thực ngày vào/ra
        if (userDto.getMoveInDate() != null && userDto.getMoveOutDate() != null) {
            if (userDto.getMoveInDate().isAfter(userDto.getMoveOutDate())) {
                bindingResult.rejectValue("moveOutDate", "error.userDto", "Ngày chuyển ra phải sau ngày chuyển vào!");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("activeTab", "users");
            return "admin/user/form_user";
        }

        UserDTO savedDto = profileService.saveUserDTO(userDto);

        Integer entityId = savedDto.getProfileId() != null ? savedDto.getProfileId() : savedDto.getEmployeeProfileId();
        String entityName = savedDto.getProfileId() != null ? "Profile" : "EmployeeProfile";
        String actionName = savedDto.getProfileId() != null ? "CREATE_PROFILE" : "CREATE_EMPLOYEE_PROFILE";
        com.quan.apartment_building_management_system.dto.systemlog.ProfileLogDTO newProfileDto =
                com.quan.apartment_building_management_system.dto.systemlog.ProfileLogDTO.fromUserDTO(savedDto);
        systemLogService.logSystemAction(actionName, entityName, entityId,
                com.quan.apartment_building_management_system.dto.systemlog.ProfileLogDTO.empty(),
                newProfileDto, "Created profile for " + savedDto.getFullName());
        
        redirectAttributes.addFlashAttribute("message", "Người dùng " + userDto.getFullName() + " đã được tạo thành công!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Optional<Account> accountOpt = accountService.findById(id);
        if (accountOpt.isEmpty()) {
            return "redirect:/admin/users";
        }
        UserDTO userDto = new UserDTO(accountOpt.get());
        model.addAttribute("userDto", userDto);
        model.addAttribute("activeTab", "users");
        return "admin/user/form_user";
    }

    @PostMapping("/admin/users/edit/{id}")
    public String updateUser(@PathVariable("id") Integer id, @Valid @ModelAttribute("userDto") UserDTO userDto,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        // Xác thực CCCD cho RESIDENT
        if ("RESIDENT".equalsIgnoreCase(userDto.getRoleName())) {
            if (userDto.getCitizenId() == null || userDto.getCitizenId().isBlank()) {
                bindingResult.rejectValue("citizenId", "error.userDto", "CCCD là bắt buộc đối với Cư dân!");
            } else if (!userDto.getCitizenId().matches("^[0-9]{12}$")) {
                bindingResult.rejectValue("citizenId", "error.userDto", "CCCD phải gồm đúng 12 chữ số!");
            } else {
                Optional<Profile> existingProfile = profileService.findByCitizenId(userDto.getCitizenId());
                if (existingProfile.isPresent() && existingProfile.get().getAccount() != null && !existingProfile.get().getAccount().getAccountId().equals(id)) {
                    bindingResult.rejectValue("citizenId", "error.userDto", "CCCD đã tồn tại trong hệ thống!");
                }
            }
        }

        // Xác thực ngày vào/ra
        if (userDto.getMoveInDate() != null && userDto.getMoveOutDate() != null) {
            if (userDto.getMoveInDate().isAfter(userDto.getMoveOutDate())) {
                bindingResult.rejectValue("moveOutDate", "error.userDto", "Ngày chuyển ra phải sau ngày chuyển vào!");
            }
        }

        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors()
                    .forEach(System.out::println);
            model.addAttribute("activeTab", "users");
            return "admin/user/form_user";
        }
        
        Optional<Account> oldAccOpt = accountService.findById(id);
        com.quan.apartment_building_management_system.dto.systemlog.ProfileLogDTO oldProfileDto =
                oldAccOpt.isPresent() ? com.quan.apartment_building_management_system.dto.systemlog.ProfileLogDTO.fromUserDTO(new UserDTO(oldAccOpt.get())) :
                com.quan.apartment_building_management_system.dto.systemlog.ProfileLogDTO.empty();

        UserDTO savedDto = profileService.saveUserDTO(userDto);

        Integer entityId = savedDto.getProfileId() != null ? savedDto.getProfileId() : savedDto.getEmployeeProfileId();
        String entityName = savedDto.getProfileId() != null ? "Profile" : "EmployeeProfile";
        String actionName = savedDto.getProfileId() != null ? "UPDATE_PROFILE" : "UPDATE_EMPLOYEE_PROFILE";
        com.quan.apartment_building_management_system.dto.systemlog.ProfileLogDTO newProfileDto =
                com.quan.apartment_building_management_system.dto.systemlog.ProfileLogDTO.fromUserDTO(savedDto);
        systemLogService.logSystemAction(actionName, entityName, entityId, oldProfileDto, newProfileDto, "Updated profile for " + savedDto.getFullName());
        
        redirectAttributes.addFlashAttribute("message", "Người dùng " + userDto.getFullName() + " đã được cập nhật thành công!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/admin/users";
    }
}
