package com.quan.apartment_building_management_system.controller.authentication;

import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.service.user.AccountService;
import com.quan.apartment_building_management_system.service.system.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService;

    public AuthController(AccountService accountService, NotificationService notificationService, PasswordEncoder passwordEncoder,
                          com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService) {
        this.accountService = accountService;
        this.notificationService = notificationService;
        this.passwordEncoder = passwordEncoder;
        this.systemLogService = systemLogService;
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
            redirectAttributes.addFlashAttribute("error", "Email không được để trống.");
            return "redirect:/login";
        }

        Optional<Account> accountOpt = accountService.findByUsername(email);

        if (accountOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Email hoặc mật khẩu không đúng. Vui lòng thử lại.");
            return "redirect:/login";
        }

        Account account = accountOpt.get();

        if (account.getStatus() == null || !account.getStatus()) {
            redirectAttributes.addFlashAttribute("error", "Tài khoản của bạn đã bị vô hiệu hóa. Vui lòng liên hệ bộ phận hỗ trợ.");
            return "redirect:/login";
        }

        // Authenticate using PasswordEncoder
        if (account.getPassword() == null) {
            redirectAttributes.addFlashAttribute("error", "Tài khoản không hợp lệ.");
            return "redirect:/login";
        }

        // Allow both plain text (for existing un-hashed passwords) and BCrypt passwords temporarily during transition
        boolean isPasswordValid = false;
        if (account.getPassword().startsWith("$2a$") || account.getPassword().startsWith("$2b$") || account.getPassword().startsWith("$2y$")) {
            isPasswordValid = passwordEncoder.matches(password, account.getPassword());
        } else {
            // Plain text comparison for backward compatibility
            isPasswordValid = account.getPassword().equals(password);
            // Optionally, we could hash and update the password here, but we will keep it simple
        }

        if (!isPasswordValid) {
            redirectAttributes.addFlashAttribute("error", "Email hoặc mật khẩu không đúng. Vui lòng thử lại.");
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
            redirectAttributes.addFlashAttribute("error", "Định dạng email không hợp lệ. Vui lòng nhập địa chỉ email đúng.");
            return "redirect:/forgot-password";
        }

        Optional<Account> accountOpt = accountService.findByUsername(email);
        if (accountOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy địa chỉ email này. Vui lòng thử lại.");
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
            redirectAttributes.addFlashAttribute("error", "Mã xác thực đã hết hạn. Vui lòng yêu cầu mã mới.");
            return "redirect:/forgot-password/verify";
        }

        if (!sessionOtp.equals(otp)) {
            redirectAttributes.addFlashAttribute("error", "Mã xác thực không đúng. Vui lòng thử lại.");
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

        redirectAttributes.addFlashAttribute("error", "Mã mới đã được gửi đến email của bạn.");
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
            redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp. Vui lòng thử lại.");
            return "redirect:/forgot-password/reset";
        }

        // Validate password complexity: at least 8 characters, uppercase, lowercase,
        // numbers
        if (password.length() < 8 || !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            redirectAttributes.addFlashAttribute("error",
                    "Mật khẩu phải có ít nhất 8 ký tự và bao gồm chữ hoa, chữ thường và chữ số.");
            return "redirect:/forgot-password/reset";
        }

        Optional<Account> accountOpt = accountService.findByUsername(email);
        if (accountOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản.");
            return "redirect:/forgot-password";
        }

        Account accountBeforeReset = accountOpt.get();
        com.quan.apartment_building_management_system.dto.systemlog.AccountLogDTO oldAccountDto =
                com.quan.apartment_building_management_system.dto.systemlog.AccountLogDTO.fromEntity(accountBeforeReset);

        accountService.resetPassword(email, password);

        Account resetAccount = accountService.findByUsername(email).orElse(accountBeforeReset);
        com.quan.apartment_building_management_system.dto.systemlog.AccountLogDTO newAccountDto =
                com.quan.apartment_building_management_system.dto.systemlog.AccountLogDTO.fromEntity(resetAccount);
        systemLogService.logSystemAction("RESET_PASSWORD", "Account",
                resetAccount.getAccountId(),
                oldAccountDto, newAccountDto, "Password reset via forgot-password flow for: " + email);

        // Clear session attributes
        session.removeAttribute("forgotPasswordEmail");
        session.removeAttribute("otp");
        session.removeAttribute("otpExpiry");
        session.removeAttribute("otpVerified");

        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
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
            case "ADMIN" -> "redirect:/admin/dashboard";
            case "MANAGER" -> "redirect:/manager/dashboard";
            case "RESIDENT" -> "redirect:/resident/dashboard";
            case "MAINTENANCE_STAFF" -> "redirect:/maintenance_staff/dashboard";
            default -> "redirect:/login";
        };
    }
}
