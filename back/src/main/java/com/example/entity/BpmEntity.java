package com.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bpm_measurements")
public class BpmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientEntity patient;

    private int value;

    public BpmEntity() {}

    public BpmEntity(LocalDateTime timestamp, PatientEntity patient, int value) {
        this.timestamp = timestamp;
        this.patient = patient;
        this.value = value;
    }

    public Long getId() { return id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public PatientEntity getPatient() { return patient; }
    public int getValue() { return value; }
}