package com.kasper.web;

import com.kasper.user.RegisterDto;
import com.kasper.user.UserDto;
import com.kasper.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new RegisterDto());
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        UserDto currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", currentUser);
        return "dashboard";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
} 