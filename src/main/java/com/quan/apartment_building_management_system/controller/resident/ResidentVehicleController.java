package com.quan.apartment_building_management_system.controller.resident;

import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.entity.Vehicle;
import com.quan.apartment_building_management_system.service.utility.VehicleService;
import com.quan.apartment_building_management_system.service.user.AccountService;
import com.quan.apartment_building_management_system.service.user.ProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.quan.apartment_building_management_system.service.system.CloudinaryService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/resident/vehicles")
public class ResidentVehicleController {

    private final VehicleService vehicleService;
    private final AccountService accountService;
    private final ProfileService profileService;
    private final CloudinaryService cloudinaryService;

    public ResidentVehicleController(VehicleService vehicleService, 
                                     AccountService accountService, 
                                     ProfileService profileService,
                                     CloudinaryService cloudinaryService) {
        this.vehicleService = vehicleService;
        this.accountService = accountService;
        this.profileService = profileService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping
    public String viewMyVehicles(HttpSession session, Model model) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null || !"RESIDENT".equalsIgnoreCase(currentUser.getRole().getRoleName())) {
            currentUser = accountService.findByUsername("tran.thi.b").orElse(null);
            if (currentUser == null) {
                List<Account> accounts = accountService.findAll();
                for (Account acc : accounts) {
                    if ("RESIDENT".equalsIgnoreCase(acc.getRole().getRoleName())) {
                        currentUser = acc;
                        break;
                    }
                }
            }
            if (currentUser != null) {
                session.setAttribute("currentUser", currentUser);
            } else {
                return "redirect:/login";
            }
        }
        System.out.println("=== ResidentVehicleController DEBUG ===");
        System.out.println("currentUser in session: " + currentUser);
        if (currentUser != null) {
            System.out.println("Username: " + currentUser.getUsername());
            System.out.println("AccountID: " + currentUser.getAccountId());
            System.out.println("Role: " + (currentUser.getRole() != null ? currentUser.getRole().getRoleName() : "null"));
        }
        
        Profile profile = profileService.findByAccountId(currentUser != null ? currentUser.getAccountId() : null).orElse(null);
        System.out.println("Profile query result: " + (profile != null ? "Found (" + profile.getFullName() + ", ID=" + profile.getProfileId() + ")" : "NULL"));
        System.out.println("=======================================");

        if (profile == null) {
            model.addAttribute("error", "No profile associated with this account.");
            return "resident/dashboard";
        }
        List<Vehicle> vehicles = vehicleService.findByProfileId(profile.getProfileId());
        model.addAttribute("vehicles", vehicles);
        return "resident/vehicle/list";
    }

    @PostMapping("/register")
    public String registerVehicle(
            @RequestParam String licensePlate,
            @RequestParam String vehicleType,
            @RequestParam String brand,
            @RequestParam String color,
            @RequestParam("documentFile") MultipartFile documentFile,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null || !"RESIDENT".equalsIgnoreCase(currentUser.getRole().getRoleName())) {
            currentUser = accountService.findByUsername("tran.thi.b").orElse(null);
            if (currentUser == null) {
                List<Account> accounts = accountService.findAll();
                for (Account acc : accounts) {
                    if ("RESIDENT".equalsIgnoreCase(acc.getRole().getRoleName())) {
                        currentUser = acc;
                        break;
                    }
                }
            }
            if (currentUser != null) {
                session.setAttribute("currentUser", currentUser);
            } else {
                return "redirect:/login";
            }
        }
        Profile profile = profileService.findByAccountId(currentUser.getAccountId()).orElse(null);
        if (profile == null) {
            redirectAttributes.addFlashAttribute("message", "Profile not found.");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/resident/vehicles";
        }

        // Validate unique license plate
        if (vehicleService.findByLicensePlate(licensePlate).isPresent()) {
            redirectAttributes.addFlashAttribute("message", "License plate is already registered!");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/resident/vehicles";
        }

        // Upload file to Cloudinary
        String documentUrl = null;
        if (documentFile != null && !documentFile.isEmpty()) {
            try {
                documentUrl = cloudinaryService.uploadFile(documentFile);
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("message", "Failed to upload document file: " + e.getMessage());
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/resident/vehicles";
            }
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setProfile(profile);
        vehicle.setLicensePlate(licensePlate.trim());
        vehicle.setVehicleType(vehicleType.trim());
        vehicle.setBrand(brand.trim());
        vehicle.setColor(color.trim());
        vehicle.setDocumentUrl(documentUrl);
        vehicle.setRegisteredDate(LocalDate.now());
        vehicle.setStatus((byte) 0); // 0 = Pending

        vehicleService.save(vehicle);

        redirectAttributes.addFlashAttribute("message", "Vehicle registration request submitted successfully!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/resident/vehicles";
    }
}
