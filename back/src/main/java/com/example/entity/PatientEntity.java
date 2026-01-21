package com.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "patients")
public class PatientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    private double heightCm;
    private double weightKg;

    @Enumerated(EnumType.STRING)
    private Illness illness;

    @Enumerated(EnumType.STRING)
    private SleepQuality sleepQuality;

    @Enumerated(EnumType.STRING)
    private int athleticism;

    private boolean smoker;
    private boolean drinker;

    private int bpmMax;

}

