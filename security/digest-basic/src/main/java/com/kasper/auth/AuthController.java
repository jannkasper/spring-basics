package com.kasper.auth;

import com.kasper.common.ApiResponse;
import com.kasper.user.RegisterDto;
import com.kasper.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@RequestBody UserDto registrationDto) {
        try {
            UserDto registeredUser = authService.register(registrationDto);
            return new ResponseEntity<>(
                    new ApiResponse<>("SUCCESS", "User registered successfully", registeredUser),
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>("ERROR", e.getMessage(), null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
    
    // Handle form post from the registration form
    @PostMapping(value = "/register", consumes = "application/x-www-form-urlencoded")
    public String registerForm(RegisterDto registerDto, RedirectAttributes redirectAttributes) {
        try {
            // Convert RegisterDto to UserDto
            UserDto userDto = new UserDto(
                    null,
                    registerDto.getUsername(),
                    registerDto.getEmail(),
                    registerDto.getPassword(),
                    true,
                    new HashSet<>()
            );
            
            authService.register(userDto);
            return "redirect:/login?registered";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto>> login(@RequestParam String username, @RequestParam String password) {
        try {
            boolean authenticated = authService.authenticate(username, password);
            if (authenticated) {
                UserDto currentUser = authService.getCurrentUser();
                return new ResponseEntity<>(
                        new ApiResponse<>("SUCCESS", "Login successful", currentUser),
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(
                        new ApiResponse<>("ERROR", "Invalid username or password", null),
                        HttpStatus.UNAUTHORIZED
                );
            }
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>("ERROR", e.getMessage(), null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @GetMapping("/current-user")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        try {
            UserDto currentUser = authService.getCurrentUser();
            if (currentUser != null) {
                return new ResponseEntity<>(
                        new ApiResponse<>("SUCCESS", "Current user retrieved", currentUser),
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(
                        new ApiResponse<>("ERROR", "No authenticated user found", null),
                        HttpStatus.UNAUTHORIZED
                );
            }
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>("ERROR", e.getMessage(), null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
} 