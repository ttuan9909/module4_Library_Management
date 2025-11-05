package com.example.library.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {
    @GetMapping("/account")
    public String account(@AuthenticationPrincipal User principal, Model model) {
        model.addAttribute("username", principal != null ? principal.getUsername() : null);
        return "account/profile";
    }
}
