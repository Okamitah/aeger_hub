package com.example.generator;

import com.example.entity.BloodTestEntity;
import com.example.entity.PatientEntity;
import com.example.entity.Sex;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

public class BloodTestGenerator {

    public BloodTestGenerator(long seed) {
    }

    public BloodTestEntity generate(PatientEntity patient) {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        boolean isMale = patient.getSex() == Sex.MALE;

        double hemoglobin = isMale
                ? 13.8 + rng.nextDouble() * 3.4
                : 12.1 + rng.nextDouble() * 3.0;

        double wbc = 4.5 + rng.nextDouble() * 6.5;

        double platelets = 150 + rng.nextDouble() * 300;

        double crp = rng.nextDouble() * 12.0;

        double creatinine = isMale
                ? 0.74 + rng.nextDouble() * 0.61
                : 0.59 + rng.nextDouble() * 0.45;

        double ast = 8 + rng.nextDouble() * 32;
        double alt = 8 + rng.nextDouble() * 32;

        double ggt = 9 + rng.nextDouble() * 39;

        double sodium = 135 + rng.nextDouble() * 10;

        double potassium = 3.5 + rng.nextDouble() * 1.5;

        double fastingGlucose = 70 + rng.nextDouble() * 50;

        double totalCholesterol = 150 + rng.nextDouble() * 70;

        LocalDateTime takenAt = LocalDateTime.now()
                .minusDays(rng.nextInt(365))
                .minusHours(rng.nextInt(24));

        return new BloodTestEntity(
                patient,
                takenAt,
                round(hemoglobin),
                round(wbc),
                round(platelets),
                round(crp),
                round(creatinine),
                round(ast),
                round(alt),
                round(ggt),
                round(sodium),
                round(potassium),
                round(fastingGlucose),
                round(totalCholesterol));
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}