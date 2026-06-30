package com.quan.apartment_building_management_system.controller.admin;

import com.quan.apartment_building_management_system.entity.*;
import com.quan.apartment_building_management_system.repository.PaymentMethodRepository;
import com.quan.apartment_building_management_system.service.apartment.ApartmentService;
import com.quan.apartment_building_management_system.service.billing.BillDetailService;
import com.quan.apartment_building_management_system.service.billing.BillService;
import com.quan.apartment_building_management_system.service.billing.PaymentService;
import com.quan.apartment_building_management_system.service.user.AccountService;
import com.quan.apartment_building_management_system.service.utility.ServiceItemService;
import com.quan.apartment_building_management_system.service.utility.UtilityBookingService;
import com.quan.apartment_building_management_system.dto.admin.BillDTO;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/bills")
public class AdminBillController {

    private final BillService billService;
    private final BillDetailService billDetailService;
    private final ApartmentService apartmentService;
    private final ServiceItemService serviceItemService;
    private final UtilityBookingService utilityBookingService;
    private final PaymentService paymentService;
    private final AccountService accountService;
    private final PaymentMethodRepository paymentMethodRepository;

    public AdminBillController(BillService billService,
                               BillDetailService billDetailService,
                               ApartmentService apartmentService,
                               ServiceItemService serviceItemService,
                               UtilityBookingService utilityBookingService,
                               PaymentService paymentService,
                               AccountService accountService,
                               PaymentMethodRepository paymentMethodRepository) {
        this.billService = billService;
        this.billDetailService = billDetailService;
        this.apartmentService = apartmentService;
        this.serviceItemService = serviceItemService;
        this.utilityBookingService = utilityBookingService;
        this.paymentService = paymentService;
        this.accountService = accountService;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    // 1. View & Search Bill List
    @GetMapping
    public String listBills(@RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "status", required = false) Byte status,
                            @RequestParam(value = "month", required = false) Byte month,
                            @RequestParam(value = "year", required = false) Short year,
                            Model model) {
        List<Bill> bills = billService.findAll();

        // Check and update overdue status dynamically based on DueDate
        LocalDateTime now = LocalDateTime.now();
        for (Bill b : bills) {
            if (b.getStatus() == 0 && b.getDueDate().isBefore(now)) {
                b.setStatus((byte) 2); // Mark as Overdue
                billService.save(b);
            }
        }

        // Apply filters
        if (search != null && !search.trim().isEmpty()) {
            String query = search.toLowerCase().trim();
            bills = bills.stream().filter(b ->
                b.getApartment().getApartmentNumber().toLowerCase().contains(query) ||
                getOwnerName(b.getApartment()).toLowerCase().contains(query)
            ).collect(Collectors.toList());
        }

        if (status != null) {
            bills = bills.stream().filter(b -> b.getStatus().equals(status)).collect(Collectors.toList());
        }

        if (month != null) {
            bills = bills.stream().filter(b -> b.getBillMonth().equals(month)).collect(Collectors.toList());
        }

        if (year != null) {
            bills = bills.stream().filter(b -> b.getBillYear().equals(year)).collect(Collectors.toList());
        }

        model.addAttribute("bills", bills);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("month", month);
        model.addAttribute("year", year);
        model.addAttribute("activeTab", "bills");

        return "admin/list_bills";
    }

