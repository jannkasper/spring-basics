package com.kasper.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class ViewController {

    @GetMapping("/")
    public RedirectView home() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        RedirectView redirectView;
        
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            redirectView = new RedirectView("/dashboard", true);
        } else {
            redirectView = new RedirectView("/login", true);
        }
        
        // Preserve the original port by using relative redirects
        redirectView.setContextRelative(true);
        return redirectView;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("roles", auth.getAuthorities());
        }
        return "dashboard";
    }
} 