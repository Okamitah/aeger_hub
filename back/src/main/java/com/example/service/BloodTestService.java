package com.example.service;

import com.example.entity.BloodTestEntity;
import com.example.entity.PatientEntity;
import com.example.generator.BloodTestGenerator;
import com.example.repository.BloodTestRepository;
import com.example.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BloodTestService {

    private final BloodTestRepository bloodTestRepository;
    private final PatientRepository patientRepository;

    public BloodTestService(BloodTestRepository bloodTestRepository, PatientRepository patientRepository) {
        this.bloodTestRepository = bloodTestRepository;
        this.patientRepository = patientRepository;
    }

    public List<BloodTestEntity> getAll() {
        return bloodTestRepository.findAll();
    }

    public List<BloodTestEntity> getByPatientId(Long patientId) {
        return bloodTestRepository.findByPatientId(patientId);
    }

    public BloodTestEntity createMock(Long patientId) {
        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));
        BloodTestGenerator generator = new BloodTestGenerator(System.nanoTime());
        return bloodTestRepository.save(generator.generate(patient));
    }

    public List<BloodTestEntity> createManyMocks(Long patientId, int count) {
        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));
        BloodTestGenerator generator = new BloodTestGenerator(System.nanoTime());
        List<BloodTestEntity> results = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            results.add(generator.generate(patient));
        }
        return bloodTestRepository.saveAll(results);
    }

    public void delete(Long id) {
        bloodTestRepository.deleteById(id);
    }
}