package com.example.service;

import com.example.entity.PatientEntity;
import com.example.repository.PatientRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TrackingSchedulerService {

    private final PatientRepository patientRepository;

    public TrackingSchedulerService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void runDailyTrackingReview() {
        List<PatientEntity> patients = patientRepository.findAll();
        LocalDate today = LocalDate.now();

        for (PatientEntity patient : patients) {
            if (patient.isTrackingEnabled()) {
                handleActiveTracking(patient, today);
            } else {
                handleInactiveTracking(patient, today);
            }
        }

        patientRepository.saveAll(patients);
    }

    private void handleActiveTracking(PatientEntity patient, LocalDate today) {
        int continuousDays = TrackingPolicy.continuousTrackingDays(patient);

        if (continuousDays == 0) {
            disableTracking(patient, today);
            return;
        }

        LocalDate enabledSince = patient.getTrackingEnabledSince();
        if (enabledSince == null) {
            patient.setTrackingEnabledSince(today);
            return;
        }

        long daysTracked = enabledSince.until(today).getDays();
        if (daysTracked >= continuousDays) {
            disableTracking(patient, today);
        }
    }

    private void handleInactiveTracking(PatientEntity patient, LocalDate today) {
        LocalDate nextCheck = patient.getNextPeriodicCheck();

        if (nextCheck == null || !today.isBefore(nextCheck)) {
            enableTracking(patient, today);
        }
    }

    private void enableTracking(PatientEntity patient, LocalDate today) {
        patient.setTrackingEnabled(true);
        patient.setTrackingEnabledSince(today);
        int duration = TrackingPolicy.periodicCheckDurationDays(patient);
        int interval = TrackingPolicy.periodicCheckIntervalDays(patient);
        patient.setNextPeriodicCheck(today.plusDays(duration + interval));

        System.out.println("[TrackingScheduler] ENABLED  -> " + patient.getName()
                + " | illness=" + patient.getIllness()
                + " | duration=" + duration + "d"
                + " | next check in " + (duration + interval) + "d");
    }

    private void disableTracking(PatientEntity patient, LocalDate today) {
        patient.setTrackingEnabled(false);
        patient.setTrackingEnabledSince(null);

        if (patient.getNextPeriodicCheck() == null) {
            int interval = TrackingPolicy.periodicCheckIntervalDays(patient);
            patient.setNextPeriodicCheck(today.plusDays(interval));
        }

        System.out.println("[TrackingScheduler] DISABLED -> " + patient.getName()
                + " | illness=" + patient.getIllness()
                + " | next check=" + patient.getNextPeriodicCheck());
    }
}