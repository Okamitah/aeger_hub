package com.example.controller;

import com.example.entity.UserEntity;
import com.example.repository.UserRepository;
import com.example.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Map<String, Object> response = new HashMap<>();

        Optional<UserEntity> userInDb = userRepository.findByUsername(username);

        if (userInDb.isPresent()) {
            UserEntity user = userInDb.get();
            
            boolean passwordMatches = false;
            if (user.getPassword().startsWith("$2a$") || user.getPassword().startsWith("$2b$")) {
                passwordMatches = passwordEncoder.matches(password, user.getPassword());
            } else {
                passwordMatches = user.getPassword().equals(password);
            }

            if (passwordMatches) {
                String token = jwtUtil.generateToken(username);
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("token", token);
                response.put("username", username);
            } else {
                response.put("success", false);
                response.put("message", "Invalid username or password");
            }
        } else {
            response.put("success", false);
            response.put("message", "Invalid username or password");
        }

        return response;
    }
}
