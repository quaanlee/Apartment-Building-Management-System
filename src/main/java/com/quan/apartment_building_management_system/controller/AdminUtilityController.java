package com.quan.apartment_building_management_system.controller;

import com.quan.apartment_building_management_system.dto.admin.AdminUtilityDTOHandler;
import com.quan.apartment_building_management_system.dto.admin.UtilityDTO;
import com.quan.apartment_building_management_system.entity.Utility;
import com.quan.apartment_building_management_system.entity.UtilityPrice;
import com.quan.apartment_building_management_system.entity.UtilityResource;
import com.quan.apartment_building_management_system.service.utility.UnitService;
import com.quan.apartment_building_management_system.service.utility.UtilityPriceService;
import com.quan.apartment_building_management_system.service.utility.UtilityResourceService;
import com.quan.apartment_building_management_system.service.utility.UtilityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/utilities")
public class AdminUtilityController {

    private final UtilityService utilityService;
    private final UtilityResourceService utilityResourceService;
    private final UtilityPriceService utilityPriceService;
    private final UnitService unitService;
    private final AdminUtilityDTOHandler dtoHandler;

    public AdminUtilityController(UtilityService utilityService,
                                  UtilityResourceService utilityResourceService,
                                  UtilityPriceService utilityPriceService,
                                  UnitService unitService,
                                  AdminUtilityDTOHandler dtoHandler) {
        this.utilityService = utilityService;
        this.utilityResourceService = utilityResourceService;
        this.utilityPriceService = utilityPriceService;
        this.unitService = unitService;
        this.dtoHandler = dtoHandler;
    }

