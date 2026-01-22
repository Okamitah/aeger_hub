package com.example.generator;

import com.example.entity.*;
import com.example.utils.RandomUtils;
import java.util.SplittableRandom;
import java.time.LocalDate;

public class PatientGenerator {

    private final SplittableRandom rng;

    public PatientGenerator(long seed) {
        this.rng = new SplittableRandom(seed);
    }

    public PatientEntity generate() {
        PatientEntity p = new PatientEntity();

        Sex sex = rng.nextBoolean() ? Sex.MALE : Sex.FEMALE;
        p.setSex(sex);

        double height = sex == Sex.MALE
                ? 165 + rng.nextDouble() * 25
                : 155 + rng.nextDouble() * 25;

        double bmi = 18.5 + rng.nextDouble() * 11.5;
        double weight = bmi * Math.pow(height / 100.0, 2);

        p.setHeightCm(round(height));
        p.setWeightKg(round(weight));

        AthleticismLevel athleticismLevel = RandomUtils.pick(AthleticismLevel.values(), rng);
        p.setAthleticism(athleticismLevel.getLevel());
        
        p.setSleepQuality(RandomUtils.pick(SleepQuality.values(), rng));
        p.setIllness(RandomUtils.pick(Illness.values(), rng));

        p.setSmoker(rng.nextDouble() < 0.25);
        p.setDrinker(rng.nextDouble() < 0.40);

        p.setBpmMax(180 - (athleticismLevel.getLevel() * 5) + rng.nextInt(10));

        p.setName(NameGenerator.randomName(rng));

        int ageInYears = 18 + rng.nextInt(63); // 18 to 80
        p.setBirthDate(LocalDate.now().minusYears(ageInYears).minusDays(rng.nextInt(365)));

        p.setCurrentActivityState(RandomUtils.pick(ActivityState.values(), rng));

        // p.setTrackingEnabled(true);

        p.setUserId(null);

        return p;
    }

    private double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
