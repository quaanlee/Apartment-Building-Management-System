package com.quan.apartment_building_management_system.controller.resident.maintenance;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.quan.apartment_building_management_system.entity.*;
import com.quan.apartment_building_management_system.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("resident/maintenance")
public class ResidentMaintenanceController {

    private final MaintenanceRequestRepository requestRepo;
    private final MaintenanceRequestImageRepository requestImageRepo;
    private final MaintenanceTaskRepository taskRepo;
    private final MaintenanceReportRepository reportRepo;
    private final ProfileRepository profileRepo;
    private final ResidentApartmentRepository residentApartmentRepo;
    private final Cloudinary cloudinary;

    public ResidentMaintenanceController(MaintenanceRequestRepository requestRepo,
                                          MaintenanceRequestImageRepository requestImageRepo,
                                          MaintenanceTaskRepository taskRepo,
                                          MaintenanceReportRepository reportRepo,
                                          ProfileRepository profileRepo,
                                          ResidentApartmentRepository residentApartmentRepo,
                                          Cloudinary cloudinary) {
        this.requestRepo = requestRepo;
        this.requestImageRepo = requestImageRepo;
        this.taskRepo = taskRepo;
        this.reportRepo = reportRepo;
        this.profileRepo = profileRepo;
        this.residentApartmentRepo = residentApartmentRepo;
        this.cloudinary = cloudinary;
    }

    private Profile getProfile(HttpSession session) {
        Account currentUser = (Account) session.getAttribute("currentUser");
        if (currentUser == null) return null;
        return profileRepo.findByAccountAccountId(currentUser.getAccountId()).orElse(null);
    }

    @GetMapping
    public String listRequests(
            @RequestParam(value = "status", required = false) Byte status,
            @RequestParam(value = "fromDate", required = false) String fromDateStr,
            @RequestParam(value = "toDate", required = false) String toDateStr,
            HttpSession session, Model model) {

        Profile profile = getProfile(session);
        if (profile == null) return "redirect:/login";

        List<MaintenanceRequest> all = requestRepo.findByProfileProfileId(profile.getProfileId());

        if (status != null) {
            all = all.stream().filter(r -> r.getStatus().equals(status)).toList();
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime fromDate = (fromDateStr != null && !fromDateStr.isBlank())
                ? LocalDate.parse(fromDateStr, dtf).atStartOfDay() : null;
        LocalDateTime toDate = (toDateStr != null && !toDateStr.isBlank())
                ? LocalDate.parse(toDateStr, dtf).atTime(23, 59, 59) : null;

        if (fromDate != null) {
            all = all.stream().filter(r -> !r.getRequestDate().isBefore(fromDate)).toList();
        }
        if (toDate != null) {
            all = all.stream().filter(r -> !r.getRequestDate().isAfter(toDate)).toList();
        }

        List<Map<String, Object>> enrichedList = new ArrayList<>();
        for (MaintenanceRequest req : all) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("requestId", req.getRequestId());
            item.put("title", req.getTitle());
            item.put("requestDate", req.getRequestDate());
            item.put("status", req.getStatus());
            item.put("apartmentNumber", req.getApartment() != null ? req.getApartment().getApartmentNumber() : "-");

            var taskOpt = taskRepo.findByMaintenanceRequestRequestId(req.getRequestId());
            if (taskOpt.isPresent()) {
                MaintenanceTask task = taskOpt.get();
                item.put("staffName", task.getStaff() != null ? task.getStaff().getUsername() : "-");
                var reports = reportRepo.findByMaintenanceTaskTaskId(task.getTaskId());
                if (!reports.isEmpty()) {
                    MaintenanceReport last = reports.get(reports.size() - 1);
                    item.put("progressPercent", last.getProgressPercent());
                    item.put("latestReport", last.getReportContent());
                } else {
                    item.put("progressPercent", 0);
                    item.put("latestReport", "");
                }
            } else {
                item.put("staffName", "-");
                item.put("progressPercent", 0);
                item.put("latestReport", "");
            }
            enrichedList.add(item);
        }

        model.addAttribute("enrichedRequests", enrichedList);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("fromDate", fromDateStr);
        model.addAttribute("toDate", toDateStr);
        model.addAttribute("pageTitle", "My Maintenance Requests");
        return "resident/maintenance/list";
    }

