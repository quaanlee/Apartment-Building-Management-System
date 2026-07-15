package com.quan.apartment_building_management_system.controller.authentication;

import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.service.user.AccountService;
import com.quan.apartment_building_management_system.service.system.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

@Controller
public class AuthController {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    private final AccountService accountService;
    private final NotificationService notificationService;

    public AuthController(AccountService accountService, NotificationService notificationService) {
        this.accountService = accountService;
        this.notificationService = notificationService;
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser != null) {
            return getRedirectUrlForRole(currentUser);
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String handleLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (email == null || email.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Email cannot be empty.");
            return "redirect:/login";
        }

        Optional<Account> accountOpt = accountService.findByUsername(email);

        if (accountOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password. Please try again.");
            return "redirect:/login";
        }

        Account account = accountOpt.get();

        if (account.getStatus() == null || !account.getStatus()) {
            redirectAttributes.addFlashAttribute("error", "Your account has been deactivated. Please contact support.");
            return "redirect:/login";
        }

        // Plaintext comparison for password authentication
        if (!account.getPassword().equals(password)) {
            redirectAttributes.addFlashAttribute("error", "Invalid email or password. Please try again.");
            return "redirect:/login";
        }

        session.setAttribute("currentUser", account);
        return getRedirectUrlForRole(account);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }


    @GetMapping("/maintainer/dashboard")
    public String maintainerDashboard(HttpSession session) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null
                || !"MAINTENANCE_STAFF".equalsIgnoreCase(currentUser.getRole().getRoleName().replace(" ", "_"))) {
            return "redirect:/login";
        }
        return "redirect:/maintenance_staff/dashboard";
    }

    // ==========================================
    // Forgot Password Flow
    // ==========================================

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @RequestParam("email") String email,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (email == null || !email.matches(EMAIL_REGEX)) {
            redirectAttributes.addFlashAttribute("error", "Invalid email format. Please enter a valid email address.");
            return "redirect:/forgot-password";
        }

        Optional<Account> accountOpt = accountService.findByUsername(email);
        if (accountOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Email address not found. Please try again.");
            return "redirect:/forgot-password";
        }

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(1000000));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(2);

        // Save in session
        session.setAttribute("forgotPasswordEmail", email);
        session.setAttribute("otp", otp);
        session.setAttribute("otpExpiry", expiry);

        // System Log output
        System.out.println("==================================================");
        System.out.println("[OTP SERVICE] Verification code for " + email + " is: " + otp);
        System.out.println("==================================================");

        // Send Email via Notification Service
        notificationService.sendOtpEmail(email, otp);

        return "redirect:/forgot-password/verify";
    }

    @GetMapping("/forgot-password/verify")
    public String verifyOtpPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("forgotPasswordEmail");
        if (email == null) {
            return "redirect:/forgot-password";
        }

        LocalDateTime expiry = (LocalDateTime) session.getAttribute("otpExpiry");
        if (expiry == null) {
            return "redirect:/forgot-password";
        }

        long remainingSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), expiry);
        if (remainingSeconds < 0) {
            remainingSeconds = 0;
        }

        model.addAttribute("obfuscatedEmail", obfuscateEmail(email));
        model.addAttribute("remainingSeconds", remainingSeconds);

        return "auth/otp-verification";
    }

    @PostMapping("/forgot-password/verify")
    public String handleVerifyOtp(
            @RequestParam("otp") String otp,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String email = (String) session.getAttribute("forgotPasswordEmail");
        String sessionOtp = (String) session.getAttribute("otp");
        LocalDateTime expiry = (LocalDateTime) session.getAttribute("otpExpiry");

        if (email == null || sessionOtp == null || expiry == null) {
            return "redirect:/forgot-password";
        }

        if (LocalDateTime.now().isAfter(expiry)) {
            redirectAttributes.addFlashAttribute("error", "Verification code has expired. Please request a new one.");
            return "redirect:/forgot-password/verify";
        }

        if (!sessionOtp.equals(otp)) {
            redirectAttributes.addFlashAttribute("error", "Invalid verification code. Please try again.");
            return "redirect:/forgot-password/verify";
        }

        // OTP is correct
        session.setAttribute("otpVerified", true);
        return "redirect:/forgot-password/reset";
    }

    @PostMapping("/forgot-password/resend")
    public String handleResendOtp(HttpSession session, RedirectAttributes redirectAttributes) {
        String email = (String) session.getAttribute("forgotPasswordEmail");
        if (email == null) {
            return "redirect:/forgot-password";
        }

        // Regenerate OTP
        String otp = String.format("%06d", new Random().nextInt(1000000));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(2);

        session.setAttribute("otp", otp);
        session.setAttribute("otpExpiry", expiry);

        // System Log output
        System.out.println("==================================================");
        System.out.println("[OTP SERVICE] RE-SENT: Verification code for " + email + " is: " + otp);
        System.out.println("==================================================");

        // Send Email via Notification Service
        notificationService.sendOtpEmail(email, otp);

        redirectAttributes.addFlashAttribute("error", "A new code has been sent to your email.");
        return "redirect:/forgot-password/verify";
    }

    @GetMapping("/forgot-password/reset")
    public String resetPasswordPage(HttpSession session) {
        String email = (String) session.getAttribute("forgotPasswordEmail");
        Boolean verified = (Boolean) session.getAttribute("otpVerified");

        if (email == null || verified == null || !verified) {
            return "redirect:/forgot-password";
        }

        return "auth/reset-password";
    }

    @PostMapping("/forgot-password/reset")
    public String handleResetPassword(
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String email = (String) session.getAttribute("forgotPasswordEmail");
        Boolean verified = (Boolean) session.getAttribute("otpVerified");

        if (email == null || verified == null || !verified) {
            return "redirect:/forgot-password";
        }

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match. Please try again.");
            return "redirect:/forgot-password/reset";
        }

        // Validate password complexity: at least 8 characters, uppercase, lowercase,
        // numbers
        if (password.length() < 8 || !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            redirectAttributes.addFlashAttribute("error",
                    "Password must be at least 8 characters long and contain uppercase, lowercase letters, and numbers.");
            return "redirect:/forgot-password/reset";
        }

        Optional<Account> accountOpt = accountService.findByUsername(email);
        if (accountOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Account not found.");
            return "redirect:/forgot-password";
        }

        Account account = accountOpt.get();
        account.setPassword(password); // Set new plaintext password
        accountService.save(account);

        // Clear session attributes
        session.removeAttribute("forgotPasswordEmail");
        session.removeAttribute("otp");
        session.removeAttribute("otpExpiry");
        session.removeAttribute("otpVerified");

        redirectAttributes.addFlashAttribute("success", "Password has been changed successfully!");
        return "redirect:/login";
    }

    private String obfuscateEmail(String email) {
        if (email == null || !email.contains("@"))
            return email;
        String[] parts = email.split("@");
        String local = parts[0];
        String domain = parts[1];
        if (local.length() <= 2) {
            return local + "***@" + domain;
        }
        return local.substring(0, 2) + "***@" + domain;
    }

    private String getRedirectUrlForRole(Account account) {
        String roleName = account.getRole().getRoleName().toUpperCase().replace(" ", "_");
        return switch (roleName) {
            case "ADMIN" -> "redirect:/admin/users";
            case "MANAGER" -> "redirect:/manager/utility-bookings";
            case "RESIDENT" -> "redirect:/resident/dashboard";
            case "MAINTENANCE_STAFF" -> "redirect:/maintenance_staff/dashboard";
            default -> "redirect:/login";
        };
    }
}
