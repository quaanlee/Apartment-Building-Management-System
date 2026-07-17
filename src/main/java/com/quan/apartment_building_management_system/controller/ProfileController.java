package com.quan.apartment_building_management_system.controller;

import com.quan.apartment_building_management_system.dto.user.UserDTO;
import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.EmployeeProfile;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.service.user.AccountService;
import com.quan.apartment_building_management_system.service.user.EmployeeProfileService;
import com.quan.apartment_building_management_system.service.user.ProfileService;
import com.quan.apartment_building_management_system.service.utility.CloudinaryUploadService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class ProfileController {

    private final ProfileService profileService;
    private final EmployeeProfileService employeeProfileService;
    private final AccountService accountService;
    private final CloudinaryUploadService cloudinaryUploadService;
    private final com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService;

    public ProfileController(ProfileService profileService,
            EmployeeProfileService employeeProfileService,
            AccountService accountService,
            CloudinaryUploadService cloudinaryUploadService,
            com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService) {
        this.profileService = profileService;
        this.employeeProfileService = employeeProfileService;
        this.accountService = accountService;
        this.cloudinaryUploadService = cloudinaryUploadService;
        this.systemLogService = systemLogService;
    }

    private Account getCurrentUser(HttpSession session) {
        return (Account) session.getAttribute("currentUser");
    }

    @GetMapping("admin/profile/profile")
    public String adminProfile(@RequestParam(value = "tab", defaultValue = "personal") String tab, HttpSession session,
            Model model) {
        return showProfile(session, model, "admin", tab);
    }

    @GetMapping("/manager/my-profile")
    public String managerProfile(@RequestParam(value = "tab", defaultValue = "personal") String tab,
            HttpSession session, Model model) {
        return showProfile(session, model, "manager", tab);
    }

    @GetMapping("/resident/profile")
    public String residentProfile(@RequestParam(value = "tab", defaultValue = "personal") String tab,
            HttpSession session, Model model) {
        return showProfile(session, model, "resident", tab);
    }

    private String showProfile(HttpSession session, Model model, String role, String tab) {
        Account user = getCurrentUser(session);
        if (user == null)
            return "redirect:/login";

        Optional<Account> accountOpt = accountService.findById(user.getAccountId());
        if (accountOpt.isEmpty())
            return "redirect:/login";
        Account account = accountOpt.get();

        UserDTO userDto;
        boolean isResident = account.getRole() != null
                && account.getRole().getRoleName().toUpperCase().contains("RESIDENT");

        if (isResident) {
            Optional<Profile> profileOpt = profileService.findByAccountId(user.getAccountId());
            if (profileOpt.isPresent()) {
                userDto = new UserDTO(profileOpt.get());
                // Ensure account data is still set just in case the relationship from profile
                // -> account is lazy/missing
                userDto.setEmail(account.getUsername());
                userDto.setAccountId(account.getAccountId());
                userDto.setRoleName(account.getRole() != null ? account.getRole().getRoleName() : null);
            } else {
                userDto = new UserDTO(account);
            }
        } else {
            Optional<EmployeeProfile> empOpt = employeeProfileService.findByAccountId(user.getAccountId());
            if (empOpt.isPresent()) {
                userDto = new UserDTO(empOpt.get());
                userDto.setEmail(account.getUsername());
                userDto.setAccountId(account.getAccountId());
                userDto.setRoleName(account.getRole() != null ? account.getRole().getRoleName() : null);
            } else {
                userDto = new UserDTO(account);
            }
        }

        model.addAttribute("userDto", userDto);
        model.addAttribute("activeProfileTab", tab);
        model.addAttribute("pageTitle", "Hồ sơ cá nhân");
        model.addAttribute("activeTab", "profile");

        if ("admin".equals(role)) {
            return "admin/profile/profile";
        } else if ("manager".equals(role)) {
            return "manager/profile/profile";
        } else {
            return "resident/profile";
        }
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("userDto") UserDTO userDto,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            HttpSession session,
            RedirectAttributes ra,
            Model model) {
        Account user = getCurrentUser(session);
        if (user == null)
            return "redirect:/login";

        // Re-fetch account from DB to ensure lazy associations (role) are loaded
        Account resolvedUser = accountService.findById(user.getAccountId()).orElse(user);

        String redirectUrl = getProfileRedirectUrl(resolvedUser);
        boolean isResident = resolvedUser.getRole() != null
                && resolvedUser.getRole().getRoleName().toUpperCase().contains("RESIDENT");

        // Validate phone number uniqueness
        String newPhone = userDto.getPhoneNumber();
        boolean phoneExists = false;
        if (newPhone != null && !newPhone.isBlank()) {
            Optional<Profile> pOpt = profileService.findByPhoneNumber(newPhone);
            if (pOpt.isPresent() && pOpt.get().getAccount() != null
                    && !pOpt.get().getAccount().getAccountId().equals(user.getAccountId())) {
                phoneExists = true;
            }
            if (!phoneExists) {
                Optional<EmployeeProfile> epOpt = employeeProfileService.findByPhoneNumber(newPhone);
                if (epOpt.isPresent() && epOpt.get().getAccount() != null
                        && !epOpt.get().getAccount().getAccountId().equals(user.getAccountId())) {
                    phoneExists = true;
                }
            }
        }

        boolean hasRealErrors = false;
        String firstErrorMessage = "Vui lòng kiểm tra lại thông tin nhập vào.";
        if (bindingResult.hasErrors()) {
            for (org.springframework.validation.FieldError error : bindingResult.getFieldErrors()) {
                if (!"password".equals(error.getField()) && !"roleName".equals(error.getField())) {
                    hasRealErrors = true;
                    if (error.getDefaultMessage() != null) {
                        firstErrorMessage = error.getDefaultMessage();
                    }
                }
            }
        }

        if (hasRealErrors) {
            model.addAttribute("message", "Cập nhật thất bại: " + firstErrorMessage);
            model.addAttribute("messageType", "error");
            model.addAttribute("pageTitle", "Hồ sơ cá nhân");
            model.addAttribute("activeTab", "profile");
            model.addAttribute("activeProfileTab", "personal");
            model.addAttribute("userDto", userDto);
            if (redirectUrl.contains("admin"))
                return "admin/profile/profile";
            if (redirectUrl.contains("manager"))
                return "manager/profile/profile";
            return "resident/profile";
        }

        if (phoneExists) {
            ra.addFlashAttribute("message", "Số điện thoại này đã được sử dụng bởi tài khoản khác!");
            ra.addFlashAttribute("messageType", "error");
            return "redirect:" + redirectUrl;
        }

        // Upload avatar if present
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String url = cloudinaryUploadService.uploadImage(avatarFile, "abms/avatars");
                if (url != null) {
                    userDto.setAvatarUrl(url);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ra.addFlashAttribute("message", "Lỗi tải ảnh lên: " + e.getMessage());
                ra.addFlashAttribute("messageType", "error");
                return "redirect:" + redirectUrl;
            }
        } else {
            // Keep existing avatar
            if (isResident) {
                Optional<Profile> opt = profileService.findByAccountId(user.getAccountId());
                opt.ifPresent(p -> userDto.setAvatarUrl(p.getAvatarUrl()));
            } else {
                Optional<EmployeeProfile> opt = employeeProfileService.findByAccountId(user.getAccountId());
                opt.ifPresent(ep -> userDto.setAvatarUrl(ep.getAvatarUrl()));
            }
        }

        try {
            if (isResident) {
                Optional<Profile> opt = profileService.findByAccountId(user.getAccountId());
                if (opt.isPresent()) {
                    Profile p = opt.get();
                    UserDTO oldDto = new UserDTO(p);
                    p.setFullName(userDto.getFullName());
                    p.setPhoneNumber(userDto.getPhoneNumber());
                    if (userDto.getGender() != null)
                        p.setGender(userDto.getGender());
                    if (userDto.getDateOfBirth() != null)
                        p.setDateOfBirth(userDto.getDateOfBirth());
                    if (userDto.getPlaceOfBirth() != null)
                        p.setPlaceOfBirth(userDto.getPlaceOfBirth());
                    if (userDto.getCitizenId() != null)
                        p.setCitizenId(userDto.getCitizenId());
                    if (userDto.getCitizenIdIssueDate() != null)
                        p.setCitizenIdIssueDate(userDto.getCitizenIdIssueDate());
                    if (userDto.getCitizenIdIssuePlace() != null)
                        p.setCitizenIdIssuePlace(userDto.getCitizenIdIssuePlace());
                    if (userDto.getNationality() != null)
                        p.setNationality(userDto.getNationality());
                    if (userDto.getEthnicity() != null)
                        p.setEthnicity(userDto.getEthnicity());
                    if (userDto.getEmergencyContactName() != null)
                        p.setEmergencyContactName(userDto.getEmergencyContactName());
                    if (userDto.getEmergencyContactPhone() != null)
                        p.setEmergencyContactPhone(userDto.getEmergencyContactPhone());
                    if (userDto.getRelationshipToOwner() != null)
                        p.setRelationshipToOwner(userDto.getRelationshipToOwner());
                    if (userDto.getOccupation() != null)
                        p.setOccupation(userDto.getOccupation());
                    if (userDto.getAvatarUrl() != null)
                        p.setAvatarUrl(userDto.getAvatarUrl());
                    profileService.save(p);
                    systemLogService.logSystemAction("UPDATE_PROFILE", "Profile", p.getProfileId(), oldDto, new UserDTO(p), "Updated personal profile");
                }
            } else {
                Optional<EmployeeProfile> opt = employeeProfileService.findByAccountId(user.getAccountId());
                if (opt.isPresent()) {
                    EmployeeProfile ep = opt.get();
                    UserDTO oldDto = new UserDTO(ep);
                    ep.setFullName(userDto.getFullName());
                    ep.setPhoneNumber(userDto.getPhoneNumber());
                    if (userDto.getAddress() != null)
                        ep.setAddress(userDto.getAddress());
                    if (userDto.getGender() != null) {
                        ep.setGender("Nam".equalsIgnoreCase(userDto.getGender()) || "1".equals(userDto.getGender())
                                || "true".equalsIgnoreCase(userDto.getGender()));
                    }
                    if (userDto.getDateOfBirth() != null)
                        ep.setDateOfBirth(userDto.getDateOfBirth());
                    if (userDto.getAvatarUrl() != null)
                        ep.setAvatarUrl(userDto.getAvatarUrl());
                    employeeProfileService.save(ep);
                    systemLogService.logSystemAction("UPDATE_EMPLOYEE_PROFILE", "EmployeeProfile", ep.getEmployeeProfileId(), oldDto, new UserDTO(ep), "Updated personal employee profile");
                }
            }

            // Refresh session
            Optional<Account> updatedAccountOpt = accountService.findById(user.getAccountId());
            updatedAccountOpt.ifPresent(updatedAccount -> session.setAttribute("currentUser", updatedAccount));

            ra.addFlashAttribute("message", "Cập nhật hồ sơ thành công!");
            ra.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            ra.addFlashAttribute("message", "Lỗi: " + e.getMessage());
            ra.addFlashAttribute("messageType", "error");
        }
        return "redirect:" + redirectUrl;
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            RedirectAttributes ra) {
        Account user = getCurrentUser(session);
        if (user == null)
            return "redirect:/login";

        // Re-fetch account from DB to ensure lazy associations (role) are loaded
        Account resolvedUser = accountService.findById(user.getAccountId()).orElse(user);
        String redirectUrl = getProfileRedirectUrl(resolvedUser);


        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("message", "Mật khẩu mới không khớp!");
            ra.addFlashAttribute("messageType", "error");
            return "redirect:" + redirectUrl + "?tab=password";
        }

        if (newPassword.length() < 8 || !newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            ra.addFlashAttribute("message",
                    "Mật khẩu mới phải có ít nhất 8 ký tự và bao gồm chữ hoa, chữ thường và chữ số.");
            ra.addFlashAttribute("messageType", "error");
            return "redirect:" + redirectUrl + "?tab=password";
        }

        boolean success = accountService.changePassword(user.getAccountId(), currentPassword, newPassword);
        if (success) {
            // Update session
            Optional<Account> updatedAccountOpt = accountService.findById(user.getAccountId());
            updatedAccountOpt.ifPresent(updatedAccount -> session.setAttribute("currentUser", updatedAccount));

            systemLogService.logSystemAction("CHANGE_PASSWORD", "Account", user.getAccountId(),
                    com.quan.apartment_building_management_system.dto.systemlog.AccountLogDTO.fromEntity(resolvedUser),
                    com.quan.apartment_building_management_system.dto.systemlog.AccountLogDTO.fromEntity(resolvedUser),
                    "Changed password for account " + resolvedUser.getUsername());

            ra.addFlashAttribute("message", "Đổi mật khẩu thành công!");
            ra.addFlashAttribute("messageType", "success");
        } else {
            ra.addFlashAttribute("message", "Mật khẩu hiện tại không đúng!");
            ra.addFlashAttribute("messageType", "error");
            return "redirect:" + redirectUrl + "?tab=password";
        }
        return "redirect:" + redirectUrl + "?tab=password";
    }

    private String getProfileRedirectUrl(Account user) {
        if (user == null || user.getRole() == null)
            return "/login";
        String role = user.getRole().getRoleName().toUpperCase();
        if (role.contains("ADMIN"))
            return "admin/profile/profile";
        if (role.contains("MANAGER"))
            return "/manager/my-profile";
        if (role.contains("MAINTENANCE"))
            return "/maintenance_staff/profile";
        if (role.contains("RESIDENT"))
            return "/resident/profile";
        return "/login";
    }
}
