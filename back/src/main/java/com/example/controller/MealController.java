package com.example.controller;

import com.example.entity.Illness;
import com.example.entity.Meal;
import com.example.entity.MealType;
import com.example.service.MealService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/meals")
@CrossOrigin(origins = "*")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @GetMapping("/recommendations/{illness}")
    public Map<MealType, List<Meal>> getRecommendations(@PathVariable Illness illness) {
        return mealService.getRecommendedMealsForIllness(illness);
    }

    @GetMapping("/by-illness/{illness}")
    public List<Meal> getMealsByIllness(@PathVariable Illness illness) {
        return mealService.getMealsForIllness(illness);
    }

    @GetMapping("/by-type/{mealType}")
    public List<Meal> getMealsByType(@PathVariable MealType mealType) {
        return mealService.getMealsByType(mealType);
    }

    @GetMapping("/by-illness-and-type/{illness}/{mealType}")
    public List<Meal> getMealsByIllnessAndType(
            @PathVariable Illness illness, 
            @PathVariable MealType mealType) {
        return mealService.getMealsForIllnessAndType(illness, mealType);
    }

    @PostMapping("/initialize")
    public String initializeMeals() {
        mealService.initializeMealDatabase();
        return "Meal database initialized successfully!";
    }
}
