package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ApiController {

    @GetMapping("/api/")
    public Map<String, String> health() {
        return Map.of(
            "status", "ok",
            "message", "Backend is running with Spring Boot + PostgreSQL!"
        );
    }
}
