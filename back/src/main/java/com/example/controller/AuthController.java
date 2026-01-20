package com.example.controller;

import com.example.entity.UserEntity;
import com.example.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://172.31.249.107"})
public class AuthController {

    private final UserRepository userRepository;

    // Inject the repository (Database Tool)
    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Map<String, Object> response = new HashMap<>();

        // 1. Ask Database: "Do we have a user with this name?"
        Optional<UserEntity> userInDb = userRepository.findByUsername(username);

        // 2. Check if User exists AND Password matches
        if (userInDb.isPresent() && userInDb.get().getPassword().equals(password)) {
            response.put("success", true);
            response.put("message", "Login successful (Validated by PostgreSQL Database!)");
        } else {
            response.put("success", false);
            response.put("message", "Invalid username or password");
        }

        return response;
    }
}