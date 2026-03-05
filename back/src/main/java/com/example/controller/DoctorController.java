package com.example.controller;

import com.example.entity.DoctorEntity;
import com.example.service.DoctorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService service;

    public DoctorController(DoctorService service) {
        this.service = service;
    }

    @GetMapping
    public List<DoctorEntity> getAll() {
        return service.getAllDoctors();
    }

    @PostMapping("/mock")
    public DoctorEntity createOne() {
        return service.createMock();
    }

    @PostMapping("/mock/{count}")
    public List<DoctorEntity> createMany(@PathVariable int count) {
        return service.createMany(count);
    }
}