    @GetMapping("/create")
    public String showCreateForm(HttpSession session, Model model) {
        Profile profile = getProfile(session);
        if (profile == null) return "redirect:/login";
        model.addAttribute("pageTitle", "Create Maintenance Request");
        return "resident/maintenance/create";
    }

    @PostMapping("/create")
    @Transactional
    public String createRequest(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            HttpSession session,
            RedirectAttributes redirect) {

        Profile profile = getProfile(session);
        if (profile == null) return "redirect:/login";

        Apartment apartment = profile.getApartment();
        if (apartment == null) {
            var ra = residentApartmentRepo.findCurrentApartmentByProfile(profile.getProfileId());
            if (ra.isPresent()) {
                apartment = ra.get().getApartment();
            }
        }
        if (apartment == null) {
            redirect.addFlashAttribute("message", "No apartment assigned to your profile.");
            redirect.addFlashAttribute("messageType", "error");
            return "redirect:/resident/maintenance";
        }

        MaintenanceRequest req = new MaintenanceRequest();
        req.setProfile(profile);
        req.setApartment(apartment);
        req.setTitle(title);
        req.setDescription(description);
        req.setStatus((byte) 0);
        req.setRequestDate(LocalDateTime.now());
        req = requestRepo.save(req);

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    try {
                        var uploadResult = cloudinary.uploader().upload(
                            image.getBytes(),
                            ObjectUtils.asMap("folder", "maintenance/resident")
                        );
                        String url = uploadResult.get("secure_url").toString();
                        MaintenanceRequestImage img = new MaintenanceRequestImage();
                        img.setRequest(req);
                        img.setImageUrl(url);
                        requestImageRepo.save(img);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        redirect.addFlashAttribute("message", "Maintenance request created successfully.");
        redirect.addFlashAttribute("messageType", "success");
        return "redirect:/resident/maintenance";
    }

    @GetMapping("/{id}/detail")
    public String viewDetail(@PathVariable Integer id, HttpSession session, Model model) {
        Profile profile = getProfile(session);
        if (profile == null) return "redirect:/login";

        MaintenanceRequest req = requestRepo.findById(id).orElse(null);
        if (req == null || !req.getProfile().getProfileId().equals(profile.getProfileId())) {
            return "redirect:/resident/maintenance";
        }

        List<MaintenanceRequestImage> images = requestImageRepo.findAll().stream()
                .filter(img -> img.getRequest().getRequestId().equals(id))
                .toList();

        // Get task and progress info for detail page
        var taskOpt = taskRepo.findByMaintenanceRequestRequestId(req.getRequestId());
        if (taskOpt.isPresent()) {
            MaintenanceTask task = taskOpt.get();
            model.addAttribute("taskStaffName", task.getStaff() != null ? task.getStaff().getUsername() : "-");
            model.addAttribute("taskDeadline", task.getDeadline());
            var reports = reportRepo.findByMaintenanceTaskTaskId(task.getTaskId());
            if (!reports.isEmpty()) {
                MaintenanceReport last = reports.get(reports.size() - 1);
                model.addAttribute("progressPercent", last.getProgressPercent());
                model.addAttribute("latestReport", last.getReportContent());
                model.addAttribute("latestReportDate", last.getCreatedAt());
            } else {
                model.addAttribute("progressPercent", 0);
                model.addAttribute("latestReport", "");
            }
            model.addAttribute("hasTask", true);
        } else {
            model.addAttribute("hasTask", false);
        }

        model.addAttribute("request", req);
        model.addAttribute("images", images);
        model.addAttribute("pageTitle", "Request #" + id);
        return "resident/maintenance/detail";
    }
}

