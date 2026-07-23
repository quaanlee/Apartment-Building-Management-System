package com.quan.apartment_building_management_system.controller.manager.utility_booking;

import com.quan.apartment_building_management_system.dto.booking.UtilityBookingDetailDto;
import com.quan.apartment_building_management_system.dto.booking.UtilityBookingFilterRequest;
import com.quan.apartment_building_management_system.dto.booking.UtilityBookingRowDto;
import com.quan.apartment_building_management_system.dto.booking.UtilityBookingStatsDto;
import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.Utility;
import com.quan.apartment_building_management_system.entity.UtilityBooking;
import com.quan.apartment_building_management_system.dto.utility.BookingRequestDTO;
import com.quan.apartment_building_management_system.service.utility.ResidentUtilityService;
import com.quan.apartment_building_management_system.service.utility.UtilityBookingService;
import com.quan.apartment_building_management_system.service.utility.UtilityService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private static final int PAGE_SIZE = 7;

    private final UtilityBookingService utilityBookingService;
    private final UtilityService utilityService;
    private final ResidentUtilityService residentUtilityService;

    public UtilityBookingController(UtilityBookingService utilityBookingService,
                                    UtilityService utilityService,
                                    ResidentUtilityService residentUtilityService) {
        this.utilityBookingService = utilityBookingService;
        this.utilityService = utilityService;
        this.residentUtilityService = residentUtilityService;
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
        model.addAttribute("autoApproveEnabled", utilityBookingService.isAutoApproveEnabled());
        model.addAttribute("pageTitle",     "Utility Booking Management");
        model.addAttribute("activeTab",     "bookings");

        return "manager/utility_bookings/list";
    }

    /**
     * AJAX – Toggles the auto approve feature.
     */
    @PostMapping("/auto-approve/toggle")
    @ResponseBody
    public Map<String, Object> toggleAutoApprove(@RequestParam boolean enabled, HttpSession session) {
        if (isUnauthorized(session)) return unauthorizedResponse();

        try {
            Account user = (Account) session.getAttribute("user");
            utilityBookingService.setAutoApproveEnabled(enabled, user);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("enabled", enabled);
            return result;
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    // ── Manager Create Booking Flow ──────────────────────────────────────────────

    @GetMapping("/utilities")
    public String listUtilities(@RequestParam(value = "search", required = false) String search, Model model, HttpSession session) {
        if (isUnauthorized(session)) return "redirect:/login";
        model.addAttribute("utilities", residentUtilityService.getActiveUtilities(search));
        return "manager/utility_bookings/booking/utilities";
    }

    @GetMapping("/utilities/{id}/resources")
    public String listResources(@PathVariable Integer id, Model model, HttpSession session) {
        if (isUnauthorized(session)) return "redirect:/login";
        model.addAttribute("utility", residentUtilityService.getUtility(id));
        model.addAttribute("resources", residentUtilityService.getActiveResources(id));
        return "manager/utility_bookings/booking/resources";
    }

    @GetMapping("/resources/{id}")
    public String viewResourceDetail(@PathVariable Integer id, Model model, HttpSession session) {
        if (isUnauthorized(session)) return "redirect:/login";
        var resource = residentUtilityService.getResourceDetail(id);
        var utility = residentUtilityService.getUtility(resource.getUtilityId());
        
        model.addAttribute("resource", resource);
        model.addAttribute("utility", utility);
        model.addAttribute("utilityType", utility.getType());
        
        return "manager/utility_bookings/booking/resource_detail";
    }

    @GetMapping("/book/{id}")
    public String bookResource(@PathVariable Integer id, 
                               @RequestParam(required = false) String date,
                               Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isUnauthorized(session)) return "redirect:/login";
        var resource = residentUtilityService.getResourceDetail(id);
        var utility = residentUtilityService.getUtility(resource.getUtilityId());
        
        if (Boolean.FALSE.equals(utility.getType())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tiện ích này không yêu cầu đặt trước.");
            return "redirect:/manager/utility-bookings/utilities/" + utility.getUtilityId() + "/resources";
        }
        
        LocalDate bookingDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : LocalDate.now();
        List<UtilityBooking> bookings = residentUtilityService.getBookingsForDate(id, bookingDate);
        
        model.addAttribute("resource", resource);
        model.addAttribute("utility", utility);
        model.addAttribute("bookings", bookings);
        model.addAttribute("hasMembership", false);
        model.addAttribute("selectedDate", bookingDate);
        
        BookingRequestDTO req = new BookingRequestDTO();
        req.setBookingDate(bookingDate);
        model.addAttribute("bookingRequest", req);
        
        return "manager/utility_bookings/booking/booking_form";
    }

    @PostMapping("/calculate")
    public String calculateSummary(@ModelAttribute BookingRequestDTO request, Model model, HttpSession session) {
        if (isUnauthorized(session)) return "redirect:/login";
        var resource = residentUtilityService.getResourceDetail(request.getResourceId());
        var utility = residentUtilityService.getUtility(resource.getUtilityId());
        
        LocalDate bookingDate = request.getBookingDate() != null ? request.getBookingDate() : LocalDate.now();
        List<UtilityBooking> bookings = residentUtilityService.getBookingsForDate(request.getResourceId(), bookingDate);
        
        model.addAttribute("resource", resource);
        model.addAttribute("utility", utility);
        model.addAttribute("bookings", bookings);
        model.addAttribute("hasMembership", false);
        model.addAttribute("selectedDate", bookingDate);
        model.addAttribute("bookingRequest", request);
        
        String packageName = "-";
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        
        if (request.getPriceId() != null) {
            var priceOpt = resource.getPrices().stream().filter(p -> p.getUtilityPriceId().equals(request.getPriceId())).findFirst();
            if (priceOpt.isPresent()) {
                var price = priceOpt.get();
                packageName = price.getUnit().getUnitName();
                if (request.getStartTime() != null && request.getEndTime() != null) {
                    try {
                        LocalDateTime start = bookingDate.atTime(request.getStartTime());
                        LocalDateTime end = bookingDate.atTime(request.getEndTime());
                        if (Boolean.TRUE.equals(utility.getType())) {
                            residentUtilityService.validateBookingTime(request.getResourceId(), start, end);
                        }

                        if (packageName.toLowerCase().contains("giờ") || packageName.toLowerCase().contains("hour")) {
                            long diffMinutes = java.time.Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
                            if (diffMinutes > 0) {
                                java.math.BigDecimal hours = java.math.BigDecimal.valueOf(diffMinutes).divide(java.math.BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP);
                                total = price.getPrice().multiply(hours);
                            }
                        } else {
                            total = price.getPrice();
                        }
                    } catch (IllegalArgumentException e) {
                        model.addAttribute("errorMessage", e.getMessage());
                    }
                }
            }
        }
        
        model.addAttribute("calculatedPackageName", packageName);
        model.addAttribute("calculatedTotal", total);
        
        return "manager/utility_bookings/booking/booking_form";
    }

    @PostMapping("/book")
    public String submitBooking(@ModelAttribute BookingRequestDTO request, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isUnauthorized(session)) return "redirect:/login";
        try {
            residentUtilityService.submitBookingByManager(request);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo booking thành công!");
            return "redirect:/manager/utility-bookings";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/manager/utility-bookings/book/" + request.getResourceId() + "?date=" + request.getBookingDate();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi, vui lòng thử lại.");
            return "redirect:/manager/utility-bookings/book/" + request.getResourceId() + "?date=" + request.getBookingDate();
        }
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
            case 0  -> "Booking approval/rejection cancelled successfully.";
            case 1  -> "Booking approved successfully.";
            case 2  -> "Booking rejected successfully.";
            case 3  -> "Booking cancelled successfully.";
            default -> "Booking status updated.";
        };
    }
}
