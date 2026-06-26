package com.quan.apartment_building_management_system.controller;

import com.quan.apartment_building_management_system.entity.Unit;
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
import java.util.Optional;

@Controller
@RequestMapping("/admin/utilities")
public class AdminUtilityController {

    private final UtilityService utilityService;
    private final UtilityResourceService utilityResourceService;
    private final UtilityPriceService utilityPriceService;
    private final UnitService unitService;

    public AdminUtilityController(UtilityService utilityService,
                                  UtilityResourceService utilityResourceService,
                                  UtilityPriceService utilityPriceService,
                                  UnitService unitService) {
        this.utilityService = utilityService;
        this.utilityResourceService = utilityResourceService;
        this.utilityPriceService = utilityPriceService;
        this.unitService = unitService;
    }

    @GetMapping
    public String listUtilities(@RequestParam(value = "query", required = false) String query,
                                @RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "size", defaultValue = "5") int size,
                                Model model) {
        List<Utility> allUtilities = utilityService.searchUtilities(query);

        // Perform simple custom pagination
        int totalItems = allUtilities.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        if (totalPages == 0) {
            totalPages = 1;
        }
        if (page < 1) {
            page = 1;
        } else if (page > totalPages) {
            page = totalPages;
        }

        int startIdx = (page - 1) * size;
        int endIdx = Math.min(startIdx + size, totalItems);
        List<Utility> paginatedUtilities = allUtilities.subList(startIdx, endIdx);

        List<Utility> fullUtilities = utilityService.findAll();
        long totalUtilities = fullUtilities.size();
        long activeUtilities = fullUtilities.stream().filter(Utility::getStatus).count();
        long totalResources = utilityResourceService.findAll().size();
        long totalPricing = utilityPriceService.findAll().size();

        model.addAttribute("utilities", paginatedUtilities);
        model.addAttribute("query", query);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("allUnits", unitService.findAll());
        model.addAttribute("allUtilities", fullUtilities);
        model.addAttribute("utilityPrices", utilityPriceService.findAll());
        model.addAttribute("newUtility", new Utility());
        model.addAttribute("totalUtilities", totalUtilities);
        model.addAttribute("activeUtilities", activeUtilities);
        model.addAttribute("totalResources", totalResources);
        model.addAttribute("totalPricing", totalPricing);

        return "admin/utilities";
    }

    @PostMapping("/save")
    public String saveUtility(@ModelAttribute("newUtility") Utility utility, RedirectAttributes redirectAttributes) {
        if (utility.getUtilityId() != null) {
            // Edit mode
            utilityService.findById(utility.getUtilityId()).ifPresent(existing -> {
                existing.setUtilityName(utility.getUtilityName());
                existing.setDescription(utility.getDescription());
                existing.setStatus(utility.getStatus());
                utilityService.save(existing);
                redirectAttributes.addFlashAttribute("message", "Utility updated successfully!");
                redirectAttributes.addFlashAttribute("messageType", "success");
            });
        } else {
            // Add mode
            utility.setStatus(true); // default active
            utilityService.save(utility);
            redirectAttributes.addFlashAttribute("message", "New utility added successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        }
        return "redirect:/admin/utilities";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        utilityService.findById(id).ifPresent(u -> {
            u.setStatus(!u.getStatus());
            utilityService.save(u);
            redirectAttributes.addFlashAttribute("message", "Utility status updated successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        });
        return "redirect:/admin/utilities";
    }

    @PostMapping("/add-resource")
    public String addResource(@RequestParam("utilityId") Integer utilityId,
                              @RequestParam("resourceName") String resourceName,
                              @RequestParam("resourceLocation") String location,
                              @RequestParam(value = "status", defaultValue = "true") Boolean status,
                              RedirectAttributes redirectAttributes) {
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
        return "redirect:/admin/utilities";
    }

    @PostMapping("/manage-pricing")
    public String managePricing(@RequestParam("utilityId") Integer utilityId,
                                @RequestParam("unitId") Integer unitId,
                                @RequestParam("price") BigDecimal price,
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
        return "redirect:/admin/utilities";
    }

    @PostMapping("/delete-pricing/{id}")
    public String deletePricing(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        utilityPriceService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Pricing configuration deleted successfully!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/admin/utilities";
    }

    @PostMapping("/resources/toggle-status/{id}")
    public String toggleResourceStatus(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        utilityResourceService.findById(id).ifPresent(r -> {
            r.setStatus(!r.getStatus());
            utilityResourceService.save(r);
            redirectAttributes.addFlashAttribute("message", "Resource status updated successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        });
        return "redirect:/admin/utilities";
    }
}
