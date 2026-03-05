package com.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "aliments")
public class AlimentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;

    private Double calories;
    private Double proteinG;
    private Double carbohydratesG;
    private Double fatG;

    private Double fiberG;
    private Double sugarG;
    private Double sodiumMg;
    private Double cholesterolMg;

    private Double vitaminAUg;
    private Double vitaminB1Mg; 
    private Double vitaminB2Mg;
    private Double vitaminB3Mg;
    private Double vitaminB6Mg;
    private Double vitaminB9Ug;
    private Double vitaminB12Ug;
    private Double vitaminCMg;
    private Double vitaminDUg;
    private Double vitaminEMg;
    private Double vitaminKUg;

    private Double calciumMg;
    private Double ironMg;
    private Double magnesiumMg;
    private Double phosphorusMg;
    private Double potassiumMg;
    private Double zincMg;

    private String offId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories; }

    public Double getProteinG() { return proteinG; }
    public void setProteinG(Double proteinG) { this.proteinG = proteinG; }

    public Double getCarbohydratesG() { return carbohydratesG; }
    public void setCarbohydratesG(Double carbohydratesG) { this.carbohydratesG = carbohydratesG; }

    public Double getFatG() { return fatG; }
    public void setFatG(Double fatG) { this.fatG = fatG; }

    public Double getFiberG() { return fiberG; }
    public void setFiberG(Double fiberG) { this.fiberG = fiberG; }

    public Double getSugarG() { return sugarG; }
    public void setSugarG(Double sugarG) { this.sugarG = sugarG; }

    public Double getSodiumMg() { return sodiumMg; }
    public void setSodiumMg(Double sodiumMg) { this.sodiumMg = sodiumMg; }

    public Double getCholesterolMg() { return cholesterolMg; }
    public void setCholesterolMg(Double cholesterolMg) { this.cholesterolMg = cholesterolMg; }

    public Double getVitaminAUg() { return vitaminAUg; }
    public void setVitaminAUg(Double vitaminAUg) { this.vitaminAUg = vitaminAUg; }

    public Double getVitaminB1Mg() { return vitaminB1Mg; }
    public void setVitaminB1Mg(Double vitaminB1Mg) { this.vitaminB1Mg = vitaminB1Mg; }

    public Double getVitaminB2Mg() { return vitaminB2Mg; }
    public void setVitaminB2Mg(Double vitaminB2Mg) { this.vitaminB2Mg = vitaminB2Mg; }

    public Double getVitaminB3Mg() { return vitaminB3Mg; }
    public void setVitaminB3Mg(Double vitaminB3Mg) { this.vitaminB3Mg = vitaminB3Mg; }

    public Double getVitaminB6Mg() { return vitaminB6Mg; }
    public void setVitaminB6Mg(Double vitaminB6Mg) { this.vitaminB6Mg = vitaminB6Mg; }

    public Double getVitaminB9Ug() { return vitaminB9Ug; }
    public void setVitaminB9Ug(Double vitaminB9Ug) { this.vitaminB9Ug = vitaminB9Ug; }

    public Double getVitaminB12Ug() { return vitaminB12Ug; }
    public void setVitaminB12Ug(Double vitaminB12Ug) { this.vitaminB12Ug = vitaminB12Ug; }

    public Double getVitaminCMg() { return vitaminCMg; }
    public void setVitaminCMg(Double vitaminCMg) { this.vitaminCMg = vitaminCMg; }

    public Double getVitaminDUg() { return vitaminDUg; }
    public void setVitaminDUg(Double vitaminDUg) { this.vitaminDUg = vitaminDUg; }

    public Double getVitaminEMg() { return vitaminEMg; }
    public void setVitaminEMg(Double vitaminEMg) { this.vitaminEMg = vitaminEMg; }

    public Double getVitaminKUg() { return vitaminKUg; }
    public void setVitaminKUg(Double vitaminKUg) { this.vitaminKUg = vitaminKUg; }

    public Double getCalciumMg() { return calciumMg; }
    public void setCalciumMg(Double calciumMg) { this.calciumMg = calciumMg; }

    public Double getIronMg() { return ironMg; }
    public void setIronMg(Double ironMg) { this.ironMg = ironMg; }

    public Double getMagnesiumMg() { return magnesiumMg; }
    public void setMagnesiumMg(Double magnesiumMg) { this.magnesiumMg = magnesiumMg; }

    public Double getPhosphorusMg() { return phosphorusMg; }
    public void setPhosphorusMg(Double phosphorusMg) { this.phosphorusMg = phosphorusMg; }

    public Double getPotassiumMg() { return potassiumMg; }
    public void setPotassiumMg(Double potassiumMg) { this.potassiumMg = potassiumMg; }

    public Double getZincMg() { return zincMg; }
    public void setZincMg(Double zincMg) { this.zincMg = zincMg; }

    public String getOffId() { return offId; }
    public void setOffId(String offId) { this.offId = offId; }
}
