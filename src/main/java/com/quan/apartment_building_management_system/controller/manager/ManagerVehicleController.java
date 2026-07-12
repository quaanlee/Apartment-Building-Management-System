package com.quan.apartment_building_management_system.controller.manager;

import com.quan.apartment_building_management_system.entity.Vehicle;
import com.quan.apartment_building_management_system.service.utility.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/manager/vehicles")
public class ManagerVehicleController {

    private final VehicleService vehicleService;

    public ManagerVehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public String viewVehiclesDashboard(Model model) {
        long totalRegistered = vehicleService.countByStatus((byte) 1);
        long pendingApprovalsCount = vehicleService.countByStatus((byte) 0);
        List<Vehicle> allVehicles = vehicleService.findAll(); // Get all vehicles

        model.addAttribute("totalRegistered", totalRegistered);
        model.addAttribute("pendingApprovals", pendingApprovalsCount);
        model.addAttribute("allVehicles", allVehicles);
        return "manager/vehicle/vehicle-list";
    }

    @PostMapping("/{id}/approve")
    public String approveVehicle(@PathVariable Integer id, Principal principal, RedirectAttributes redirectAttributes) {
        String username = (principal != null) ? principal.getName() : "system";
        vehicleService.approveVehicle(id, username);
        redirectAttributes.addFlashAttribute("message", "Vehicle registration approved successfully.");
        return "redirect:/manager/vehicles";
    }

    @PostMapping("/{id}/reject")
    public String rejectVehicle(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        vehicleService.rejectVehicle(id);
        redirectAttributes.addFlashAttribute("message", "Vehicle registration rejected.");
        redirectAttributes.addFlashAttribute("messageType", "warning");
        return "redirect:/manager/vehicles";
    }

    @PostMapping("/{id}/revoke")
    public String revokeVehicle(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        vehicleService.revokeVehicle(id);
        redirectAttributes.addFlashAttribute("message", "Vehicle registration revoked successfully.");
        redirectAttributes.addFlashAttribute("messageType", "warning");
        return "redirect:/manager/vehicles";
    }
}
