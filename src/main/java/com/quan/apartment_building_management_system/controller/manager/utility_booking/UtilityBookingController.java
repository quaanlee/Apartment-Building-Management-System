package com.quan.apartment_building_management_system.controller.manager.utility_booking;

import com.quan.apartment_building_management_system.dto.booking.UtilityBookingDetailDto;
import com.quan.apartment_building_management_system.dto.booking.UtilityBookingFilterRequest;
import com.quan.apartment_building_management_system.dto.booking.UtilityBookingRowDto;
import com.quan.apartment_building_management_system.dto.booking.UtilityBookingStatsDto;
import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.Utility;
import com.quan.apartment_building_management_system.service.utility.UtilityBookingService;
import com.quan.apartment_building_management_system.service.utility.UtilityService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager controller for the Utility Booking Management feature.
 * Routes are prefixed with /manager/utility-bookings.
 */
@Controller
@RequestMapping("/manager/utility-bookings")
public class UtilityBookingController {

    private static final int PAGE_SIZE = 5;

    private final UtilityBookingService utilityBookingService;
    private final UtilityService utilityService;

    public UtilityBookingController(UtilityBookingService utilityBookingService,
                                    UtilityService utilityService) {
        this.utilityBookingService = utilityBookingService;
        this.utilityService = utilityService;
    }

    // ── Page render ──────────────────────────────────────────────────────────────

    /**
     * Renders the main Utility Booking Management page (SSR initial load).
     * Populates stat cards, first page of bookings, and utility filter dropdown.
     */
    @GetMapping
    public String bookingListPage(@ModelAttribute UtilityBookingFilterRequest filter,
                                  Model model,
                                  HttpSession session) {
        if (isUnauthorized(session)) return "redirect:/login";

        filter.setSize(PAGE_SIZE);

        Page<UtilityBookingRowDto> page = utilityBookingService.findFiltered(filter);
        UtilityBookingStatsDto stats   = utilityBookingService.getStats();
        List<Utility> utilities        = utilityService.findAll();

        model.addAttribute("bookings",      page.getContent());
        model.addAttribute("totalPages",    page.getTotalPages());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("currentPage",   filter.getPage());
        model.addAttribute("stats",         stats);
        model.addAttribute("utilities",     utilities);
        model.addAttribute("filter",        filter);
        model.addAttribute("pageTitle",     "Utility Booking Management");
        model.addAttribute("activeTab",     "bookings");

        return "manager/utility_bookings/list";
    }

    // ── AJAX endpoints ────────────────────────────────────────────────────────────

    /**
     * AJAX – Returns a paginated + filtered list of bookings as JSON.
     * Called by the JS filter/pagination logic without a full page reload.
     */
    @GetMapping("/list")
    @ResponseBody
    public Map<String, Object> getFilteredList(@ModelAttribute UtilityBookingFilterRequest filter,
                                               HttpSession session) {
        if (isUnauthorized(session)) return unauthorizedResponse();

        filter.setSize(PAGE_SIZE);
        Page<UtilityBookingRowDto> page = utilityBookingService.findFiltered(filter);

        Map<String, Object> result = new HashMap<>();
        result.put("bookings",      page.getContent());
        result.put("totalPages",    page.getTotalPages());
        result.put("totalElements", page.getTotalElements());
        result.put("currentPage",   filter.getPage());
        return result;
    }

    /**
     * AJAX – Returns full booking details for the View modal.
     */
    @GetMapping("/{id}/detail")
    @ResponseBody
    public Map<String, Object> getDetail(@PathVariable Integer id,
                                         HttpSession session) {
        if (isUnauthorized(session)) return unauthorizedResponse();

        try {
            UtilityBookingDetailDto detail = utilityBookingService.getDetail(id);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", detail);
            return result;
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    /**
     * AJAX – Returns current stats for the three summary cards.
     */
    @GetMapping("/stats")
    @ResponseBody
    public Map<String, Object> getStats(HttpSession session) {
        if (isUnauthorized(session)) return unauthorizedResponse();

        UtilityBookingStatsDto stats = utilityBookingService.getStats();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", stats);
        return result;
    }

    /**
     * AJAX – Updates the booking status (approve / reject / cancel).
     * @param status 1=Approved | 2=Rejected | 3=Cancelled
     */
    @PostMapping("/{id}/status")
    @ResponseBody
    public Map<String, Object> updateStatus(@PathVariable Integer id,
                                            @RequestParam Byte status,
                                            HttpSession session) {
        if (isUnauthorized(session)) return unauthorizedResponse();

        try {
            Account actor = (Account) session.getAttribute("currentUser");
            utilityBookingService.updateStatus(id, status, actor);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", resolveSuccessMessage(status));
            return result;
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────────

    private boolean isUnauthorized(HttpSession session) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null) return true;
        String role = currentUser.getRole().getRoleName().toUpperCase();
        return !"ADMIN".equals(role) && !"MANAGER".equals(role);
    }

    private Map<String, Object> unauthorizedResponse() {
        return errorResponse("Unauthorized access.");
    }

    private Map<String, Object> errorResponse(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        return result;
    }

    private String resolveSuccessMessage(Byte status) {
        if (status == null) return "Booking updated.";
        return switch (status) {
            case 1  -> "Booking approved successfully.";
            case 2  -> "Booking rejected successfully.";
            case 3  -> "Booking cancelled successfully.";
            default -> "Booking status updated.";
        };
    }
}
