package com.quan.apartment_building_management_system.controller;

import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.EmployeeProfile;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.service.user.AccountService;
import com.quan.apartment_building_management_system.service.user.EmployeeProfileService;
import com.quan.apartment_building_management_system.service.user.ProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class ProfileController {

    private final ProfileService profileService;
    private final EmployeeProfileService employeeProfileService;
    private final AccountService accountService;

    public ProfileController(ProfileService profileService,
                             EmployeeProfileService employeeProfileService,
                             AccountService accountService) {
        this.profileService = profileService;
        this.employeeProfileService = employeeProfileService;
        this.accountService = accountService;
    }

    private Account getCurrentUser(HttpSession session) {
        return (Account) session.getAttribute("currentUser");
    }

    @GetMapping("/admin/profile")
    public String adminProfile(HttpSession session, Model model) {
        return showProfile(session, model, "admin");
    }

    @GetMapping("/manager/my-profile")
    public String managerProfile(HttpSession session, Model model) {
        return showProfile(session, model, "manager");
    }

    @GetMapping("/maintenance_staff/profile")
    public String staffProfile(HttpSession session, Model model) {
        return showProfile(session, model, "staff");
    }

    @GetMapping("/resident/profile")
    public String residentProfile(HttpSession session, Model model) {
        return showProfile(session, model, "resident");
    }

    private String showProfile(HttpSession session, Model model, String role) {
        Account user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        // Common info
        model.addAttribute("account", user);
        model.addAttribute("username", user.getUsername());

        if ("resident".equals(role)) {
            Optional<Profile> profileOpt = profileService.findByAccountId(user.getAccountId());
            if (profileOpt.isPresent()) {
                model.addAttribute("profile", profileOpt.get());
                model.addAttribute("profileType", "resident");
            }
            return "profile/view";
        } else {
            Optional<EmployeeProfile> empOpt = employeeProfileService.findByAccountId(user.getAccountId());
            if (empOpt.isPresent()) {
                model.addAttribute("employeeProfile", empOpt.get());
                model.addAttribute("profileType", "employee");
            }
            return "profile/view";
        }
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("fullName") String fullName,
                                 @RequestParam("phoneNumber") String phoneNumber,
                                 @RequestParam(value = "address", required = false) String address,
                                 @RequestParam(value = "gender", required = false) String gender,
                                 @RequestParam(value = "dateOfBirth", required = false) String dateOfBirth,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        Account user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        String role = getRolePath(user);
        boolean isResident = "resident".equals(role);

        try {
            if (isResident) {
                Optional<Profile> opt = profileService.findByAccountId(user.getAccountId());
                if (opt.isPresent()) {
                    Profile p = opt.get();
                    p.setFullName(fullName);
                    p.setPhoneNumber(phoneNumber);
                    if (gender != null) p.setGender(gender);
                    if (dateOfBirth != null && !dateOfBirth.isBlank())
                        p.setDateOfBirth(java.time.LocalDate.parse(dateOfBirth));
                    if (address != null) p.setPlaceOfBirth(address);
                    profileService.save(p);
                }
            } else {
                Optional<EmployeeProfile> opt = employeeProfileService.findByAccountId(user.getAccountId());
                if (opt.isPresent()) {
                    EmployeeProfile ep = opt.get();
                    ep.setFullName(fullName);
                    ep.setPhoneNumber(phoneNumber);
                    if (address != null) ep.setAddress(address);
                    if (gender != null) ep.setGender("1".equals(gender));
                    if (dateOfBirth != null && !dateOfBirth.isBlank())
                        ep.setDateOfBirth(java.time.LocalDate.parse(dateOfBirth));
                    employeeProfileService.save(ep);
                }
            }
            ra.addFlashAttribute("message", "Cập nhật hồ sơ thành công!");
            ra.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            ra.addFlashAttribute("message", "Lỗi: " + e.getMessage());
            ra.addFlashAttribute("messageType", "error");
        }
        return "redirect:/" + role + "/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                  @RequestParam("newPassword") String newPassword,
                                  @RequestParam("confirmPassword") String confirmPassword,
                                  HttpSession session,
                                  RedirectAttributes ra) {
        Account user = getCurrentUser(session);
        if (user == null) return "redirect:/login";

        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("message", "Mật khẩu mới không khớp!");
            ra.addFlashAttribute("messageType", "error");
            return "redirect:/" + getRolePath(user) + "/profile";
        }

        boolean success = accountService.changePassword(user.getAccountId(), currentPassword, newPassword);
        if (success) {
            ra.addFlashAttribute("message", "Đổi mật khẩu thành công!");
            ra.addFlashAttribute("messageType", "success");
        } else {
            ra.addFlashAttribute("message", "Mật khẩu hiện tại không đúng!");
            ra.addFlashAttribute("messageType", "error");
        }
        return "redirect:/" + getRolePath(user) + "/profile";
    }

    private String getRolePath(Account user) {
        if (user.getRole() == null) return "login";
        String role = user.getRole().getRoleName().toUpperCase();
        if (role.contains("ADMIN")) return "admin";
        if (role.contains("MANAGER")) return "manager/my";
        if (role.contains("MAINTENANCE")) return "maintenance_staff";
        if (role.contains("RESIDENT")) return "resident";
        return "login";
    }
}