    // 2. View Payment Status & Monitor Dashboard
    @GetMapping("/payment-status")
    public String paymentStatus(@RequestParam(value = "search", required = false) String search,
                                @RequestParam(value = "status", required = false) Byte status,
                                Model model) {
        List<Bill> bills = billService.findAll();
        
        // Update overdue status dynamically
        LocalDateTime now = LocalDateTime.now();
        for (Bill b : bills) {
            if (b.getStatus() == 0 && b.getDueDate().isBefore(now)) {
                b.setStatus((byte) 2); // Overdue
                billService.save(b);
            }
        }

        // Statistics for cards
        long unpaidCount = bills.stream().filter(b -> b.getStatus() == 0).count();
        long paidCount = bills.stream().filter(b -> b.getStatus() == 1).count();
        long overdueCount = bills.stream().filter(b -> b.getStatus() == 2).count();
        BigDecimal totalReceivable = bills.stream()
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPaid = bills.stream()
                .filter(b -> b.getStatus() == 1)
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalOverdue = bills.stream()
                .filter(b -> b.getStatus() == 2)
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Apply filters
        if (search != null && !search.trim().isEmpty()) {
            String query = search.toLowerCase().trim();
            bills = bills.stream().filter(b ->
                b.getApartment().getApartmentNumber().toLowerCase().contains(query) ||
                getOwnerName(b.getApartment()).toLowerCase().contains(query)
            ).collect(Collectors.toList());
        }

        if (status != null) {
            bills = bills.stream().filter(b -> b.getStatus().equals(status)).collect(Collectors.toList());
        }

        model.addAttribute("bills", bills);
        model.addAttribute("unpaidCount", unpaidCount);
        model.addAttribute("paidCount", paidCount);
        model.addAttribute("overdueCount", overdueCount);
        model.addAttribute("totalReceivable", totalReceivable);
        model.addAttribute("totalPaid", totalPaid);
        model.addAttribute("totalOverdue", totalOverdue);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("activeTab", "payment-status");

        return "admin/payment_status";
    }

    // 3. Show Generate Bill Form
    @GetMapping("/generate")
    public String showGenerateForm(Model model) {
        List<Apartment> apartments = apartmentService.findAll().stream()
                .filter(a -> a.getStatus() == 1) // Only occupied apartments can be billed
                .collect(Collectors.toList());
        
        List<ServiceItem> services = serviceItemService.findAll().stream()
                .filter(s -> s.getStatus() && !"Utility Booking".equals(s.getServiceName()))
                .collect(Collectors.toList());

        model.addAttribute("apartments", apartments);
        model.addAttribute("services", services);
        model.addAttribute("billDto", new BillDTO());
        model.addAttribute("activeTab", "bills");
        return "admin/form_bill";
    }

    // 4. Retrieve utility bookings and pending billing items for an apartment via AJAX (helper)
    @GetMapping("/generate/preview")
    @ResponseBody
    public List<String> previewUtilityBookings(@RequestParam("apartmentId") Integer apartmentId,
                                               @RequestParam("month") Byte month,
                                               @RequestParam("year") Short year) {
        List<String> bookingDetails = new ArrayList<>();
        Optional<Apartment> aptOpt = apartmentService.findById(apartmentId);
        if (aptOpt.isPresent()) {
            Apartment apartment = aptOpt.get();
            List<UtilityBooking> bookings = getMonthBookings(apartment, month, year);
            for (UtilityBooking b : bookings) {
                bookingDetails.add(b.getResource().getUtility().getUtilityName() + 
                                   " (" + b.getStartTime().toLocalDate().toString() + "): $" + b.getTotalPrice());
            }
        }
        return bookingDetails;
    }

    // 5. Handle Bill Generation Post Action
    @PostMapping("/generate")
    public String generateBill(@Valid @ModelAttribute("billDto") BillDTO billDto,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            List<Apartment> apartments = apartmentService.findAll().stream()
                    .filter(a -> a.getStatus() == 1)
                    .collect(Collectors.toList());
            
            List<ServiceItem> services = serviceItemService.findAll().stream()
                    .filter(s -> s.getStatus() && !"Utility Booking".equals(s.getServiceName()))
                    .collect(Collectors.toList());

            model.addAttribute("apartments", apartments);
            model.addAttribute("services", services);
            model.addAttribute("activeTab", "bills");
            return "admin/form_bill";
        }

        Integer apartmentId = billDto.getApartmentId();
        Byte month = billDto.getMonth();
        Short year = billDto.getYear();
        List<Integer> serviceIds = billDto.getServiceIds();
        List<BigDecimal> quantities = billDto.getQuantities();
        List<String> descriptions = billDto.getDescriptions();

        Optional<Apartment> aptOpt = apartmentService.findById(apartmentId);
        if (aptOpt.isEmpty()) {
            bindingResult.rejectValue("apartmentId", "error.billDto", "Selected apartment not found.");
            
            List<Apartment> apartments = apartmentService.findAll().stream()
                    .filter(a -> a.getStatus() == 1)
                    .collect(Collectors.toList());
            
            List<ServiceItem> services = serviceItemService.findAll().stream()
                    .filter(s -> s.getStatus() && !"Utility Booking".equals(s.getServiceName()))
                    .collect(Collectors.toList());

            model.addAttribute("apartments", apartments);
            model.addAttribute("services", services);
            model.addAttribute("activeTab", "bills");
            return "admin/form_bill";
        }

