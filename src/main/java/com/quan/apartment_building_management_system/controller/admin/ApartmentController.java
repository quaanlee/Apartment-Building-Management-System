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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/apartments")
public class ApartmentController {

    private final ApartmentService apartmentService;
    private final ProfileRepository profileRepository;
    private final ResidentApartmentRepository residentApartmentRepository;

    public ApartmentController(ApartmentService apartmentService,
                               ProfileRepository profileRepository,
                               ResidentApartmentRepository residentApartmentRepository) {
        this.apartmentService = apartmentService;
        this.profileRepository = profileRepository;
        this.residentApartmentRepository = residentApartmentRepository;
    }

    @GetMapping
    public String listApartments(Model model) {
        List<Apartment> apartments = apartmentService.findAll();

        model.addAttribute("apartments", apartments);
        model.addAttribute("totalUnits", apartments.size());
        model.addAttribute("availableUnits", apartments.stream().filter(a -> a.getStatus() == 0).count());
        model.addAttribute("occupiedUnits", apartments.stream().filter(a -> a.getStatus() == 1).count());
        model.addAttribute("maintenanceUnits", apartments.stream().filter(a -> a.getStatus() == 2).count());

        return "admin/building-management";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("apartment", new Apartment());
        model.addAttribute("pageTitle", "Add New Unit");
        model.addAttribute("actionUrl", "/admin/apartments/save");
        return "admin/apartment-form";
    }

    @PostMapping("/save")
    public String saveApartment(@Valid @ModelAttribute("apartment") Apartment apartment,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Add New Unit");
            model.addAttribute("actionUrl", "/admin/apartments/save");
            return "admin/apartment-form";
        }

        Optional<Apartment> existing = apartmentService.findByApartmentNumber(apartment.getApartmentNumber());

        if (existing.isPresent()) {
            bindingResult.rejectValue("apartmentNumber", "duplicate", "Apartment number already exists!");
            model.addAttribute("pageTitle", "Add New Unit");
            model.addAttribute("actionUrl", "/admin/apartments/save");
            return "admin/apartment-form";
        }

        if (apartment.getStatus() == null) {
            apartment.setStatus((byte) 0);
        }

        apartmentService.save(apartment);
        redirectAttributes.addFlashAttribute("successMessage", "Apartment added successfully!");

        return "redirect:/admin/apartments";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        Optional<Apartment> apartment = apartmentService.findById(id);

        if (apartment.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Apartment not found!");
            return "redirect:/admin/apartments";
        }

        model.addAttribute("apartment", apartment.get());
        model.addAttribute("pageTitle", "Edit Unit");
        model.addAttribute("actionUrl", "/admin/apartments/update/" + id);

        return "admin/apartment-form";
    }

    @PostMapping("/update/{id}")
    public String updateApartment(@PathVariable Integer id,
                                  @Valid @ModelAttribute("apartment") Apartment apartment,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        Optional<Apartment> currentApartment = apartmentService.findById(id);

        if (currentApartment.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Apartment not found!");
            return "redirect:/admin/apartments";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Unit");
            model.addAttribute("actionUrl", "/admin/apartments/update/" + id);
            return "admin/apartment-form";
        }

        Optional<Apartment> duplicate = apartmentService.findByApartmentNumber(apartment.getApartmentNumber());

        if (duplicate.isPresent() && !duplicate.get().getApartmentId().equals(id)) {
            bindingResult.rejectValue("apartmentNumber", "duplicate", "Apartment number already exists!");
            model.addAttribute("pageTitle", "Edit Unit");
            model.addAttribute("actionUrl", "/admin/apartments/update/" + id);
            return "admin/apartment-form";
        }

        Apartment updateApartment = currentApartment.get();
        updateApartment.setApartmentNumber(apartment.getApartmentNumber());
        updateApartment.setFloor(apartment.getFloor());
        updateApartment.setArea(apartment.getArea());
        updateApartment.setRoomType(apartment.getRoomType());
        updateApartment.setStatus(apartment.getStatus());
        updateApartment.setMaxOccupancy(apartment.getMaxOccupancy());

        apartmentService.save(updateApartment);
        redirectAttributes.addFlashAttribute("successMessage", "Apartment updated successfully!");

        return "redirect:/admin/apartments";
    }

    @GetMapping("/detail/{id}")
    public String viewApartmentDetail(@PathVariable Integer id,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {

        Optional<Apartment> apartment = apartmentService.findById(id);

        if (apartment.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Apartment not found!");
            return "redirect:/admin/apartments";
        }

        List<ResidentApartment> residentApartments =
                residentApartmentRepository.findByApartmentApartmentId(id);

        model.addAttribute("apartment", apartment.get());
        model.addAttribute("residentApartments", residentApartments);

        return "admin/apartment-detail";
    }

    @GetMapping("/delete/{id}")
    public String deleteApartment(@PathVariable Integer id,
                                  RedirectAttributes redirectAttributes) {

        Optional<Apartment> apartment = apartmentService.findById(id);

        if (apartment.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Apartment not found!");
            return "redirect:/admin/apartments";
        }

        apartmentService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Apartment deleted successfully!");

        return "redirect:/admin/apartments";
    }

    @GetMapping("/assign/{apartmentId}")
    public String showAssignResidentForm(@PathVariable Integer apartmentId,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {

        Optional<Apartment> apartment = apartmentService.findById(apartmentId);

        if (apartment.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Apartment not found!");
            return "redirect:/admin/apartments";
        }

        List<Profile> residents = profileRepository.findAll();

        model.addAttribute("apartment", apartment.get());
        model.addAttribute("residents", residents);

        return "admin/assign-resident";
    }

    @PostMapping("/assign")
    public String assignResident(@RequestParam Integer apartmentId,
                                 @RequestParam Integer profileId,
                                 @RequestParam String moveInDate,
                                 RedirectAttributes redirectAttributes) {

        Optional<Apartment> apartmentOptional = apartmentService.findById(apartmentId);
        Optional<Profile> profileOptional = profileRepository.findById(profileId);

        if (apartmentOptional.isEmpty() || profileOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Apartment or resident not found!");
            return "redirect:/admin/apartments";
        }

        Apartment apartment = apartmentOptional.get();
        Profile profile = profileOptional.get();

        List<ResidentApartment> currentResidents =
                residentApartmentRepository.findByApartmentApartmentId(apartmentId)
                        .stream()
                        .filter(ra -> ra.getMoveOutDate() == null)
                        .toList();

        if (currentResidents.size() >= apartment.getMaxOccupancy()) {
            redirectAttributes.addFlashAttribute("errorMessage", "This apartment has reached maximum occupancy!");
            return "redirect:/admin/apartments/assign/" + apartmentId;
        }

        ResidentApartment residentApartment = new ResidentApartment();
        residentApartment.setApartment(apartment);
        residentApartment.setProfile(profile);
        residentApartment.setMoveInDate(LocalDate.parse(moveInDate));

        residentApartmentRepository.save(residentApartment);

        profile.setApartment(apartment);
        profile.setMoveInDate(LocalDate.parse(moveInDate));
        profile.setResidentStatus((byte) 1);
        profileRepository.save(profile);

        apartment.setStatus((byte) 1);
        apartmentService.save(apartment);

        redirectAttributes.addFlashAttribute("successMessage", "Resident assigned successfully!");

        return "redirect:/admin/apartments/detail/" + apartmentId;
    }
}