package com.example.service;

import com.example.entity.PatientEntity;
import com.example.generator.PatientGenerator;
import com.example.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PatientService {

    private final PatientRepository repo;
    private final PatientGenerator generator = new PatientGenerator(42);

    public PatientService(PatientRepository repo) {
        this.repo = repo;
    }

    public List<PatientEntity> getAllPatients() {
        return repo.findAll();
    }

    public PatientEntity getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public PatientEntity createMock() {
        return repo.save(generator.generate());
    }

    public List<PatientEntity> createMany(int count) {
        List<PatientEntity> results = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            results.add(repo.save(generator.generate()));
        }
        return results;
    }
}