package com.example.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "meals")
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    private MealType mealType;
    
    private int calories;
    
    private double protein;  // in grams
    private double carbs;    // in grams
    private double fats;     // in grams
    private double fiber;    // in grams
    private double sugar;    // in grams
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "meal_suitable_for", joinColumns = @JoinColumn(name = "meal_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "illness")
    private Set<Illness> suitableFor = new HashSet<>();

    public Meal() {}

    public Meal(String name, String description, MealType mealType, int calories, 
                double protein, double carbs, double fats, double fiber, double sugar) {
        this.name = name;
        this.description = description;
        this.mealType = mealType;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
        this.fiber = fiber;
        this.sugar = sugar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getFats() {
        return fats;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public double getFiber() {
        return fiber;
    }

    public void setFiber(double fiber) {
        this.fiber = fiber;
    }

    public double getSugar() {
        return sugar;
    }

    public void setSugar(double sugar) {
        this.sugar = sugar;
    }

    public Set<Illness> getSuitableFor() {
        return suitableFor;
    }

    public void setSuitableFor(Set<Illness> suitableFor) {
        this.suitableFor = suitableFor;
    }

    public void addSuitableIllness(Illness illness) {
        this.suitableFor.add(illness);
    }
}