    @GetMapping
    public String listUtilities(@RequestParam(value = "query", required = false) String query,
                                @RequestParam(value = "status", required = false) String status,
                                @RequestParam(value = "statusFilter", required = false) String statusFilter,
                                @RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "size", defaultValue = "5") int size,
                                @RequestParam(value = "pricePage", defaultValue = "1") int pricePage,
                                @RequestParam(value = "priceQuery", required = false) String priceQuery,
                                Model model) {
        List<Utility> allUtilities = utilityService.searchUtilities(query);

        String activeStatus = "all";
        if (statusFilter != null && !statusFilter.isEmpty()) {
            activeStatus = statusFilter;
        } else if (status != null && !status.isEmpty()) {
            activeStatus = status;
        }

        // Apply status filter
        if ("active".equalsIgnoreCase(activeStatus)) {
            allUtilities = allUtilities.stream().filter(Utility::getStatus).toList();
        } else if ("maintenance".equalsIgnoreCase(activeStatus)) {
            allUtilities = allUtilities.stream().filter(u -> !u.getStatus()).toList();
        }

        int totalItems = allUtilities.size();
        int totalPages = dtoHandler.calculateTotalPages(totalItems, size);
        int validPage = dtoHandler.validatePage(page, totalPages);

        List<Utility> paginatedEntities = dtoHandler.getPaginatedList(allUtilities, validPage, size);
        List<UtilityDTO> paginatedUtilities = dtoHandler.toUtilityDTOList(paginatedEntities, true);

        List<Utility> fullUtilities = utilityService.findAll();
        long totalUtilities = fullUtilities.size();
        long activeUtilities = dtoHandler.countActiveUtilities(fullUtilities);
        long totalResources = utilityResourceService.findAll().size();

        // Paginate Pricing Configurations (5 items per page) with search
        List<UtilityPrice> allPrices;
        if (priceQuery != null && !priceQuery.trim().isEmpty()) {
            String pq = priceQuery.trim().toLowerCase();
            allPrices = utilityPriceService.findAll().stream()
                    .filter(price -> (price.getUtility() != null && price.getUtility().getUtilityName() != null && price.getUtility().getUtilityName().toLowerCase().contains(pq)) ||
                                     (price.getUnit() != null && price.getUnit().getUnitName() != null && price.getUnit().getUnitName().toLowerCase().contains(pq)))
                    .toList();
        } else {
            allPrices = utilityPriceService.findAll();
        }

        long totalPricing = allPrices.size();
        int totalPricePages = dtoHandler.calculateTotalPages((int) totalPricing, 5);
        int validPricePage = dtoHandler.validatePage(pricePage, totalPricePages);
        List<UtilityPrice> paginatedPrices = dtoHandler.getPaginatedList(allPrices, validPricePage, 5);
        List<UtilityDTO.Price> paginatedPricesDTO = dtoHandler.toUtilityPriceDTOList(paginatedPrices);

        model.addAttribute("utilities", paginatedUtilities);
        model.addAttribute("query", query);
        model.addAttribute("statusFilter", activeStatus);
        model.addAttribute("priceQuery", priceQuery);
        model.addAttribute("currentPage", validPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("allUnits", dtoHandler.toUnitDTOList(unitService.findAll()));
        model.addAttribute("allUtilities", dtoHandler.toUtilityDTOList(fullUtilities, false));
        model.addAttribute("utilityPrices", paginatedPricesDTO);
        model.addAttribute("currentPricePage", validPricePage);
        model.addAttribute("totalPricePages", totalPricePages);
        model.addAttribute("newUtility", new UtilityDTO());
        model.addAttribute("totalUtilities", totalUtilities);
        model.addAttribute("activeUtilities", activeUtilities);
        model.addAttribute("totalResources", totalResources);
        model.addAttribute("totalPricing", totalPricing);

        return "admin/utilities";
    }

    @GetMapping("/{id}")
    public String viewUtilityDetails(@PathVariable("id") Integer id,
                                     @RequestParam(value = "query", required = false) String query,
                                     @RequestParam(value = "status", required = false) String status,
                                     @RequestParam(value = "statusFilter", required = false) String statusFilter,
                                     @RequestParam(value = "page", defaultValue = "1") int page,
                                     @RequestParam(value = "pricePage", defaultValue = "1") int pricePage,
                                     @RequestParam(value = "priceQuery", required = false) String priceQuery,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        Utility utility = utilityService.findById(id).orElse(null);
        if (utility == null) {
            redirectAttributes.addFlashAttribute("message", "Utility not found.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/admin/utilities";
        }
        UtilityDTO dto = dtoHandler.toUtilityDTO(utility, true);
        model.addAttribute("utility", dto);
        model.addAttribute("query", query);

        String activeStatus = "all";
        if (statusFilter != null && !statusFilter.isEmpty()) {
            activeStatus = statusFilter;
        } else if (status != null && !status.isEmpty()) {
            activeStatus = status;
        }
        model.addAttribute("statusFilter", activeStatus);
        model.addAttribute("currentPage", page);
        model.addAttribute("currentPricePage", pricePage);
        model.addAttribute("priceQuery", priceQuery);
        return "admin/utility_detail";
    }

    @PostMapping("/save")
    public String saveUtility(@ModelAttribute("newUtility") UtilityDTO utilityDTO,
                              @RequestParam(value = "query", required = false) String query,
                              @RequestParam(value = "statusFilter", required = false) String statusFilter,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "pricePage", defaultValue = "1") int pricePage,
                              @RequestParam(value = "priceQuery", required = false) String priceQuery,
                              RedirectAttributes redirectAttributes) {
        if (utilityDTO.getUtilityName() == null || utilityDTO.getUtilityName().trim().isEmpty() || utilityDTO.getUtilityName().trim().length() > 100) {
            redirectAttributes.addFlashAttribute("message", "Utility name must not be empty and must be under 100 characters.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addAttribute("query", query);
            redirectAttributes.addAttribute("statusFilter", statusFilter);
            redirectAttributes.addAttribute("page", page);
            redirectAttributes.addAttribute("pricePage", pricePage);
            redirectAttributes.addAttribute("priceQuery", priceQuery);
            return "redirect:/admin/utilities";
        }
        if (utilityDTO.getDescription() == null || utilityDTO.getDescription().trim().isEmpty() || utilityDTO.getDescription().trim().length() > 100) {
            redirectAttributes.addFlashAttribute("message", "Description must not be empty and must be under 100 characters.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addAttribute("query", query);
            redirectAttributes.addAttribute("statusFilter", statusFilter);
            redirectAttributes.addAttribute("page", page);
            redirectAttributes.addAttribute("pricePage", pricePage);
            redirectAttributes.addAttribute("priceQuery", priceQuery);
            return "redirect:/admin/utilities";
        }

        if (utilityDTO.getUtilityId() != null) {
            // Edit mode
            utilityService.findById(utilityDTO.getUtilityId()).ifPresent(existing -> {
                dtoHandler.updateEntityFromDTO(utilityDTO, existing);
                utilityService.save(existing);
                redirectAttributes.addFlashAttribute("message", "Utility updated successfully!");
                redirectAttributes.addFlashAttribute("messageType", "success");
            });
        } else {
            // Add mode
            Utility utility = dtoHandler.toEntity(utilityDTO);
            utilityService.save(utility);
            redirectAttributes.addFlashAttribute("message", "New utility added successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        }
        redirectAttributes.addAttribute("query", query);
        redirectAttributes.addAttribute("statusFilter", statusFilter);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("pricePage", pricePage);
        redirectAttributes.addAttribute("priceQuery", priceQuery);
        return "redirect:/admin/utilities";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable("id") Integer id,
                               @RequestParam(value = "query", required = false) String query,
                               @RequestParam(value = "statusFilter", required = false) String statusFilter,
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               @RequestParam(value = "pricePage", defaultValue = "1") int pricePage,
                               @RequestParam(value = "priceQuery", required = false) String priceQuery,
                               RedirectAttributes redirectAttributes) {
        utilityService.findById(id).ifPresent(u -> {
            u.setStatus(!u.getStatus());
            utilityService.save(u);
            redirectAttributes.addFlashAttribute("message", "Utility status updated successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        });
        redirectAttributes.addAttribute("query", query);
        redirectAttributes.addAttribute("statusFilter", statusFilter);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("pricePage", pricePage);
        redirectAttributes.addAttribute("priceQuery", priceQuery);
        return "redirect:/admin/utilities";
    }

    @PostMapping("/add-resource")
    public String addResource(@RequestParam("utilityId") Integer utilityId,
                              @RequestParam("resourceName") String resourceName,
                              @RequestParam("resourceLocation") String location,
                              @RequestParam(value = "status", defaultValue = "true") Boolean status,
                              @RequestParam(value = "query", required = false) String query,
                              @RequestParam(value = "statusFilter", required = false) String statusFilter,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "pricePage", defaultValue = "1") int pricePage,
                              @RequestParam(value = "priceQuery", required = false) String priceQuery,
                              RedirectAttributes redirectAttributes) {
        if (resourceName == null || resourceName.trim().isEmpty() || resourceName.trim().length() > 100) {
            redirectAttributes.addFlashAttribute("message", "Resource name must not be empty and must be under 100 characters.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addAttribute("query", query);
            redirectAttributes.addAttribute("statusFilter", statusFilter);
            redirectAttributes.addAttribute("page", page);
            redirectAttributes.addAttribute("pricePage", pricePage);
            redirectAttributes.addAttribute("priceQuery", priceQuery);
            return "redirect:/admin/utilities";
        }
        if (location == null || location.trim().isEmpty() || location.trim().length() > 100) {
            redirectAttributes.addFlashAttribute("message", "Resource location must not be empty and must be under 100 characters.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            redirectAttributes.addAttribute("query", query);
            redirectAttributes.addAttribute("statusFilter", statusFilter);
            redirectAttributes.addAttribute("page", page);
            redirectAttributes.addAttribute("pricePage", pricePage);
            redirectAttributes.addAttribute("priceQuery", priceQuery);
            return "redirect:/admin/utilities";
        }

        utilityService.findById(utilityId).ifPresent(utility -> {
            UtilityResource resource = new UtilityResource();
            resource.setUtility(utility);
            resource.setResourceName(resourceName);
            resource.setLocation(location);
            resource.setStatus(status);
            utilityResourceService.save(resource);
            redirectAttributes.addFlashAttribute("message", "New resource added successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        });
        redirectAttributes.addAttribute("query", query);
        redirectAttributes.addAttribute("statusFilter", statusFilter);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("pricePage", pricePage);
        redirectAttributes.addAttribute("priceQuery", priceQuery);
        return "redirect:/admin/utilities";
    }

    @PostMapping("/manage-pricing")
    public String managePricing(@RequestParam("utilityId") Integer utilityId,
                                @RequestParam("unitId") Integer unitId,
                                @RequestParam("price") BigDecimal price,
                                @RequestParam(value = "query", required = false) String query,
                                @RequestParam(value = "statusFilter", required = false) String statusFilter,
                                @RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "pricePage", defaultValue = "1") int pricePage,
                                @RequestParam(value = "priceQuery", required = false) String priceQuery,
                                RedirectAttributes redirectAttributes) {
        utilityService.findById(utilityId).ifPresent(utility -> {
            unitService.findById(unitId).ifPresent(unit -> {
                UtilityPrice targetPrice = utilityPriceService.findByUtilityIdAndUnitId(utilityId, unitId)
                        .orElseGet(() -> {
                            UtilityPrice newPrice = new UtilityPrice();
                            newPrice.setUtility(utility);
                            newPrice.setUnit(unit);
                            return newPrice;
                        });
                targetPrice.setPrice(price);
                utilityPriceService.save(targetPrice);
                redirectAttributes.addFlashAttribute("message", "Pricing configuration saved successfully!");
                redirectAttributes.addFlashAttribute("messageType", "success");
            });
        });
        redirectAttributes.addAttribute("query", query);
        redirectAttributes.addAttribute("statusFilter", statusFilter);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("pricePage", pricePage);
        redirectAttributes.addAttribute("priceQuery", priceQuery);
        return "redirect:/admin/utilities";
    }

    @PostMapping("/delete-pricing/{id}")
    public String deletePricing(@PathVariable("id") Integer id,
                                @RequestParam(value = "query", required = false) String query,
                                @RequestParam(value = "statusFilter", required = false) String statusFilter,
                                @RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "pricePage", defaultValue = "1") int pricePage,
                                @RequestParam(value = "priceQuery", required = false) String priceQuery,
                                RedirectAttributes redirectAttributes) {
        utilityPriceService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Pricing configuration deleted successfully!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        redirectAttributes.addAttribute("query", query);
        redirectAttributes.addAttribute("statusFilter", statusFilter);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("pricePage", pricePage);
        redirectAttributes.addAttribute("priceQuery", priceQuery);
        return "redirect:/admin/utilities";
    }

    @PostMapping("/resources/toggle-status/{id}")
    public String toggleResourceStatus(@PathVariable("id") Integer id,
                                       @RequestParam(value = "query", required = false) String query,
                                       @RequestParam(value = "statusFilter", required = false) String statusFilter,
                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                       @RequestParam(value = "pricePage", defaultValue = "1") int pricePage,
                                       @RequestParam(value = "priceQuery", required = false) String priceQuery,
                                       RedirectAttributes redirectAttributes) {
        final Integer[] utilityIdHolder = new Integer[1];
        utilityResourceService.findById(id).ifPresent(r -> {
            r.setStatus(!r.getStatus());
            utilityResourceService.save(r);
            if (r.getUtility() != null) {
                utilityIdHolder[0] = r.getUtility().getUtilityId();
            }
            redirectAttributes.addFlashAttribute("message", "Resource status updated successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        });
        redirectAttributes.addAttribute("query", query);
        redirectAttributes.addAttribute("statusFilter", statusFilter);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("pricePage", pricePage);
        redirectAttributes.addAttribute("priceQuery", priceQuery);
        if (utilityIdHolder[0] != null) {
            return "redirect:/admin/utilities/" + utilityIdHolder[0];
        }
        return "redirect:/admin/utilities";
    }
}
