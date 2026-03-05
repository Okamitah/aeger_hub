package com.example.service;

import com.example.entity.Illness;
import com.example.entity.PatientEntity;
import com.example.entity.SleepQuality;

import java.time.LocalDate;
import java.time.Period;

public class TrackingPolicy {

    public static int continuousTrackingDays(PatientEntity p) {
        int score = 0;

        switch (p.getIllness()) {
            case HEART_DISEASE:
                score += 40;
                break;
            case HYPERTENSION:
                score += 30;
                break;
            case DIABETES:
                score += 20;
                break;
            case ASTHMA:
                score += 15;
                break;
            case HEALTHY:
                score += 0;
                break;
        }

        int age = Period.between(p.getBirthDate(), LocalDate.now()).getYears();
        if (age >= 65)
            score += 15;
        else if (age >= 50)
            score += 8;
        else if (age >= 40)
            score += 4;

        if (p.isSmoker())
            score += 10;
        if (p.isDrinker())
            score += 5;

        if (p.getSleepQuality() == SleepQuality.POOR)
            score += 8;
        else if (p.getSleepQuality() == SleepQuality.AVERAGE)
            score += 4;

        int athleticism = p.getAthleticism();
        if (athleticism == 0)
            score += 5;
        else if (athleticism >= 3)
            score -= 5;

        score = Math.max(score, 0);

        if (score >= 60)
            return 14;
        if (score >= 40)
            return 7;
        if (score >= 20)
            return 3;
        if (score >= 10)
            return 1;
        return 0;
    }

    public static int periodicCheckIntervalDays(PatientEntity p) {
        switch (p.getIllness()) {
            case HEART_DISEASE:
                return 3;
            case HYPERTENSION:
                return 5;
            case DIABETES:
                return 7;
            case ASTHMA:
                return 10;
            case HEALTHY:
            default:
                int age = Period.between(p.getBirthDate(), LocalDate.now()).getYears();
                if (age >= 65)
                    return 7;
                if (p.isSmoker() || p.isDrinker())
                    return 10;
                return 14;
        }
    }

    public static int periodicCheckDurationDays(PatientEntity p) {
        switch (p.getIllness()) {
            case HEART_DISEASE:
                return 2;
            case HYPERTENSION:
                return 1;
            case DIABETES:
                return 1;
            default:
                return 1;
        }
    }

    public static boolean requiresContinuousTracking(PatientEntity p) {
        return continuousTrackingDays(p) > 0;
    }
}