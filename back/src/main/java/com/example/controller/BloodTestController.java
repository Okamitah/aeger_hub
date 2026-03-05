package com.example.controller;

import com.example.entity.BloodTestEntity;
import com.example.service.BloodTestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blood-tests")
public class BloodTestController {

    private final BloodTestService service;

    public BloodTestController(BloodTestService service) {
        this.service = service;
    }

    @GetMapping
    public List<BloodTestEntity> getAll() {
        return service.getAll();
    }

    @GetMapping("/patient/{patientId}")
    public List<BloodTestEntity> getByPatient(@PathVariable Long patientId) {
        return service.getByPatientId(patientId);
    }

    @PostMapping("/mock/patient/{patientId}")
    public BloodTestEntity createMock(@PathVariable Long patientId) {
        return service.createMock(patientId);
    }

    @PostMapping("/mock/patient/{patientId}/many/{count}")
    public List<BloodTestEntity> createMany(@PathVariable Long patientId, @PathVariable int count) {
        return service.createManyMocks(patientId, count);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}