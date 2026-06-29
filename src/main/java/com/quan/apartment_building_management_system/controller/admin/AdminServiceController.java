package com.quan.apartment_building_management_system.controller.admin;

import com.quan.apartment_building_management_system.dto.ServiceDTO;
import com.quan.apartment_building_management_system.entity.ServiceItem;
import com.quan.apartment_building_management_system.entity.Unit;
import com.quan.apartment_building_management_system.service.utility.ServiceItemService;
import com.quan.apartment_building_management_system.service.utility.UnitService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/services")
public class AdminServiceController {

    private final ServiceItemService serviceItemService;
    private final UnitService unitService;

    public AdminServiceController(ServiceItemService serviceItemService, UnitService unitService) {
        this.serviceItemService = serviceItemService;
        this.unitService = unitService;
    }

    @GetMapping
    public String listServices(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status",  required = false) Boolean status,
            Model model) {

        String searchKeyword = (keyword == null) ? "" : keyword.trim();

        List<ServiceItem> filteredItems = serviceItemService.searchServices(searchKeyword, status);
        List<ServiceDTO> dtos = filteredItems.stream()
                .map(ServiceDTO::new)
                .toList();

        // Statistics (based on full list)
        List<ServiceItem> allItems  = serviceItemService.findAll();
        long totalServices          = allItems.size();
        long activeServices         = allItems.stream().filter(ServiceItem::getStatus).count();

        // Units list for "Add New Service" modal dropdown
        List<Unit> units = unitService.findAll();

        model.addAttribute("services",      dtos);
        model.addAttribute("totalServices", totalServices);
        model.addAttribute("activeServices",activeServices);
        model.addAttribute("keyword",       searchKeyword);
        model.addAttribute("statusFilter",  status);
        model.addAttribute("units",         units);

        return "admin/services/services";
    }

    @PostMapping("/create")
    public String createService(
            @RequestParam("serviceName")                     String     serviceName,
            @RequestParam("serviceType")                     String     serviceType,
            @RequestParam("unitPrice")                       BigDecimal unitPrice,
            @RequestParam("unitId")                          Integer    unitId,
            @RequestParam(value = "description", required = false) String description) {

        Optional<Unit> unitOpt = unitService.findById(unitId);
        if (unitOpt.isEmpty()) {
            return "redirect:/admin/services?error=unit_not_found";
        }

        ServiceItem newService = new ServiceItem();
        newService.setServiceName(serviceName.trim());
        newService.setServiceType(serviceType.trim().toUpperCase());
        newService.setUnitPrice(unitPrice);
        newService.setUnit(unitOpt.get());
        newService.setDescription(description != null ? description.trim() : "");
        newService.setStatus(true); // máº·c Ä‘á»‹nh ACTIVE khi táº¡o má»›i

        serviceItemService.save(newService);

        return "redirect:/admin/services?success=created";
    }

    @GetMapping("/{serviceId}/details")
    @ResponseBody
    public ResponseEntity<ServiceDTO> getServiceDetails(@PathVariable Integer serviceId) {
        Optional<ServiceItem> serviceOpt = serviceItemService.findById(serviceId);
        if (serviceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ServiceDTO(serviceOpt.get()));
    }

    @PostMapping("/update")
    public String updateService(
            @RequestParam("serviceId")                       Integer    serviceId,
            @RequestParam("serviceName")                     String     serviceName,
            @RequestParam("unitPrice")                       BigDecimal unitPrice,
            @RequestParam("unitId")                          Integer    unitId,
            @RequestParam(value = "description", required = false) String description) {

        Optional<ServiceItem> serviceOpt = serviceItemService.findById(serviceId);
        if (serviceOpt.isEmpty()) {
            return "redirect:/admin/services?error=service_not_found";
        }

        Optional<Unit> unitOpt = unitService.findById(unitId);
        if (unitOpt.isEmpty()) {
            return "redirect:/admin/services?error=unit_not_found";
        }

        ServiceItem service = serviceOpt.get();
        service.setServiceName(serviceName.trim());
        service.setUnitPrice(unitPrice);
        service.setUnit(unitOpt.get());
        service.setDescription(description != null ? description.trim() : "");

        serviceItemService.save(service);

        return "redirect:/admin/services?success=updated";
    }

    @PostMapping("/{serviceId}/toggle-status")
    public String toggleServiceStatus(
            @PathVariable Integer serviceId,
            @RequestParam(value = "lock", required = false) Boolean lock) {

        Optional<ServiceItem> serviceOpt = serviceItemService.findById(serviceId);
        if (serviceOpt.isEmpty()) {
            return "redirect:/admin/services?error=service_not_found";
        }

        ServiceItem service = serviceOpt.get();

        // If lock param is provided, use it; otherwise toggle the current status
        if (lock != null) {
            service.setStatus(!lock); // lock=true means set status=false (locked)
        } else {
            service.setStatus(!service.getStatus()); // toggle current status
        }

        serviceItemService.save(service);

        String action = service.getStatus() ? "unlocked" : "locked";
        return "redirect:/admin/services?success=" + action;
    }
}
