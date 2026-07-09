package com.quan.apartment_building_management_system.controller.resident;

import com.quan.apartment_building_management_system.entity.*;
import com.quan.apartment_building_management_system.repository.*;
import com.quan.apartment_building_management_system.service.payment.PayOSService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/resident/dashboard")
public class ResidentDashboardController {

    private final ProfileRepository profileRepository;
    private final BillRepository billRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UtilityBookingRepository utilityBookingRepository;
    private final VehicleRepository vehicleRepository;
    private final AccountNotificationRepository accountNotificationRepository;
    private final PayOSService payOSService;

    public ResidentDashboardController(ProfileRepository profileRepository,
                                       BillRepository billRepository,
                                       MaintenanceRequestRepository maintenanceRequestRepository,
                                       UtilityBookingRepository utilityBookingRepository,
                                       VehicleRepository vehicleRepository,
                                       AccountNotificationRepository accountNotificationRepository,
                                       PayOSService payOSService) {
        this.profileRepository = profileRepository;
        this.billRepository = billRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.utilityBookingRepository = utilityBookingRepository;
        this.vehicleRepository = vehicleRepository;
        this.accountNotificationRepository = accountNotificationRepository;
        this.payOSService = payOSService;
    }

    @GetMapping
    public String showDashboard(HttpSession session, Model model) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Profile> profileOpt = profileRepository.findByAccountAccountId(currentUser.getAccountId());
        if (profileOpt.isEmpty() || profileOpt.get().getApartment() == null) {
            model.addAttribute("error", "Tài khoản của bạn chưa được liên kết với căn hộ nào. Vui lòng liên hệ Ban quản lý.");
            return "resident/dashboard";
        }

        Profile profile = profileOpt.get();
        Apartment apartment = profile.getApartment();

        // Sync pending payments with PayOS on page load
        try {
            payOSService.syncPendingPaymentsForApartment(apartment.getApartmentId());
        } catch (Exception e) {
            System.err.println("[ResidentDashboard Sync Warning] " + e.getMessage());
        }

        // 1. KPI: Outstanding Balance (Total amount of bills with status 0 or 2)
        List<Bill> allBills = billRepository.findByApartmentApartmentId(apartment.getApartmentId());
        BigDecimal unpaidBalance = allBills.stream()
                .filter(b -> b.getStatus() != null && (b.getStatus() == 0 || b.getStatus() == 2))
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. KPI: Active Repairs (Status PENDING (0) or ASSIGNED (1))
        List<MaintenanceRequest> repairs = maintenanceRequestRepository.findByApartmentApartmentId(apartment.getApartmentId());
        long activeRepairsCount = repairs.stream()
                .filter(r -> r.getStatus() != null && (r.getStatus() == 0 || r.getStatus() == 1))
                .count();

        // 3. KPI: Upcoming Amenity Bookings (Status 0: PENDING or 1: APPROVED, and start time in the future)
        List<UtilityBooking> bookings = utilityBookingRepository.findByProfileProfileId(profile.getProfileId());
        long upcomingBookingsCount = bookings.stream()
                .filter(b -> b.getStatus() != null && (b.getStatus() == 0 || b.getStatus() == 1) && b.getStartTime().isAfter(LocalDateTime.now()))
                .count();

        // 4. KPI: Household members & registered vehicles count
        List<Profile> householdMembers = profileRepository.findByApartmentApartmentId(apartment.getApartmentId());
        long householdSize = householdMembers.size();

        long householdVehiclesCount = 0;
        for (Profile m : householdMembers) {
            List<Vehicle> vList = vehicleRepository.findByProfileProfileId(m.getProfileId());
            if (vList != null) {
                householdVehiclesCount += vList.size();
            }
        }

        // 5. Recent 3 Bills
        List<Bill> recentBills = allBills.stream()
                .limit(3)
                .collect(Collectors.toList());

        // 6. Recent 3 Notifications for this resident account
        List<AccountNotification> accountNotifications = accountNotificationRepository.findByAccountAccountId(currentUser.getAccountId());
        List<Notification> recentNotifications = accountNotifications.stream()
                .map(AccountNotification::getNotification)
                .sorted((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()))
                .limit(3)
                .collect(Collectors.toList());

        // Populate Model Attributes
        model.addAttribute("profile", profile);
        model.addAttribute("apartment", apartment);
        model.addAttribute("unpaidBalance", unpaidBalance);
        model.addAttribute("activeRepairsCount", activeRepairsCount);
        model.addAttribute("upcomingBookingsCount", upcomingBookingsCount);
        model.addAttribute("householdSize", householdSize);
        model.addAttribute("householdVehiclesCount", householdVehiclesCount);
        model.addAttribute("recentBills", recentBills);
        model.addAttribute("recentNotifications", recentNotifications);

        return "resident/dashboard";
    }
}
