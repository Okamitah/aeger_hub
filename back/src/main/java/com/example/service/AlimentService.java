package com.example.service;

import com.example.client.OpenFoodFactsClient;
import com.example.entity.AlimentEntity;
import com.example.repository.AlimentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlimentService {

    private final AlimentRepository repo;
    private final OpenFoodFactsClient offClient;

    public AlimentService(AlimentRepository repo, OpenFoodFactsClient offClient) {
        this.repo = repo;
        this.offClient = offClient;
    }

    public List<AlimentEntity> getAll() {
        return repo.findAll();
    }

    public AlimentEntity save(AlimentEntity aliment) {
        return repo.save(aliment);
    }

    public List<AlimentEntity> searchPreview(String query, int pageSize) throws Exception {
        return offClient.searchByName(query, pageSize);
    }

    public List<AlimentEntity> categoryPreview(String categoryTag, int pageSize) throws Exception {
        return offClient.searchByCategory(categoryTag, pageSize);
    }

    public AlimentEntity importOne(AlimentEntity aliment) {
        if (aliment.getOffId() != null && repo.existsByOffId(aliment.getOffId())) {
            return repo.findByOffId(aliment.getOffId()).get();
        }
        return repo.save(aliment);
    }

    public List<AlimentEntity> importMany(List<AlimentEntity> aliments) {
        return aliments.stream()
                .map(this::importOne)
                .toList();
    }
}
