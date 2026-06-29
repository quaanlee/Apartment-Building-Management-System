package com.quan.apartment_building_management_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/admin/logs";
    }

    @GetMapping("/admin/dashboard")
    public String dashboard() {
        return "redirect:/admin/logs";
    }
}
