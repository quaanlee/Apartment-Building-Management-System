package com.quan.apartment_building_management_system.controller.manager.service;

import com.quan.apartment_building_management_system.dto.service.ServiceDTO;
import com.quan.apartment_building_management_system.dto.utility.AdminUtilityDTOHandler;
import com.quan.apartment_building_management_system.dto.utility.UtilityDTO;
import com.quan.apartment_building_management_system.entity.ServiceItem;
import com.quan.apartment_building_management_system.entity.Unit;
import com.quan.apartment_building_management_system.entity.Utility;
import com.quan.apartment_building_management_system.entity.UtilityPrice;
import com.quan.apartment_building_management_system.entity.UtilityResource;
import com.quan.apartment_building_management_system.service.utility.ServiceItemService;
import com.quan.apartment_building_management_system.service.utility.UnitService;
import com.quan.apartment_building_management_system.service.utility.UtilityPriceService;
import com.quan.apartment_building_management_system.service.utility.UtilityResourceService;
import com.quan.apartment_building_management_system.service.utility.UtilityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ManagerServiceController {

    private final ServiceItemService serviceItemService;
    private final UnitService unitService;
    private final UtilityService utilityService;
    private final UtilityResourceService utilityResourceService;
    private final UtilityPriceService utilityPriceService;
    private final AdminUtilityDTOHandler dtoHandler;

    public ManagerServiceController(ServiceItemService serviceItemService,
                                    UnitService unitService,
                                    UtilityService utilityService,
                                    UtilityResourceService utilityResourceService,
                                    UtilityPriceService utilityPriceService,
                                    AdminUtilityDTOHandler dtoHandler) {
        this.serviceItemService = serviceItemService;
        this.unitService = unitService;
        this.utilityService = utilityService;
        this.utilityResourceService = utilityResourceService;
        this.utilityPriceService = utilityPriceService;
        this.dtoHandler = dtoHandler;
    }

    @GetMapping("/manager/services")
    public String listServices(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status",  required = false) Boolean status,
            Model model) {

        String searchKeyword = (keyword == null) ? "" : keyword.trim();

        // Filtered list → DTO
        List<ServiceItem> filteredItems = serviceItemService.searchServices(searchKeyword, status);
        List<ServiceDTO> dtos = filteredItems.stream()
                .map(ServiceDTO::new)
                .toList();

        // Statistics (based on full list)
        List<ServiceItem> allItems  = serviceItemService.findAll();
        long totalServices          = allItems.size();
        long activeServices         = allItems.stream().filter(ServiceItem::getStatus).count();

        // Units list for view
        List<Unit> units = unitService.findAll();

        model.addAttribute("services",      dtos);
        model.addAttribute("totalServices", totalServices);
        model.addAttribute("activeServices",activeServices);
        model.addAttribute("keyword",       searchKeyword);
        model.addAttribute("statusFilter",  status);
        model.addAttribute("units",         units);
        model.addAttribute("activeTab",     "service_utility");

        return "manager/services/list";
    }

    @GetMapping("/manager/utilities")
    public String listUtilities(@RequestParam(value = "query", required = false) String query,
                                @RequestParam(value = "status", defaultValue = "all") String status,
                                @RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "size", defaultValue = "5") int size,
                                @RequestParam(value = "pricePage", defaultValue = "1") int pricePage,
                                @RequestParam(value = "priceQuery", required = false) String priceQuery,
                                Model model) {
        List<Utility> allUtilities = utilityService.searchUtilities(query);

        if ("active".equalsIgnoreCase(status)) {
            allUtilities = allUtilities.stream().filter(Utility::getStatus).toList();
        } else if ("maintenance".equalsIgnoreCase(status)) {
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

        List<UtilityPrice> allPrices;
        if (priceQuery != null && !priceQuery.trim().isEmpty()) {
            String pq = priceQuery.trim().toLowerCase();
            allPrices = utilityPriceService.findAll().stream()
                    .filter(price -> (price.getResource() != null
                            && price.getResource().getUtility() != null
                            && price.getResource().getUtility().getUtilityName() != null
                            && price.getResource().getUtility().getUtilityName().toLowerCase().contains(pq))
                            || (price.getUnit() != null
                            && price.getUnit().getUnitName() != null
                            && price.getUnit().getUnitName().toLowerCase().contains(pq)))
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
        model.addAttribute("statusFilter", status);
        model.addAttribute("priceQuery", priceQuery);
        model.addAttribute("currentPage", validPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("allUnits", dtoHandler.toUnitDTOList(unitService.findAll()));
        model.addAttribute("allUtilities", dtoHandler.toUtilityDTOList(fullUtilities, false));
        model.addAttribute("utilityPrices", paginatedPricesDTO);
        model.addAttribute("currentPricePage", validPricePage);
        model.addAttribute("totalPricePages", totalPricePages);
        model.addAttribute("totalUtilities", totalUtilities);
        model.addAttribute("activeUtilities", activeUtilities);
        model.addAttribute("totalResources", totalResources);
        model.addAttribute("totalPricing", totalPricing);
        model.addAttribute("activeTab", "service_utility");

        return "manager/utilities/list";
    }

    @GetMapping("/manager/utilities/{id}")
    public String viewUtilityDetails(@PathVariable("id") Integer id,
                                     @RequestParam(value = "query", required = false) String query,
                                     @RequestParam(value = "status", defaultValue = "all") String status,
                                     @RequestParam(value = "page", defaultValue = "1") int page,
                                     @RequestParam(value = "pricePage", defaultValue = "1") int pricePage,
                                     @RequestParam(value = "priceQuery", required = false) String priceQuery,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        Utility utility = utilityService.findById(id).orElse(null);
        if (utility == null) {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy tiện ích.");
            return "redirect:/manager/utilities";
        }

        UtilityDTO dto = dtoHandler.toUtilityDTO(utility, true);
        model.addAttribute("utility", dto);
        model.addAttribute("query", query);
        model.addAttribute("statusFilter", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("currentPricePage", pricePage);
        model.addAttribute("priceQuery", priceQuery);
        model.addAttribute("activeTab", "service_utility");
        return "manager/utilities/detail";
    }

    @GetMapping("/manager/utilities/resources/{id}")
    public String viewResourceDetails(@PathVariable("id") Integer id,
                                      @RequestParam(value = "query", required = false) String query,
                                      @RequestParam(value = "statusFilter", required = false) String statusFilter,
                                      @RequestParam(value = "page", defaultValue = "1") int page,
                                      @RequestParam(value = "pricePage", defaultValue = "1") int pricePage,
                                      @RequestParam(value = "priceQuery", required = false) String priceQuery,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        UtilityResource resource = utilityResourceService.findById(id).orElse(null);
        if (resource == null) {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy tài nguyên.");
            return "redirect:/manager/utilities";
        }
        UtilityDTO.Resource resourceDTO = dtoHandler.toUtilityResourceDTO(resource);
        model.addAttribute("resource", resourceDTO);
        model.addAttribute("utility", dtoHandler.toUtilityDTO(resource.getUtility(), false));
        model.addAttribute("query", query);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("currentPage", page);
        model.addAttribute("currentPricePage", pricePage);
        model.addAttribute("priceQuery", priceQuery);
        model.addAttribute("activeTab", "service_utility");
        return "manager/utilities/resource_detail";
    }

    @PostMapping("/manager/utilities/resources/toggle-status/{id}")
    public String toggleResourceStatus(@PathVariable("id") Integer id,
                                       @RequestParam(value = "query", required = false) String query,
                                       @RequestParam(value = "status", defaultValue = "all") String status,
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
            redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái tài nguyên thành công!");
        });
        redirectAttributes.addAttribute("query", query);
        redirectAttributes.addAttribute("status", status);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("pricePage", pricePage);
        redirectAttributes.addAttribute("priceQuery", priceQuery);
        if (utilityIdHolder[0] != null) {
            return "redirect:/manager/utilities/" + utilityIdHolder[0];
        }
        return "redirect:/manager/utilities";
    }
}
