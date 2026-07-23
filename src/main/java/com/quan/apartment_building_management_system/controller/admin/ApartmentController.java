package com.quan.apartment_building_management_system.controller.admin;

import com.quan.apartment_building_management_system.entity.Apartment;
import com.quan.apartment_building_management_system.entity.Profile;
import com.quan.apartment_building_management_system.entity.ResidentApartment;
import com.quan.apartment_building_management_system.repository.ProfileRepository;
import com.quan.apartment_building_management_system.repository.ResidentApartmentRepository;
import com.quan.apartment_building_management_system.service.apartment.ApartmentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import com.quan.apartment_building_management_system.service.system.SystemLogService;

@Controller
@RequestMapping("/admin/apartments")
public class ApartmentController {

    private final ApartmentService apartmentService;
    private final ProfileRepository profileRepository;
    private final ResidentApartmentRepository residentApartmentRepository;
    private final SystemLogService systemLogService;

    public ApartmentController(ApartmentService apartmentService,
                               ProfileRepository profileRepository,
                               ResidentApartmentRepository residentApartmentRepository,
                               SystemLogService systemLogService) {
        this.apartmentService = apartmentService;
        this.profileRepository = profileRepository;
        this.residentApartmentRepository = residentApartmentRepository;
        this.systemLogService = systemLogService;
    }

    @GetMapping
    public String listApartments(@RequestParam(value = "query", required = false) String query, Model model) {
        List<Apartment> apartments = apartmentService.searchApartments(query);
        model.addAttribute("query", query);

        model.addAttribute("apartments", apartments);
        model.addAttribute("totalUnits", apartments.size());
        model.addAttribute("availableUnits", apartments.stream().filter(a -> a.getStatus() == 0).count());
        model.addAttribute("occupiedUnits", apartments.stream().filter(a -> a.getStatus() == 1).count());
        model.addAttribute("maintenanceUnits", apartments.stream().filter(a -> a.getStatus() == 2).count());

        return "admin/apartment/building-management";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("apartment", new Apartment());
        model.addAttribute("pageTitle", "Thêm căn hộ mới");
        model.addAttribute("actionUrl", "/admin/apartments/save");
        return "admin/apartment/apartment-form";
    }

    @PostMapping("/save")
    public String saveApartment(@Valid @ModelAttribute("apartment") Apartment apartment,
                                 BindingResult bindingResult,
                                 @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
                                 @RequestParam(value = "subImage1", required = false) MultipartFile subImage1,
                                 @RequestParam(value = "subImage2", required = false) MultipartFile subImage2,
                                 @RequestParam(value = "deleteMainImage", defaultValue = "false") boolean deleteMainImage,
                                 @RequestParam(value = "deleteSubImage1", defaultValue = "false") boolean deleteSubImage1,
                                 @RequestParam(value = "deleteSubImage2", defaultValue = "false") boolean deleteSubImage2,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Thêm căn hộ mới");
            model.addAttribute("actionUrl", "/admin/apartments/save");
            return "admin/apartment/apartment-form";
        }

        Optional<Apartment> existing = apartmentService.findByApartmentNumber(apartment.getApartmentNumber());

        if (existing.isPresent()) {
            bindingResult.rejectValue("apartmentNumber", "duplicate", "Số căn hộ đã tồn tại trong hệ thống!");
            model.addAttribute("pageTitle", "Thêm căn hộ mới");
            model.addAttribute("actionUrl", "/admin/apartments/save");
            return "admin/apartment/apartment-form";
        }

        if (apartment.getStatus() == null) {
            apartment.setStatus((byte) 0);
        }

        Apartment savedApartment = apartmentService.save(apartment);
        try {
            apartmentService.saveApartmentImages(savedApartment, mainImage, subImage1, subImage2, deleteMainImage, deleteSubImage1, deleteSubImage2);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tải ảnh lên: " + e.getMessage());
        }

        com.quan.apartment_building_management_system.dto.systemlog.ApartmentLogDTO newDto = com.quan.apartment_building_management_system.dto.systemlog.ApartmentLogDTO.fromEntity(savedApartment);
        systemLogService.logSystemAction("CREATE_APARTMENT", "Apartment", savedApartment.getApartmentId(), null, newDto, "Created apartment " + savedApartment.getApartmentNumber());

        redirectAttributes.addFlashAttribute("successMessage", "Thêm căn hộ thành công!");

        return "redirect:/admin/apartments";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        Optional<Apartment> apartment = apartmentService.findById(id);

        if (apartment.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy căn hộ!");
            return "redirect:/admin/apartments";
        }

        model.addAttribute("apartment", apartment.get());
        model.addAttribute("pageTitle", "Chỉnh sửa căn hộ");
        model.addAttribute("actionUrl", "/admin/apartments/update/" + id);

