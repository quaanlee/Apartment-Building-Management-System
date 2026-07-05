package com.quan.apartment_building_management_system.controller.resident.billing;

import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.Apartment;
import com.quan.apartment_building_management_system.entity.Bill;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.repository.BillRepository;
import com.quan.apartment_building_management_system.repository.ProfileRepository;
import jakarta.servlet.http.HttpSession;
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

    public ResidentBillingController(BillRepository billRepository, ProfileRepository profileRepository) {
        this.billRepository = billRepository;
        this.profileRepository = profileRepository;
    }

    @GetMapping
    public String viewBills(@RequestParam(required = false) Byte status,
                            HttpSession session,
                            Model model) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Profile> profileOpt = profileRepository.findByAccountAccountId(currentUser.getAccountId());
        if (profileOpt.isEmpty() || profileOpt.get().getApartment() == null) {
            model.addAttribute("error", "Tài khoản của bạn chưa được liên kết với căn hộ nào. Vui lòng liên hệ Ban quản lý.");
            model.addAttribute("bills", List.of());
            model.addAttribute("totalUnpaid", BigDecimal.ZERO);
            model.addAttribute("totalPaid", BigDecimal.ZERO);
            model.addAttribute("totalOverdue", BigDecimal.ZERO);
            return "resident/billing/list";
        }

        Profile profile = profileOpt.get();
        Apartment apartment = profile.getApartment();
        model.addAttribute("apartment", apartment);
        model.addAttribute("residentName", profile.getFullName());

        List<Bill> rawBills = billRepository.findByApartmentApartmentId(apartment.getApartmentId());
        List<Bill> bills = new ArrayList<>(rawBills);

        // Calculate summary metrics
        BigDecimal totalUnpaid = bills.stream()
                .filter(b -> b.getStatus() != null && b.getStatus() == 0)
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaid = bills.stream()
                .filter(b -> b.getStatus() != null && b.getStatus() == 1)
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOverdue = bills.stream()
                .filter(b -> b.getStatus() != null && b.getStatus() == 2)
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Apply status filter if present
        if (status != null) {
            bills = bills.stream()
                    .filter(b -> b.getStatus() != null && b.getStatus().equals(status))
                    .collect(Collectors.toList());
        }

        // Sort bills so that the newest billing month/year is first
        bills.sort((b1, b2) -> {
            int yearCompare = Short.compare(b2.getBillYear(), b1.getBillYear());
            if (yearCompare != 0) return yearCompare;
            return Byte.compare(b2.getBillMonth(), b1.getBillMonth());
        });

        model.addAttribute("bills", bills);
        model.addAttribute("totalUnpaid", totalUnpaid);
        model.addAttribute("totalPaid", totalPaid);
        model.addAttribute("totalOverdue", totalOverdue);
        model.addAttribute("activeStatus", status);

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
        model.addAttribute("ownerName", profile.getFullName());
        model.addAttribute("apartment", apartment);

        return "resident/billing/detail";
    }
}
