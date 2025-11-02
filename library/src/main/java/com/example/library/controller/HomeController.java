package com.example.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String index() { return "home"; }


    @GetMapping("/admin")
    public String admin() { return "admin/home/home"; }

    @GetMapping("/admin/lending")
    public String lending() { return "admin/lending/lending"; }
}
