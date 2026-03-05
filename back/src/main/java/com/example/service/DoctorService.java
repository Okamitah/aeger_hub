package com.example.service;

import com.example.entity.DoctorEntity;
import com.example.generator.DoctorGenerator;
import com.example.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class DoctorService {

    private final DoctorRepository repo;
    private final DoctorGenerator generator = new DoctorGenerator(99);

    public DoctorService(DoctorRepository repo) {
        this.repo = repo;
    }

    public List<DoctorEntity> getAllDoctors() {
        return repo.findAll();
    }

    public DoctorEntity createMock() {
        return repo.save(generator.generate());
    }

    public List<DoctorEntity> createMany(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> generator.generate())
                .map(repo::save)
                .toList();
    }
}
