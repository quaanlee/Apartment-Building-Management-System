package com.quan.apartment_building_management_system.controller.resident;

import com.quan.apartment_building_management_system.dto.service.ServiceDTO;
import com.quan.apartment_building_management_system.entity.ServiceItem;
import com.quan.apartment_building_management_system.service.utility.ServiceItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String viewServices(Model model) {
        List<ServiceItem> services = serviceItemService.findAll();

        long totalServices = services.size();
        long activeServices = services.stream().filter(ServiceItem::getStatus).count();

        List<ServiceDTO> dtos = services.stream()
                .map(ServiceDTO::new)
                .collect(Collectors.toList());

        model.addAttribute("services", dtos);
        model.addAttribute("totalServices", totalServices);
        model.addAttribute("activeServices", activeServices);

        return "resident/services/list";
    }
}
