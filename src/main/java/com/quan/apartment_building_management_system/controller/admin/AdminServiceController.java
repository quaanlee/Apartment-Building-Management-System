package com.quan.apartment_building_management_system.controller.admin;

import com.quan.apartment_building_management_system.dto.service.ServiceDTO;
import com.quan.apartment_building_management_system.dto.service.ServiceCreateDTO;
import com.quan.apartment_building_management_system.dto.service.ServiceUpdateDTO;
import com.quan.apartment_building_management_system.entity.ServiceItem;
import com.quan.apartment_building_management_system.entity.Unit;
import com.quan.apartment_building_management_system.service.utility.ServiceItemService;
import com.quan.apartment_building_management_system.service.utility.UnitService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/services")
public class AdminServiceController {

    private final ServiceItemService serviceItemService;
    private final UnitService unitService;
    private final com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService;

    public AdminServiceController(ServiceItemService serviceItemService, UnitService unitService, com.quan.apartment_building_management_system.service.system.SystemLogService systemLogService) {
        this.serviceItemService = serviceItemService;
        this.unitService = unitService;
        this.systemLogService = systemLogService;
    }

    // ── GET: Hiển thị danh sách dịch vụ ──────────────────────────────────────
    @GetMapping
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

        // Units list for "Add New Service" modal dropdown
        List<Unit> units = unitService.findAll();

        model.addAttribute("services",      dtos);
        model.addAttribute("totalServices", totalServices);
        model.addAttribute("activeServices",activeServices);
        model.addAttribute("keyword",       searchKeyword);
        model.addAttribute("statusFilter",  status);
        model.addAttribute("units",         units);

        return "admin/services/list";
    }

    // ── POST: Tạo service mới từ modal ────────────────────────────────────────
    @PostMapping("/create")
    public String createService(
            @Valid @ModelAttribute ServiceCreateDTO createDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return "redirect:/admin/services?error=" + URLEncoder.encode(errorMsg, StandardCharsets.UTF_8);
        }

        Optional<Unit> unitOpt = unitService.findById(createDTO.getUnitId());
        if (unitOpt.isEmpty()) {
            return "redirect:/admin/services?error=unit_not_found";
        }

        ServiceItem newService = new ServiceItem();
        newService.setServiceName(createDTO.getServiceName().trim());
        newService.setServiceType(createDTO.getServiceType().trim().toUpperCase());
        newService.setUnitPrice(createDTO.getUnitPrice());
        newService.setUnit(unitOpt.get());
        newService.setDescription(createDTO.getDescription() != null ? createDTO.getDescription().trim() : "");
        newService.setStatus(true); // mặc định ACTIVE khi tạo mới

        newService = serviceItemService.save(newService);

        com.quan.apartment_building_management_system.dto.systemlog.ServiceLogDTO newDto = com.quan.apartment_building_management_system.dto.systemlog.ServiceLogDTO.fromEntity(newService);
        systemLogService.logSystemAction("CREATE_SERVICE", "ServiceItem", newService.getServiceId(), null, newDto, "Created service " + newService.getServiceName());

        return "redirect:/admin/services?success=created";
    }

    // ── GET: Lấy chi tiết service (JSON) cho modal edit ────────────────────────
    @GetMapping("/{serviceId}/details")
    @ResponseBody
    public ResponseEntity<ServiceDTO> getServiceDetails(@PathVariable Integer serviceId) {
        Optional<ServiceItem> serviceOpt = serviceItemService.findById(serviceId);
        if (serviceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ServiceDTO(serviceOpt.get()));
    }

    // ── POST: Cập nhật service ────────────────────────────────────────────────────
    @PostMapping("/update")
    public String updateService(
            @Valid @ModelAttribute ServiceUpdateDTO updateDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return "redirect:/admin/services?error=" + URLEncoder.encode(errorMsg, StandardCharsets.UTF_8);
        }

        Optional<ServiceItem> serviceOpt = serviceItemService.findById(updateDTO.getServiceId());
        if (serviceOpt.isEmpty()) {
            return "redirect:/admin/services?error=service_not_found";
        }

        Optional<Unit> unitOpt = unitService.findById(updateDTO.getUnitId());
        if (unitOpt.isEmpty()) {
            return "redirect:/admin/services?error=unit_not_found";
        }

        ServiceItem service = serviceOpt.get();
        com.quan.apartment_building_management_system.dto.systemlog.ServiceLogDTO oldDto = com.quan.apartment_building_management_system.dto.systemlog.ServiceLogDTO.fromEntity(service);
        
        service.setServiceName(updateDTO.getServiceName().trim());
        service.setUnitPrice(updateDTO.getUnitPrice());
        service.setUnit(unitOpt.get());
        service.setDescription(updateDTO.getDescription() != null ? updateDTO.getDescription().trim() : "");

        service = serviceItemService.save(service);

        com.quan.apartment_building_management_system.dto.systemlog.ServiceLogDTO newDto = com.quan.apartment_building_management_system.dto.systemlog.ServiceLogDTO.fromEntity(service);
        systemLogService.logSystemAction("UPDATE_SERVICE", "ServiceItem", service.getServiceId(), oldDto, newDto, "Updated service " + service.getServiceName());

        return "redirect:/admin/services?success=updated";
    }

    // ── POST: Toggle service lock/unlock ──────────────────────────────────────
    @PostMapping("/{serviceId}/toggle-status")
    public String toggleServiceStatus(
            @PathVariable Integer serviceId,
            @RequestParam(value = "lock", required = false) Boolean lock) {

        Optional<ServiceItem> serviceOpt = serviceItemService.findById(serviceId);
        if (serviceOpt.isEmpty()) {
            return "redirect:/admin/services?error=service_not_found";
        }

        ServiceItem service = serviceOpt.get();
        com.quan.apartment_building_management_system.dto.systemlog.ServiceLogDTO oldDto = com.quan.apartment_building_management_system.dto.systemlog.ServiceLogDTO.fromEntity(service);

        // If lock param is provided, use it; otherwise toggle the current status
        if (lock != null) {
            service.setStatus(!lock); // lock=true means set status=false (locked)
        } else {
            service.setStatus(!service.getStatus()); // toggle current status
        }

        service = serviceItemService.save(service);

        com.quan.apartment_building_management_system.dto.systemlog.ServiceLogDTO newDto = com.quan.apartment_building_management_system.dto.systemlog.ServiceLogDTO.fromEntity(service);
        String actionStr = service.getStatus() ? "Unlocked" : "Locked";
        systemLogService.logSystemAction("UPDATE_SERVICE", "ServiceItem", service.getServiceId(), oldDto, newDto, actionStr + " service " + service.getServiceName());

        String action = service.getStatus() ? "unlocked" : "locked";
        return "redirect:/admin/services?success=" + action;
    }
}
