package com.quan.apartment_building_management_system.controller.manager;

import com.quan.apartment_building_management_system.entity.*;
import com.quan.apartment_building_management_system.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/manager/dashboard")
public class ManagerDashboardController {

    private final ApartmentRepository apartmentRepository;
    private final AccountRepository accountRepository;
    private final BillRepository billRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final UtilityBookingRepository utilityBookingRepository;

    public ManagerDashboardController(ApartmentRepository apartmentRepository,
                                      AccountRepository accountRepository,
                                      BillRepository billRepository,
                                      MaintenanceRequestRepository maintenanceRequestRepository,
                                      UtilityBookingRepository utilityBookingRepository) {
        this.apartmentRepository = apartmentRepository;
        this.accountRepository = accountRepository;
        this.billRepository = billRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.utilityBookingRepository = utilityBookingRepository;
    }

    @GetMapping
    public String showDashboard(HttpSession session, Model model) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null || !"MANAGER".equalsIgnoreCase(currentUser.getRole().getRoleName())) {
            return "redirect:/login";
        }

        // 1. KPI: Apartment Occupancy
        long totalApartments = apartmentRepository.count();
        long occupiedApartments = apartmentRepository.countByStatus((byte) 1) + apartmentRepository.countByStatus((byte) 2);
        long vacantApartments = apartmentRepository.countByStatus((byte) 0);
        double occupancyRate = totalApartments > 0 ? ((double) occupiedApartments / totalApartments) * 100 : 0.0;

        // 2. KPI: Active Residents
        long activeResidentsCount = accountRepository.countActiveResidents();

        // 3. KPI: Pending Revenue (Outstanding status=0 + Overdue status=2)
        BigDecimal outstanding = billRepository.sumOutstandingByDateRange(null, null, null, null, null);
        BigDecimal overdue = billRepository.sumOverdueByDateRange(null, null, null, null, null);
        if (outstanding == null) outstanding = BigDecimal.ZERO;
        if (overdue == null) overdue = BigDecimal.ZERO;
        BigDecimal pendingRevenue = outstanding.add(overdue);

        // 4. KPI: Pending Maintenance Requests (status 0: PENDING)
        List<MaintenanceRequest> allPendingRequests = maintenanceRequestRepository.findFiltered((byte) 0, null, null, null);
        long pendingMaintenanceCount = allPendingRequests.size();

        // 5. Recent 5 Maintenance Requests
        List<MaintenanceRequest> recentRequests = maintenanceRequestRepository.findAll(
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "requestDate"))
        ).getContent();

        // 6. Recent 5 Utility Bookings
        List<UtilityBooking> recentBookings = utilityBookingRepository.findAll(
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();

        // Add attributes to Model
        model.addAttribute("totalApartments", totalApartments);
        model.addAttribute("occupiedApartments", occupiedApartments);
        model.addAttribute("vacantApartments", vacantApartments);
        model.addAttribute("occupancyRate", String.format("%.1f", occupancyRate));
        model.addAttribute("activeResidentsCount", activeResidentsCount);
        model.addAttribute("pendingRevenue", pendingRevenue);
        model.addAttribute("pendingMaintenanceCount", pendingMaintenanceCount);
        model.addAttribute("recentRequests", recentRequests);
        model.addAttribute("recentBookings", recentBookings);

        return "manager/dashboard";
    }
}