        Apartment apartment = aptOpt.get();

        // Check duplicate
        List<Bill> existing = billService.findByYearAndMonth(year, month).stream()
                .filter(b -> b.getApartment().getApartmentId().equals(apartmentId))
                .collect(Collectors.toList());

        if (!existing.isEmpty()) {
            bindingResult.rejectValue("month", "error.billDto", "Bill already exists for Apartment " + apartment.getApartmentNumber() + " for " + month + "/" + year);
            
            List<Apartment> apartments = apartmentService.findAll().stream()
                    .filter(a -> a.getStatus() == 1)
                    .collect(Collectors.toList());
            
            List<ServiceItem> services = serviceItemService.findAll().stream()
                    .filter(s -> s.getStatus() && !"Utility Booking".equals(s.getServiceName()))
                    .collect(Collectors.toList());

            model.addAttribute("apartments", apartments);
            model.addAttribute("services", services);
            model.addAttribute("activeTab", "bills");
            return "admin/form_bill";
        }

        // Fetch Manager/Admin account to set as creator
        Account creator = accountService.findByUsername("manager").orElseGet(() ->
                accountService.findByUsername("admin").orElse(null)
        );

        if (creator == null) {
            redirectAttributes.addFlashAttribute("message", "No manager or admin account found to sign the bill.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/admin/bills/generate";
        }

        // Create the Bill
        Bill bill = new Bill();
        bill.setApartment(apartment);
        bill.setCreatedBy(creator);
        bill.setBillMonth(month);
        bill.setBillYear(year);
        bill.setStatus((byte) 0); // UNPAID
        bill.setDueDate(LocalDateTime.now().plusDays(15));
        bill.setCreatedDate(LocalDateTime.now());
        bill = billService.save(bill);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Process standard service inputs
        if (serviceIds != null) {
            for (int i = 0; i < serviceIds.size(); i++) {
                Integer serviceId = serviceIds.get(i);
                BigDecimal qty = (quantities != null && quantities.size() > i) ? quantities.get(i) : BigDecimal.ZERO;
                String desc = (descriptions != null && descriptions.size() > i) ? descriptions.get(i) : "";

                if (qty != null && qty.compareTo(BigDecimal.ZERO) > 0) {
                    Optional<ServiceItem> serviceOpt = serviceItemService.findById(serviceId);
                    if (serviceOpt.isPresent()) {
                        ServiceItem item = serviceOpt.get();
                        BigDecimal lineAmount = qty.multiply(item.getUnitPrice());

                        BillDetail detail = new BillDetail();
                        detail.setBill(bill);
                        detail.setServiceItem(item);
                        detail.setQuantity(qty);
                        detail.setDescription(desc != null && !desc.trim().isEmpty() ? desc : item.getServiceName());
                        detail.setAmount(lineAmount);
                        billDetailService.save(detail);

                        totalAmount = totalAmount.add(lineAmount);
                    }
                }
            }
        }

        // Fetch utility bookings & process as details
        List<UtilityBooking> monthBookings = getMonthBookings(apartment, month, year);
        Optional<ServiceItem> utilityServiceOpt = serviceItemService.findByServiceName("Utility Booking");
        if (utilityServiceOpt.isPresent() && !monthBookings.isEmpty()) {
            ServiceItem bookingService = utilityServiceOpt.get();
            for (UtilityBooking booking : monthBookings) {
                BillDetail detail = new BillDetail();
                detail.setBill(bill);
                detail.setServiceItem(bookingService);
                detail.setBooking(booking);
                detail.setQuantity(BigDecimal.ONE);
                detail.setDescription("Booking: " + booking.getResource().getUtility().getUtilityName() + 
                                       " (" + booking.getStartTime().toLocalDate().toString() + ")");
                detail.setAmount(booking.getTotalPrice());
                billDetailService.save(detail);

                totalAmount = totalAmount.add(booking.getTotalPrice());
            }
        }

        // Update Total Amount on the Bill
        bill.setTotalAmount(totalAmount);
        billService.save(bill);

        redirectAttributes.addFlashAttribute("message", "Bill generated successfully for Apartment " + apartment.getApartmentNumber() + " ($" + totalAmount + ")");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/admin/bills";
    }

