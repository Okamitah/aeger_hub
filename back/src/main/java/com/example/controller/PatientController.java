package com.example.controller;

import com.example.entity.PatientEntity;
import com.example.service.PatientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    @PostMapping("/mock")
    public PatientEntity createMock() {
        return service.createMock();
    }

    @PostMapping("/mock/{count}")
    public List<PatientEntity> createMany(@PathVariable int count) {
        return service.createMany(count);
    }
}

