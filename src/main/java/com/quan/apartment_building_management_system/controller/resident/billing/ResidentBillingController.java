package com.quan.apartment_building_management_system.controller.resident.billing;

import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.Apartment;
import com.quan.apartment_building_management_system.entity.Bill;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.repository.BillRepository;
import com.quan.apartment_building_management_system.repository.ProfileRepository;
import jakarta.servlet.http.HttpSession;
import com.quan.apartment_building_management_system.service.payment.PayOSService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/resident/billing")
public class ResidentBillingController {

    private final BillRepository billRepository;
    private final ProfileRepository profileRepository;
    private final PayOSService payOSService;

    public ResidentBillingController(BillRepository billRepository, ProfileRepository profileRepository, PayOSService payOSService) {
        this.billRepository = billRepository;
        this.profileRepository = profileRepository;
        this.payOSService = payOSService;
    }

    @GetMapping
    public String viewBills(@RequestParam(required = false) String period,
            @RequestParam(required = false) Byte status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            HttpSession session,
            Model model) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Profile> profileOpt = profileRepository.findByAccountAccountId(currentUser.getAccountId());
        if (profileOpt.isEmpty() || profileOpt.get().getApartment() == null) {
            model.addAttribute("error",
                    "Your account is not assigned any apartment!");
            model.addAttribute("bills", List.of());
            model.addAttribute("totalUnpaid", BigDecimal.ZERO);
            model.addAttribute("totalPaid", BigDecimal.ZERO);
            model.addAttribute("totalOverdue", BigDecimal.ZERO);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            return "resident/billing/list";
        }

        Profile profile = profileOpt.get();
        Apartment apartment = profile.getApartment();
        model.addAttribute("apartment", apartment);
        model.addAttribute("residentName", profile.getFullName());

        // Sync pending payments with PayOS on page load
        try {
            payOSService.syncPendingPaymentsForApartment(apartment.getApartmentId());
        } catch (Exception e) {
            System.err.println("[ResidentBillingController Sync Warning] " + e.getMessage());
        }

        // Parse YYYY-MM
        Short year = null;
        Byte month = null;
        if (period != null && period.matches("\\d{4}-\\d{2}")) {
            String[] parts = period.split("-");
            year = Short.parseShort(parts[0]);
            month = Byte.parseByte(parts[1]);
        }

        // Calculate summary metrics on all bills
        List<Bill> allBills = billRepository.findByApartmentApartmentId(apartment.getApartmentId());
        BigDecimal totalUnpaid = allBills.stream()
                .filter(b -> b.getStatus() != null && b.getStatus() == 0)
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaid = allBills.stream()
                .filter(b -> b.getStatus() != null && b.getStatus() == 1)
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOverdue = allBills.stream()
                .filter(b -> b.getStatus() != null && b.getStatus() == 2)
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Retrieve paginated & filtered bills
        Pageable pageable = PageRequest.of(page, size);
        Page<Bill> billPage = billRepository.findByApartmentAndFilter(
                apartment.getApartmentId(), status, month, year, pageable);

        model.addAttribute("bills", billPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", billPage.getTotalPages());
        model.addAttribute("totalItems", billPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("period", period);
        model.addAttribute("activeStatus", status);

        model.addAttribute("totalUnpaid", totalUnpaid);
        model.addAttribute("totalPaid", totalPaid);
        model.addAttribute("totalOverdue", totalOverdue);

        return "resident/billing/list";
    }

    @GetMapping("/detail/{billId}")
    public String viewBillDetail(@PathVariable Integer billId,
            HttpSession session,
            Model model) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Profile> profileOpt = profileRepository.findByAccountAccountId(currentUser.getAccountId());
        if (profileOpt.isEmpty() || profileOpt.get().getApartment() == null) {
            return "redirect:/resident/billing";
        }

        Profile profile = profileOpt.get();
        Apartment apartment = profile.getApartment();

        // Sync pending payments with PayOS on page load
        try {
            payOSService.syncPendingPaymentsForApartment(apartment.getApartmentId());
        } catch (Exception e) {
            System.err.println("[ResidentBillingController Detail Sync Warning] " + e.getMessage());
        }

        Optional<Bill> billOpt = billRepository.findById(billId);
        if (billOpt.isEmpty()) {
            return "redirect:/resident/billing";
        }

        Bill bill = billOpt.get();
        // Security check: ensure this bill belongs to the resident's apartment
        if (!bill.getApartment().getApartmentId().equals(apartment.getApartmentId())) {
            return "redirect:/resident/billing";
        }

        model.addAttribute("bill", bill);
        model.addAttribute("apartment", apartment);

        return "resident/billing/detail";
    }
}