    // 6. View Bill Detail Modal / Drawer
    @GetMapping("/detail/{id}")
    public String showBillDetail(@PathVariable("id") Integer id, Model model) {
        Optional<Bill> billOpt = billService.findById(id);
        if (billOpt.isEmpty()) {
            return "redirect:/admin/bills";
        }
        Bill bill = billOpt.get();
        model.addAttribute("bill", bill);
        model.addAttribute("ownerName", getOwnerName(bill.getApartment()));
        model.addAttribute("activeTab", "bills");
        return "admin/detail_bill";
    }

    // 7. Manually Toggle Payment Status (Paid / Unpaid)
    @PostMapping("/{id}/toggle-payment")
    public String togglePayment(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        Optional<Bill> billOpt = billService.findById(id);
        if (billOpt.isPresent()) {
            Bill bill = billOpt.get();
            if (bill.getStatus() == 1) {
                // Revert to Unpaid
                bill.setStatus((byte) 0);
                bill.setPaidDate(null);
                billService.save(bill);

                // Delete associated successful payments
                List<Payment> payments = paymentService.findByBillId(bill.getBillId());
                for (Payment p : payments) {
                    paymentService.deleteById(p.getPaymentId());
                }

                redirectAttributes.addFlashAttribute("message", "Bill status reverted to UNPAID for Apartment " + bill.getApartment().getApartmentNumber());
                redirectAttributes.addFlashAttribute("messageType", "info");
            } else {
                // Mark as Paid
                bill.setStatus((byte) 1);
                bill.setPaidDate(LocalDateTime.now());
                billService.save(bill);

                // Create a Payment record
                PaymentMethod cashMethod = paymentMethodRepository.findByMethodName("Cash").orElse(null);
                Account managerAcc = accountService.findByUsername("manager").orElseGet(() ->
                        accountService.findByUsername("admin").orElse(null)
                );

                if (managerAcc != null && cashMethod != null) {
                    Payment payment = new Payment();
                    payment.setBill(bill);
                    payment.setPaidBy(bill.getApartment().getProfiles().isEmpty() ? managerAcc : bill.getApartment().getProfiles().get(0).getAccount());
                    payment.setPaymentMethod(cashMethod);
                    payment.setAmount(bill.getTotalAmount());
                    payment.setPaymentDate(LocalDateTime.now());
                    payment.setStatus((byte) 1); // SUCCESS
                    payment.setTransactionCode("MANUAL-" + System.currentTimeMillis());
                    paymentService.save(payment);
                }

                redirectAttributes.addFlashAttribute("message", "Bill marked as PAID for Apartment " + bill.getApartment().getApartmentNumber());
                redirectAttributes.addFlashAttribute("messageType", "success");
            }
        }
        return "redirect:/admin/bills";
    }

    // Helper: Find approved bookings for an apartment's residents
    private List<UtilityBooking> getMonthBookings(Apartment apartment, Byte month, Short year) {
        List<UtilityBooking> monthBookings = new ArrayList<>();
        List<Profile> residents = apartment.getProfiles();
        for (Profile p : residents) {
            List<UtilityBooking> bookings = utilityBookingService.findByProfileId(p.getProfileId());
            for (UtilityBooking b : bookings) {
                if (b.getStatus() == 1 && // APPROVED
                    b.getStartTime().getMonthValue() == month &&
                    b.getStartTime().getYear() == year) {
                    monthBookings.add(b);
                }
            }
        }
        return monthBookings;
    }

    // Helper: Get fullName of primary owner or first profile
    public String getOwnerName(Apartment apt) {
        if (apt == null || apt.getProfiles() == null) return "N/A";
        return apt.getProfiles().stream()
                .filter(p -> p.getIsHouseholdOwner() != null && p.getIsHouseholdOwner())
                .map(Profile::getFullName)
                .findFirst()
                .orElse(apt.getProfiles().isEmpty() ? "N/A" : apt.getProfiles().get(0).getFullName());
    }
}
