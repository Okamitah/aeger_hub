package com.example.service;

import com.example.entity.PatientEntity;
import com.example.generator.PatientGenerator;
import com.example.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

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

    public PatientEntity createMock() {
        return repo.save(generator.generate());
    }

    public List<PatientEntity> createMany(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> generator.generate())
                .map(repo::save)
                .toList();
    }
}
