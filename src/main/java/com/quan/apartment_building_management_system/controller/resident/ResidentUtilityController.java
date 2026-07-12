package com.quan.apartment_building_management_system.controller.resident;

import com.quan.apartment_building_management_system.dto.utility.BookingRequestDTO;
import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.service.utility.ResidentUtilityService;
import com.quan.apartment_building_management_system.entity.UtilityBooking;
import com.quan.apartment_building_management_system.service.payment.PayOSService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/resident/utilities")
public class ResidentUtilityController {

    private final ResidentUtilityService utilityService;
    private final PayOSService payOSService;

    public ResidentUtilityController(ResidentUtilityService utilityService, PayOSService payOSService) {
        this.utilityService = utilityService;
        this.payOSService = payOSService;
    }

    private Account getCurrentUser(HttpSession session) {
        Account user = (Account) session.getAttribute("currentUser");
        if (user == null || !"RESIDENT".equalsIgnoreCase(user.getRole().getRoleName())) {
            throw new IllegalStateException("User not logged in or not a resident");
        }
        return user;
    }

    @GetMapping
    public String listUtilities(Model model, HttpSession session) {
        getCurrentUser(session);
        model.addAttribute("utilities", utilityService.getActiveUtilities());
        return "resident/utility/list";
    }

    @GetMapping("/{id}/resources")
    public String listResources(@PathVariable Integer id, Model model, HttpSession session) {
        getCurrentUser(session);
        model.addAttribute("utility", utilityService.getUtility(id));
        model.addAttribute("resources", utilityService.getActiveResources(id));
        return "resident/utility/resources";
    }

    @GetMapping("/resources/{id}")
    public String resourceDetail(@PathVariable Integer id, Model model, HttpSession session) {
        Account user = getCurrentUser(session);
        var resource = utilityService.getResourceDetail(id);
        model.addAttribute("resource", resource);
        
        // For FREE_USE, check membership
        var utility = utilityService.getUtility(resource.getUtilityId());
        if (Boolean.FALSE.equals(utility.getType())) {
            boolean hasMembership = utilityService.hasActiveMembership(user.getAccountId(), utility.getUtilityId());
            model.addAttribute("hasMembership", hasMembership);
            if (hasMembership) {
                model.addAttribute("activeMembership", utilityService.getActiveMembership(user.getAccountId(), utility.getUtilityId()));
            }
        }
        model.addAttribute("utilityType", utility.getType());
        model.addAttribute("utility", utility);
        return "resident/utility/resource_detail";
    }

    @GetMapping("/book/{id}")
    public String bookResource(@PathVariable Integer id, 
                               @RequestParam(required = false) String date,
                               Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Account user = getCurrentUser(session);
        var resource = utilityService.getResourceDetail(id);
        var utility = utilityService.getUtility(resource.getUtilityId());
        
        if (Boolean.FALSE.equals(utility.getType())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Tiện ích này không yêu cầu đặt trước.");
            return "redirect:/resident/utilities/resources/" + id;
        }
        
        LocalDate bookingDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : LocalDate.now();
        List<UtilityBooking> bookings = utilityService.getBookingsForDate(id, bookingDate);
        
        boolean hasMembership = utilityService.hasActiveMembership(user.getAccountId(), utility.getUtilityId());
        
        model.addAttribute("resource", resource);
        model.addAttribute("utility", utility);
        model.addAttribute("bookings", bookings);
        model.addAttribute("hasMembership", hasMembership);
        model.addAttribute("selectedDate", bookingDate);
        
        BookingRequestDTO req = new BookingRequestDTO();
        req.setBookingDate(bookingDate);
        model.addAttribute("bookingRequest", req);
        
        return "resident/utility/booking";
    }

    @PostMapping("/book")
    public String submitBooking(@ModelAttribute BookingRequestDTO request, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Account user = getCurrentUser(session);
            UtilityBooking booking = utilityService.submitBookingRequest(user.getAccountId(), request);
            
            boolean hasMembership = utilityService.hasActiveMembership(user.getAccountId(), request.getUtilityId());
            if (!hasMembership && "ONLINE".equalsIgnoreCase(request.getPaymentMethod())) {
                try {
                    String checkoutUrl = payOSService.createBookingPaymentLink(booking.getBookingId());
                    return "redirect:" + checkoutUrl;
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Không thể tạo link thanh toán: " + e.getMessage());
                    return "redirect:/resident/utilities/resources/" + request.getResourceId();
                }
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký sử dụng tiện ích thành công!");
            return "redirect:/resident/utilities/history";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/resident/utilities/resources/" + request.getResourceId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi, vui lòng thử lại.");
            return "redirect:/resident/utilities/resources/" + request.getResourceId();
        }
    }

    @GetMapping("/calculate")
    public String calculateRedirect() {
        return "redirect:/resident/utilities";
    }

    @PostMapping("/calculate")
    public String calculateSummary(@ModelAttribute BookingRequestDTO request, Model model, HttpSession session) {
        Account user = getCurrentUser(session);
        var resource = utilityService.getResourceDetail(request.getResourceId());
        var utility = utilityService.getUtility(resource.getUtilityId());
        
        LocalDate bookingDate = request.getBookingDate() != null ? request.getBookingDate() : LocalDate.now();
        List<UtilityBooking> bookings = utilityService.getBookingsForDate(request.getResourceId(), bookingDate);
        boolean hasMembership = utilityService.hasActiveMembership(user.getAccountId(), utility.getUtilityId());
        
        model.addAttribute("resource", resource);
        model.addAttribute("utility", utility);
        model.addAttribute("bookings", bookings);
        model.addAttribute("hasMembership", hasMembership);
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
                            utilityService.validateBookingTime(request.getResourceId(), start, end);
                        }

                        if (packageName.toLowerCase().contains("giờ") || packageName.toLowerCase().contains("hour")) {
                            long diffMinutes = java.time.Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
                            if (diffMinutes > 0) {
                                java.math.BigDecimal hours = java.math.BigDecimal.valueOf(diffMinutes).divide(java.math.BigDecimal.valueOf(60), 2, java.math.RoundingMode.HALF_UP);
                                total = price.getPrice().multiply(hours);
                            }
                        } else {
                            total = price.getPrice(); // Flat rate for non-hourly
                        }
                    } catch (IllegalArgumentException e) {
                        model.addAttribute("errorMessage", e.getMessage());
                    }
                }
            }
        }
        
        model.addAttribute("calculatedPackageName", packageName);
        model.addAttribute("calculatedTotal", total);
        
        return "resident/utility/booking";
    }

    @GetMapping("/rebook")
    public String rebookSummary(Model model, HttpSession session) {
        if (!model.containsAttribute("bookingRequest")) {
            return "redirect:/resident/utilities";
        }
        BookingRequestDTO request = (BookingRequestDTO) model.getAttribute("bookingRequest");
        return calculateSummary(request, model, session);
    }

    @GetMapping("/history")
    public String bookingHistory(Model model, HttpSession session) {
        Account user = getCurrentUser(session);
        model.addAttribute("bookings", utilityService.getBookingHistory(user.getAccountId()));
        return "resident/utility/history";
    }

    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Integer id, @RequestParam String reason, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Account user = getCurrentUser(session);
            utilityService.cancelBooking(user.getAccountId(), id, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Hủy đặt chỗ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/resident/utilities/history";
    }

    @GetMapping("/memberships")
    public String membershipHistory(Model model, HttpSession session) {
        Account user = getCurrentUser(session);
        model.addAttribute("memberships", utilityService.getMembershipHistory(user.getAccountId()));
        return "resident/utility/memberships";
    }
}
