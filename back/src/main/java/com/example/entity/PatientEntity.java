package com.example.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "patients")
public class PatientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    private String name;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    private double heightCm;
    private double weightKg;

    @Enumerated(EnumType.STRING)
    private Illness illness;

    @Enumerated(EnumType.STRING)
    private SleepQuality sleepQuality;

    @Enumerated(EnumType.STRING)
    private ActivityState currentActivityState = ActivityState.NORMAL; 

    private int athleticism;
    private boolean smoker;
    private boolean drinker;

    private int bpmMax;
    private boolean trackingEnabled;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public Sex getSex() { return sex; }
    public void setSex(Sex sex) { this.sex = sex; }
    public double getHeightCm() { return heightCm; }
    public void setHeightCm(double heightCm) { this.heightCm = heightCm; }
    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
    public Illness getIllness() { return illness; }
    public void setIllness(Illness illness) { this.illness = illness; }
    public SleepQuality getSleepQuality() { return sleepQuality; }
    public void setSleepQuality(SleepQuality sleepQuality) { this.sleepQuality = sleepQuality; }
    public ActivityState getCurrentActivityState() { return currentActivityState; }
    public void setCurrentActivityState(ActivityState currentActivityState) { this.currentActivityState = currentActivityState; }
    public int getAthleticism() { return athleticism; }
    public void setAthleticism(int athleticism) { this.athleticism = athleticism; }
    public boolean isSmoker() { return smoker; }
    public void setSmoker(boolean smoker) { this.smoker = smoker; }
    public boolean isDrinker() { return drinker; }
    public void setDrinker(boolean drinker) { this.drinker = drinker; }
    public int getBpmMax() { return bpmMax; }
    public void setBpmMax(int bpmMax) { this.bpmMax = bpmMax; }
    public boolean isTrackingEnabled() { return trackingEnabled; }
    public void setTrackingEnabled(boolean trackingEnabled) { this.trackingEnabled = trackingEnabled; }
}
