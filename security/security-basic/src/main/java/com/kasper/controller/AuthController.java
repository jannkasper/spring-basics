package com.kasper.controller;

import com.kasper.user.CustomUserDetails;
import com.kasper.user.UserDetailsRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
public class AuthController {

    private final UserDetailsRepository userDetailsRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UserDetailsRepository userDetailsRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userDetailsRepository = userDetailsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                         @RequestParam String password,
                         Model model) {
        if (userDetailsRepository.existsByUsername(username)) {
            model.addAttribute("error", "Username already exists");
            return "signup";
        }

        CustomUserDetails newUser = new CustomUserDetails(
            username,
            passwordEncoder.encode(password),
            Set.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        userDetailsRepository.save(newUser);
        return "redirect:/login?registered";
    }

    @GetMapping("/secured")
    public String securedPage(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        return "secured";
    }
}