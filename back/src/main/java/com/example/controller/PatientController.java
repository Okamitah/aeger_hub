package com.example.controller;

import com.example.entity.PatientEntity;
import com.example.service.PatientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
@CrossOrigin(origins = {"http://localhost:5173", "http://172.31.249.107"})
public class PatientController {

    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    @GetMapping
    public List<PatientEntity> getAllPatients() {
        return service.getAllPatients();
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
