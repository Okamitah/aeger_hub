package com.example.service;

import com.example.entity.ActivityState;
import com.example.entity.BpmEntity;
import com.example.entity.PatientEntity;
import com.example.repository.BpmRepository;
import com.example.repository.PatientRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class BpmSimulationService {

    private final PatientRepository patientRepository;
    private final BpmRepository bpmRepository;

    public BpmSimulationService(PatientRepository patientRepository, BpmRepository bpmRepository) {
        this.patientRepository = patientRepository;
        this.bpmRepository = bpmRepository;
    }

    @Scheduled(fixedRate = 5000)
    public void simulateHeartRates() {
        List<PatientEntity> patients = patientRepository.findAll();

        for (PatientEntity patient : patients) {
            if (!patient.isTrackingEnabled())
                continue;

            int calculatedBpm = calculateBpm(patient);

            if (calculatedBpm > patient.getBpmMax()) {
                System.out.println(
                        "WARNING: Patient " + patient.getName() + " exceeded max BPM! Value: " + calculatedBpm);
            }

            bpmRepository.save(new BpmEntity(LocalDateTime.now(), patient, calculatedBpm));
        }
    }

    private int calculateBpm(PatientEntity patient) {
        int age = Period.between(patient.getBirthDate(), LocalDate.now()).getYears();
        int baseBpm = getBaseBpmByAge(age);

        baseBpm -= (patient.getAthleticism() * 2);

        if (patient.isSmoker()) {
            baseBpm += 7;
        }

        switch (patient.getSleepQuality()) {
            case POOR:
                baseBpm += 5;
                break;
            case AVERAGE:
                baseBpm += 2;
                break;
            case GOOD:
                break;
            case EXCELLENT:
                baseBpm -= 2;
                break;
        }

        double activityMultiplier = 1.0;
        if (patient.getCurrentActivityState() == ActivityState.SLEEPING) {
            activityMultiplier = 0.85;
        } else if (patient.getCurrentActivityState() == ActivityState.TRAINING) {
            activityMultiplier = 1.8;
        }

        LocalTime now = LocalTime.now();
        if (now.isAfter(LocalTime.of(23, 0)) || now.isBefore(LocalTime.of(6, 0))) {
            if (patient.getCurrentActivityState() != ActivityState.TRAINING) {
                activityMultiplier -= 0.05;
            }
        }

        int finalBpm = (int) (baseBpm * activityMultiplier);
        finalBpm += ThreadLocalRandom.current().nextInt(7) - 3;

        return finalBpm;
    }

    private int getBaseBpmByAge(int age) {
        if (age < 1)
            return 130;
        if (age <= 3)
            return 110;
        if (age <= 5)
            return 100;
        if (age <= 12)
            return 90;
        if (age <= 17)
            return 80;
        return 75;
    }
}