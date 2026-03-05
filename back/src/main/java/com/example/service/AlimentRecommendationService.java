package com.example.service;

import com.example.entity.AlimentEntity;
import com.example.entity.Illness;
import com.example.repository.AlimentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AlimentRecommendationService {

    private final AlimentRepository alimentRepository;

    public AlimentRecommendationService(AlimentRepository alimentRepository) {
        this.alimentRepository = alimentRepository;
    }

    public List<AlimentEntity> getRecommendedAliments(Illness illness) {
        List<AlimentEntity> all = alimentRepository.findAll();
        return all.stream()
                .filter(a -> passes(a, illness))
                .collect(Collectors.toList());
    }

    public Map<String, List<AlimentEntity>> getRecommendedAlimentsGrouped(Illness illness) {
        return getRecommendedAliments(illness).stream()
                .collect(Collectors.groupingBy(
                        a -> a.getCategory() != null ? a.getCategory() : "uncategorized"
                ));
    }

    private boolean passes(AlimentEntity a, Illness illness) {
        return switch (illness) {
            case DIABETES     -> passesDiabetes(a);
            case HYPERTENSION -> passesHypertension(a);
            case HEART_DISEASE -> passesHeartDisease(a);
            case ASTHMA       -> passesAsthma(a);
            case HEALTHY      -> true;
        };
    }

    private boolean passesDiabetes(AlimentEntity a) {
        if (exceeds(a.getSugarG(), 5.0))          return false;
        if (exceeds(a.getCarbohydratesG(), 30.0)) return false;
        return true;
    }

    private boolean passesHypertension(AlimentEntity a) {
        if (exceeds(a.getSodiumMg(), 120.0)) return false;
        if (exceeds(a.getFatG(), 15.0))      return false;
        return true;
    }

    private boolean passesHeartDisease(AlimentEntity a) {
        if (exceeds(a.getFatG(), 10.0))          return false;
        if (exceeds(a.getCholesterolMg(), 50.0)) return false;
        if (exceeds(a.getSodiumMg(), 140.0))     return false;
        return true;
    }

    private boolean passesAsthma(AlimentEntity a) {
        if (exceeds(a.getSodiumMg(), 200.0)) return false;
        if (exceeds(a.getFatG(), 20.0))      return false;
        return true;
    }

    private boolean exceeds(Double value, double threshold) {
        return value != null && value > threshold;
    }
}
