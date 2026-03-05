package com.example.repository;

import com.example.entity.Illness;
import com.example.entity.Meal;
import com.example.entity.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Long> {

    List<Meal> findByMealType(MealType mealType);

    @Query("SELECT m FROM Meal m JOIN m.suitableFor i WHERE i = :illness")
    List<Meal> findBySuitableFor(@Param("illness") Illness illness);

    @Query("SELECT m FROM Meal m JOIN m.suitableFor i WHERE i = :illness AND m.mealType = :mealType")
    List<Meal> findByIllnessAndMealType(@Param("illness") Illness illness, @Param("mealType") MealType mealType);
}
