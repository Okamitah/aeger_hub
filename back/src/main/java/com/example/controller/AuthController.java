package com.example.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://172.31.249.107"
})
public class AuthController {

    private static final Map<String, String> USER_DB = new HashMap<>();

    static {
        USER_DB.put("user1", "pass1");
        USER_DB.put("admin", "secret");
    }

    @PostMapping("/login/")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Map<String, Object> response = new HashMap<>();

        if (username != null && password != null && 
            USER_DB.containsKey(username) && 
            USER_DB.get(username).equals(password)) {
            
            response.put("success", true);
            response.put("message", "Login successful");
        } else {
            response.put("success", false);
            response.put("message", "Invalid username or password");
        }

        return response;
    }
}
