package com.quan.apartment_building_management_system.controller.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "redirect:/features";
    }

    @GetMapping("/about")
    public String about() {
        return "home-page/about";
    }

    @GetMapping("/features")
    public String features() {
        return "home-page/features";
    }

    @GetMapping("/contact")
    public String contact() {
        return "home-page/contact";
    }
}
