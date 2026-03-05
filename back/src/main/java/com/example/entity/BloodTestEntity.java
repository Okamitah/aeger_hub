package com.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blood_tests")
public class BloodTestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientEntity patient;

    private LocalDateTime takenAt;

    private Double hemoglobinGdl;

    private Double wbcThousandsPerUl;

    private Double plateletsThousandsPerUl;

    private Double crpMgL;

    private Double creatinineMgdl;

    private Double astUL;

    private Double altUL;

    private Double ggtUL;

    private Double sodiumMeqL;

    private Double potassiumMeqL;

    private Double fastingGlucoseMgdl;

    private Double totalCholesterolMgdl;

    public BloodTestEntity() {}

    public BloodTestEntity(PatientEntity patient, LocalDateTime takenAt,
                           Double hemoglobinGdl, Double wbcThousandsPerUl, Double plateletsThousandsPerUl,
                           Double crpMgL, Double creatinineMgdl, Double astUL, Double altUL,
                           Double ggtUL, Double sodiumMeqL, Double potassiumMeqL,
                           Double fastingGlucoseMgdl, Double totalCholesterolMgdl) {
        this.patient = patient;
        this.takenAt = takenAt;
        this.hemoglobinGdl = hemoglobinGdl;
        this.wbcThousandsPerUl = wbcThousandsPerUl;
        this.plateletsThousandsPerUl = plateletsThousandsPerUl;
        this.crpMgL = crpMgL;
        this.creatinineMgdl = creatinineMgdl;
        this.astUL = astUL;
        this.altUL = altUL;
        this.ggtUL = ggtUL;
        this.sodiumMeqL = sodiumMeqL;
        this.potassiumMeqL = potassiumMeqL;
        this.fastingGlucoseMgdl = fastingGlucoseMgdl;
        this.totalCholesterolMgdl = totalCholesterolMgdl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PatientEntity getPatient() { return patient; }
    public void setPatient(PatientEntity patient) { this.patient = patient; }

    public LocalDateTime getTakenAt() { return takenAt; }
    public void setTakenAt(LocalDateTime takenAt) { this.takenAt = takenAt; }

    public Double getHemoglobinGdl() { return hemoglobinGdl; }
    public void setHemoglobinGdl(Double hemoglobinGdl) { this.hemoglobinGdl = hemoglobinGdl; }

    public Double getWbcThousandsPerUl() { return wbcThousandsPerUl; }
    public void setWbcThousandsPerUl(Double wbcThousandsPerUl) { this.wbcThousandsPerUl = wbcThousandsPerUl; }

    public Double getPlateletsThousandsPerUl() { return plateletsThousandsPerUl; }
    public void setPlateletsThousandsPerUl(Double plateletsThousandsPerUl) { this.plateletsThousandsPerUl = plateletsThousandsPerUl; }

    public Double getCrpMgL() { return crpMgL; }
    public void setCrpMgL(Double crpMgL) { this.crpMgL = crpMgL; }

    public Double getCreatinineMgdl() { return creatinineMgdl; }
    public void setCreatinineMgdl(Double creatinineMgdl) { this.creatinineMgdl = creatinineMgdl; }

    public Double getAstUL() { return astUL; }
    public void setAstUL(Double astUL) { this.astUL = astUL; }

    public Double getAltUL() { return altUL; }
    public void setAltUL(Double altUL) { this.altUL = altUL; }

    public Double getGgtUL() { return ggtUL; }
    public void setGgtUL(Double ggtUL) { this.ggtUL = ggtUL; }

    public Double getSodiumMeqL() { return sodiumMeqL; }
    public void setSodiumMeqL(Double sodiumMeqL) { this.sodiumMeqL = sodiumMeqL; }

    public Double getPotassiumMeqL() { return potassiumMeqL; }
    public void setPotassiumMeqL(Double potassiumMeqL) { this.potassiumMeqL = potassiumMeqL; }

    public Double getFastingGlucoseMgdl() { return fastingGlucoseMgdl; }
    public void setFastingGlucoseMgdl(Double fastingGlucoseMgdl) { this.fastingGlucoseMgdl = fastingGlucoseMgdl; }

    public Double getTotalCholesterolMgdl() { return totalCholesterolMgdl; }
    public void setTotalCholesterolMgdl(Double totalCholesterolMgdl) { this.totalCholesterolMgdl = totalCholesterolMgdl; }
}