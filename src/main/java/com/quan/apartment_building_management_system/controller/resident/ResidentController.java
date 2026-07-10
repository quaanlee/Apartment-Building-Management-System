package com.quan.apartment_building_management_system.controller.resident;

import com.quan.apartment_building_management_system.dto.ApartmentDTO;
import com.quan.apartment_building_management_system.dto.ApartmentDetailDTO;
import com.quan.apartment_building_management_system.dto.service.ServiceDTO;
import com.quan.apartment_building_management_system.entity.Account;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.entity.ServiceItem;
import com.quan.apartment_building_management_system.entity.Utility;
import com.quan.apartment_building_management_system.entity.UtilityPrice;
import com.quan.apartment_building_management_system.service.apartment.ApartmentManagerService;
import com.quan.apartment_building_management_system.service.user.ProfileService;
import com.quan.apartment_building_management_system.service.utility.ServiceItemService;
import com.quan.apartment_building_management_system.service.utility.UtilityPriceService;
import com.quan.apartment_building_management_system.service.utility.UtilityService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/resident")
public class ResidentController {

    private final ApartmentManagerService apartmentManagerService;
    private final ProfileService profileService;
    private final ServiceItemService serviceItemService;
    private final UtilityService utilityService;
    private final UtilityPriceService utilityPriceService;

    public ResidentController(ApartmentManagerService apartmentManagerService,
                              ProfileService profileService,
                              ServiceItemService serviceItemService,
                              UtilityService utilityService,
                              UtilityPriceService utilityPriceService) {
        this.apartmentManagerService = apartmentManagerService;
        this.profileService = profileService;
        this.serviceItemService = serviceItemService;
        this.utilityService = utilityService;
        this.utilityPriceService = utilityPriceService;
    }

    private boolean isNotResident(HttpSession session) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        return currentUser == null || !"RESIDENT".equalsIgnoreCase(currentUser.getRole().getRoleName());
    }

    @GetMapping("/apartments")
    public String viewApartments(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) Byte floor,
            @RequestParam(required = false) Byte status,
            @RequestParam(required = false) BigDecimal minArea,
            @RequestParam(required = false) BigDecimal maxArea,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "apartmentId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpSession session,
            Model model) {

        if (isNotResident(session)) {
            return "redirect:/login";
        }

        Account currentUser = (Account) session.getAttribute("currentUser");
        Optional<Profile> residentProfileOpt = profileService.findByAccountId(currentUser.getAccountId());
        
        if (residentProfileOpt.isPresent()) {
            Profile profile = residentProfileOpt.get();
            model.addAttribute("myApartment", profile.getApartment());
            model.addAttribute("residentProfile", profile);
        } else {
            model.addAttribute("myApartment", null);
        }

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ApartmentDTO> apartmentPage = apartmentManagerService.getFilteredApartments(
                search.isEmpty() ? null : search,
                roomType,
                floor,
                status,
                minArea,
                maxArea,
                pageable);

        model.addAttribute("apartments", apartmentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", apartmentPage.getTotalPages());
        model.addAttribute("totalItems", apartmentPage.getTotalElements());
        
        model.addAttribute("search", search);
        model.addAttribute("roomType", roomType);
        model.addAttribute("floor", floor);
        model.addAttribute("status", status);
        model.addAttribute("minArea", minArea);
        model.addAttribute("maxArea", maxArea);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("size", size);

        // Header statistics
        model.addAttribute("totalUnits", apartmentManagerService.getTotalApartments());
        model.addAttribute("vacantUnits", apartmentManagerService.countByStatus((byte) 0));
        model.addAttribute("occupiedUnits", apartmentManagerService.countByStatus((byte) 1));
        model.addAttribute("maintenanceUnits", apartmentManagerService.countByStatus((byte) 2));

        model.addAttribute("activeTab", "apartments");
        model.addAttribute("pageTitle", "Apartments Directory");

        return "resident/resident";
    }

    @GetMapping("/apartments/{id}")
    public String viewApartmentDetail(
            @PathVariable Integer id,
            HttpSession session,
            Model model) {

        if (isNotResident(session)) {
            return "redirect:/login";
        }

        ApartmentDetailDTO apartment = apartmentManagerService.getApartmentDetail(id);
        model.addAttribute("apartment", apartment);
        
        model.addAttribute("activeTab", "apartments");
        model.addAttribute("pageTitle", "Apartment Details");

        return "resident/detail";
    }

    @GetMapping("/services")
    public String viewServicesAndUtilities(
            @RequestParam(value = "keyword", required = false) String keyword,
            HttpSession session,
            Model model) {

        if (isNotResident(session)) {
            return "redirect:/login";
        }

        String searchKeyword = (keyword == null) ? "" : keyword.trim();

        // 1. Fetch & map standard services (only active ones for residents)
        List<ServiceItem> services = serviceItemService.searchServices(searchKeyword, true);
        List<ServiceDTO> serviceDtos = services.stream().map(ServiceDTO::new).toList();

        // 2. Fetch facilities (utilities)
        List<Utility> utilities = utilityService.searchUtilities(searchKeyword);

        // 3. Fetch utility prices
        List<UtilityPrice> prices = utilityPriceService.findAll();

        model.addAttribute("services", serviceDtos);
        model.addAttribute("utilities", utilities);
        model.addAttribute("prices", prices);
        model.addAttribute("keyword", searchKeyword);

        model.addAttribute("activeTab", "services & utility");
        model.addAttribute("pageTitle", "Services & Utilities");

        return "resident/services";
    }
}
