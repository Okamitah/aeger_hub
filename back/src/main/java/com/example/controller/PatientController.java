package com.example.controller;

import com.example.entity.AlimentEntity;
import com.example.entity.PatientEntity;
import com.example.service.AlimentRecommendationService;
import com.example.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patients")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class PatientController {

    private final PatientService service;
    private final AlimentRecommendationService recommendationService;

    public PatientController(PatientService service, AlimentRecommendationService recommendationService) {
        this.service = service;
        this.recommendationService = recommendationService;
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

    @GetMapping("/{id}/recommendations")
    public ResponseEntity<?> getAlimentRecommendations(@PathVariable Long id) {
        PatientEntity patient = service.getById(id);
        if (patient == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, List<AlimentEntity>> grouped =
                recommendationService.getRecommendedAlimentsGrouped(patient.getIllness());

        return ResponseEntity.ok(grouped);
    }
}
