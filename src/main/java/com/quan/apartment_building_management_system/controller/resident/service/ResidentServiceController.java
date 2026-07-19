package com.quan.apartment_building_management_system.controller.resident.service;

import com.quan.apartment_building_management_system.dto.service.ServiceDTO;
import com.quan.apartment_building_management_system.entity.ServiceItem;
import com.quan.apartment_building_management_system.service.utility.ServiceItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/resident/services")
public class ResidentServiceController {

    private final ServiceItemService serviceItemService;

    public ResidentServiceController(ServiceItemService serviceItemService) {
        this.serviceItemService = serviceItemService;
    }

    @GetMapping
    public String viewServices(@RequestParam(required = false) String search, Model model) {
        List<ServiceItem> services;
        if (search != null && !search.trim().isEmpty()) {
            services = serviceItemService.searchServices(search, null);
        } else {
            services = serviceItemService.findAll();
        }

        long totalServices = services.size();
        long activeServices = services.stream().filter(ServiceItem::getStatus).count();

        List<ServiceDTO> dtos = services.stream()
                .map(ServiceDTO::new)
                .collect(Collectors.toList());

        model.addAttribute("services", dtos);
        model.addAttribute("totalServices", totalServices);
        model.addAttribute("activeServices", activeServices);
        model.addAttribute("search", search);

        return "resident/services/list";
    }
}
