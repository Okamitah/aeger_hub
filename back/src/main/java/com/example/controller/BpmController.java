package com.example.controller;

import com.example.entity.BpmEntity;
import com.example.repository.BpmRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bpm")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE,
        RequestMethod.OPTIONS })
public class BpmController {

    private final BpmRepository bpmRepository;

    public BpmController(BpmRepository bpmRepository) {
        this.bpmRepository = bpmRepository;
    }

    @GetMapping
    public List<BpmEntity> getAll() {
        return bpmRepository.findAll();
    }

    @GetMapping("/patient/{patientId}")
    public List<BpmEntity> getByPatient(@PathVariable Long patientId) {
        return bpmRepository.findByPatientId(patientId);
    }
}