        return "admin/apartment/apartment-form";
    }

    @PostMapping("/update/{id}")
    public String updateApartment(@PathVariable Integer id,
                                  @Valid @ModelAttribute("apartment") Apartment apartment,
                                  BindingResult bindingResult,
                                  @RequestParam(value = "mainImage", required = false) MultipartFile mainImage,
                                  @RequestParam(value = "subImage1", required = false) MultipartFile subImage1,
                                  @RequestParam(value = "subImage2", required = false) MultipartFile subImage2,
                                  @RequestParam(value = "deleteMainImage", defaultValue = "false") boolean deleteMainImage,
                                  @RequestParam(value = "deleteSubImage1", defaultValue = "false") boolean deleteSubImage1,
                                  @RequestParam(value = "deleteSubImage2", defaultValue = "false") boolean deleteSubImage2,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        Optional<Apartment> currentApartment = apartmentService.findById(id);

        if (currentApartment.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy căn hộ!");
            return "redirect:/admin/apartments";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Chỉnh sửa căn hộ");
            model.addAttribute("actionUrl", "/admin/apartments/update/" + id);
            return "admin/apartment/apartment-form";
        }

        Optional<Apartment> duplicate = apartmentService.findByApartmentNumber(apartment.getApartmentNumber());

        if (duplicate.isPresent() && !duplicate.get().getApartmentId().equals(id)) {
            bindingResult.rejectValue("apartmentNumber", "duplicate", "Số căn hộ đã tồn tại trong hệ thống!");
            model.addAttribute("pageTitle", "Chỉnh sửa căn hộ");
            model.addAttribute("actionUrl", "/admin/apartments/update/" + id);
            return "admin/apartment/apartment-form";
        }

        Apartment updateApartment = currentApartment.get();
        com.quan.apartment_building_management_system.dto.systemlog.ApartmentLogDTO oldDto = com.quan.apartment_building_management_system.dto.systemlog.ApartmentLogDTO.fromEntity(updateApartment);

        updateApartment.setApartmentNumber(apartment.getApartmentNumber());
        updateApartment.setFloor(apartment.getFloor());
        updateApartment.setArea(apartment.getArea());
        updateApartment.setRoomType(apartment.getRoomType());
        updateApartment.setStatus(apartment.getStatus());
        updateApartment.setMaxOccupancy(apartment.getMaxOccupancy());

        updateApartment = apartmentService.save(updateApartment);
        try {
            apartmentService.saveApartmentImages(updateApartment, mainImage, subImage1, subImage2, deleteMainImage, deleteSubImage1, deleteSubImage2);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tải ảnh lên: " + e.getMessage());
        }

        com.quan.apartment_building_management_system.dto.systemlog.ApartmentLogDTO newDto = com.quan.apartment_building_management_system.dto.systemlog.ApartmentLogDTO.fromEntity(updateApartment);
        systemLogService.logSystemAction("UPDATE_APARTMENT", "Apartment", updateApartment.getApartmentId(), oldDto, newDto, "Updated apartment " + updateApartment.getApartmentNumber());

        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật căn hộ thành công!");

        return "redirect:/admin/apartments";
    }

    @GetMapping("/detail/{id}")
    public String viewApartmentDetail(@PathVariable Integer id,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {

        Optional<Apartment> apartment = apartmentService.findById(id);

        if (apartment.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy căn hộ!");
            return "redirect:/admin/apartments";
        }

        List<ResidentApartment> residentApartments =
                residentApartmentRepository.findByApartmentApartmentId(id);

        model.addAttribute("apartment", apartment.get());
        model.addAttribute("residentApartments", residentApartments);

        return "admin/apartment/apartment-detail";
    }

    @GetMapping("/delete/{id}")
    public String deleteApartment(@PathVariable Integer id,
                                  RedirectAttributes redirectAttributes) {

        Optional<Apartment> apartment = apartmentService.findById(id);

        if (apartment.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy căn hộ!");
            return "redirect:/admin/apartments";
        }

        try {
            apartmentService.deleteById(id);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa căn hộ vì đang có cư dân hoặc dữ liệu liên quan!");
            return "redirect:/admin/apartments";
        }

        com.quan.apartment_building_management_system.dto.systemlog.ApartmentLogDTO oldDto =
                com.quan.apartment_building_management_system.dto.systemlog.ApartmentLogDTO.fromEntity(apartment.get());
        systemLogService.logSystemAction("DELETE_APARTMENT", "Apartment", id, oldDto, null,
                "Deleted apartment " + apartment.get().getApartmentNumber());

        redirectAttributes.addFlashAttribute("successMessage", "Xóa căn hộ thành công!");

        return "redirect:/admin/apartments";
    }

}
