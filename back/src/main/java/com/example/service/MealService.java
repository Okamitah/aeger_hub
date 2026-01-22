package com.example.service;

import com.example.entity.Illness;
import com.example.entity.Meal;
import com.example.entity.MealType;
import com.example.repository.MealRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MealService {

    private final MealRepository mealRepository;

    public MealService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    public List<Meal> getMealsForIllness(Illness illness) {
        return mealRepository.findBySuitableFor(illness);
    }

    public List<Meal> getMealsByType(MealType mealType) {
        return mealRepository.findByMealType(mealType);
    }

    public List<Meal> getMealsForIllnessAndType(Illness illness, MealType mealType) {
        return mealRepository.findByIllnessAndMealType(illness, mealType);
    }

    public Map<MealType, List<Meal>> getRecommendedMealsForIllness(Illness illness) {
        Map<MealType, List<Meal>> recommendations = new HashMap<>();
        
        for (MealType type : MealType.values()) {
            List<Meal> meals = getMealsForIllnessAndType(illness, type);
            if (type != MealType.SNACK && meals.size() > 3) {
                meals = meals.subList(0, 3);
            }
            recommendations.put(type, meals);
        }
        
        return recommendations;
    }

    public void initializeMealDatabase() {
        if (mealRepository.count() > 0) {
            return;
        }

        List<Meal> meals = new ArrayList<>();

        
        Meal oatmeal = new Meal("Oatmeal with Berries", 
            "Steel-cut oats topped with fresh blueberries and strawberries", 
            MealType.BREAKFAST, 320, 12, 54, 8, 10, 12);
        oatmeal.addSuitableIllness(Illness.HEALTHY);
        oatmeal.addSuitableIllness(Illness.DIABETES);
        oatmeal.addSuitableIllness(Illness.HYPERTENSION);
        oatmeal.addSuitableIllness(Illness.HEART_DISEASE);
        meals.add(oatmeal);

        Meal yogurt = new Meal("Greek Yogurt Parfait", 
            "Non-fat Greek yogurt with granola and mixed berries", 
            MealType.BREAKFAST, 280, 20, 35, 6, 5, 18);
        yogurt.addSuitableIllness(Illness.HEALTHY);
        yogurt.addSuitableIllness(Illness.HYPERTENSION);
        yogurt.addSuitableIllness(Illness.HEART_DISEASE);
        meals.add(yogurt);

        Meal omelet = new Meal("Vegetable Omelet", 
            "3-egg white omelet with spinach, tomatoes, and mushrooms", 
            MealType.BREAKFAST, 240, 25, 8, 10, 3, 3);
        omelet.addSuitableIllness(Illness.HEALTHY);
        omelet.addSuitableIllness(Illness.DIABETES);
        omelet.addSuitableIllness(Illness.HYPERTENSION);
        omelet.addSuitableIllness(Illness.HEART_DISEASE);
        meals.add(omelet);

        Meal avocadoToast = new Meal("Avocado Toast", 
            "Whole grain toast topped with mashed avocado and poached egg", 
            MealType.BREAKFAST, 350, 15, 30, 18, 8, 2);
        avocadoToast.addSuitableIllness(Illness.HEALTHY);
        avocadoToast.addSuitableIllness(Illness.DIABETES);
        avocadoToast.addSuitableIllness(Illness.HYPERTENSION);
        avocadoToast.addSuitableIllness(Illness.HEART_DISEASE);
        meals.add(avocadoToast);

        Meal smoothieBowl = new Meal("Berry Smoothie Bowl", 
            "Blended berries, banana, topped with nuts and seeds", 
            MealType.BREAKFAST, 380, 12, 62, 14, 9, 35);
        smoothieBowl.addSuitableIllness(Illness.HEALTHY);
        smoothieBowl.addSuitableIllness(Illness.HYPERTENSION);
        meals.add(smoothieBowl);


        Meal chickenSalad = new Meal("Grilled Chicken Salad", 
            "Mixed greens, grilled chicken breast, vegetables, olive oil dressing", 
            MealType.LUNCH, 420, 45, 25, 18, 6, 8);
        chickenSalad.addSuitableIllness(Illness.HEALTHY);
        chickenSalad.addSuitableIllness(Illness.DIABETES);
        chickenSalad.addSuitableIllness(Illness.HYPERTENSION);
        chickenSalad.addSuitableIllness(Illness.HEART_DISEASE);
        meals.add(chickenSalad);

        Meal buddhaBowl = new Meal("Quinoa Buddha Bowl", 
            "Quinoa, roasted vegetables, chickpeas, tahini dressing", 
            MealType.LUNCH, 480, 18, 68, 16, 12, 6);
        buddhaBowl.addSuitableIllness(Illness.HEALTHY);
        buddhaBowl.addSuitableIllness(Illness.DIABETES);
        buddhaBowl.addSuitableIllness(Illness.HYPERTENSION);
        buddhaBowl.addSuitableIllness(Illness.HEART_DISEASE);
        meals.add(buddhaBowl);

        Meal salmon = new Meal("Grilled Salmon with Brown Rice", 
            "Grilled salmon fillet, brown rice, steamed broccoli", 
            MealType.LUNCH, 520, 42, 48, 18, 6, 2);
        salmon.addSuitableIllness(Illness.HEALTHY);
        salmon.addSuitableIllness(Illness.DIABETES);
        salmon.addSuitableIllness(Illness.HYPERTENSION);
        salmon.addSuitableIllness(Illness.HEART_DISEASE);
        meals.add(salmon);

        Meal turkeyWrap = new Meal("Turkey & Veggie Wrap", 
            "Whole wheat wrap, lean turkey, lettuce, tomato, hummus", 
            MealType.LUNCH, 390, 28, 42, 12, 8, 4);
        turkeyWrap.addSuitableIllness(Illness.HEALTHY);
        turkeyWrap.addSuitableIllness(Illness.DIABETES);
        turkeyWrap.addSuitableIllness(Illness.HYPERTENSION);
        turkeyWrap.addSuitableIllness(Illness.HEART_DISEASE);
        meals.add(turkeyWrap);

        Meal lentilSoup = new Meal("Hearty Lentil Soup", 
            "Lentils, vegetables, herbs in low-sodium broth", 
            MealType.LUNCH, 340, 20, 55, 4, 16, 6);
        lentilSoup.addSuitableIllness(Illness.HEALTHY);
        lentilSoup.addSuitableIllness(Illness.DIABETES);
        lentilSoup.addSuitableIllness(Illness.HYPERTENSION);
        lentilSoup.addSuitableIllness(Illness.HEART_DISEASE);
        meals.add(lentilSoup);


        Meal bakedChicken = new Meal("Herb-Baked Chicken Breast", 
            "Seasoned chicken breast, roasted vegetables, quinoa", 
            MealType.DINNER, 480, 48, 38, 14, 7, 5);
        bakedChicken.addSuitableIllness(Illness.HEALTHY);
        bakedChicken.addSuitableIllness(Illness.DIABETES);
        bakedChicken.addSuitableIllness(Illness.HYPERTENSION);
        bakedChicken.addSuitableIllness(Illness.HEART_DISEASE);
        meals.add(bakedChicken);

        Meal stirFry = new Meal("Vegetable Tofu Stir-Fry", 
            "Tofu, mixed vegetables, ginger, garlic, brown rice", 
            MealType.DINNER, 420, 22, 58, 12, 8, 7);
        stirFry.addSuitableIllness(Illness.HEALTHY);
        stirFry.addSuitableIllness(Illness.DIABETES);
        stirFry.addSuitableIllness(Illness.HYPERTENSION);
        stirFry.addSuitableIllness(Illness.HEART_DISEASE);
        meals.add(stirFry);

        Meal fishTacos = new Meal("Grilled Fish Tacos", 
            "Grilled white fish, corn tortillas, cabbage slaw, salsa", 
            MealType.DINNER, 440, 35, 48, 14, 9, 6);
        fishTacos.addSuitableIllness(Illness.HEALTHY);
        fishTacos.addSuitableIllness(Illness.DIABETES);
        fishTacos.addSuitableIllness(Illness.HYPERTENSION);
        fishTacos.addSuitableIllness(Illness.HEART_DISEASE);
        meals.add(fishTacos);

        Meal turkeyMeatballs = new Meal("Turkey Meatballs with Zucchini Noodles", 
            "Lean turkey meatballs, spiralized zucchini, marinara sauce", 
            MealType.DINNER, 380, 38, 28, 12, 6, 8);
        turkeyMeatballs.addSuitableIllness(Illness.HEALTHY);
        turkeyMeatballs.addSuitableIllness(Illness.DIABETES);
        turkeyMeatballs.addSuitableIllness(Illness.HYPERTENSION);
        turkeyMeatballs.addSuitableIllness(Illness.HEART_DISEASE);
        meals.add(turkeyMeatballs);

        Meal shrimpVeggies = new Meal("Garlic Shrimp with Vegetables", 
            "Sautéed shrimp, bell peppers, asparagus, cauliflower rice", 
            MealType.DINNER, 320, 35, 22, 10, 5, 4);
        shrimpVeggies.addSuitableIllness(Illness.HEALTHY);
        shrimpVeggies.addSuitableIllness(Illness.DIABETES);
        shrimpVeggies.addSuitableIllness(Illness.HYPERTENSION);
        shrimpVeggies.addSuitableIllness(Illness.HEART_DISEASE);
        meals.add(shrimpVeggies);


        Meal appleAlmond = new Meal("Apple Slices with Almond Butter", 
            "Fresh apple slices with natural almond butter", 
            MealType.SNACK, 200, 6, 24, 10, 5, 16);
        appleAlmond.addSuitableIllness(Illness.HEALTHY);
        appleAlmond.addSuitableIllness(Illness.HYPERTENSION);
        appleAlmond.addSuitableIllness(Illness.HEART_DISEASE);
        meals.add(appleAlmond);

        Meal hummusVeggies = new Meal("Hummus with Veggie Sticks", 
            "Homemade hummus, carrot and cucumber sticks", 
            MealType.SNACK, 150, 6, 18, 6, 5, 3);
        hummusVeggies.addSuitableIllness(Illness.HEALTHY);
        hummusVeggies.addSuitableIllness(Illness.DIABETES);
        hummusVeggies.addSuitableIllness(Illness.HYPERTENSION);
        hummusVeggies.addSuitableIllness(Illness.HEART_DISEASE);
        meals.add(hummusVeggies);

        Meal mixedNuts = new Meal("Mixed Nuts", 
            "Unsalted almonds, walnuts, and cashews", 
            MealType.SNACK, 180, 6, 8, 16, 3, 2);
        mixedNuts.addSuitableIllness(Illness.HEALTHY);
        mixedNuts.addSuitableIllness(Illness.DIABETES);
        mixedNuts.addSuitableIllness(Illness.HEART_DISEASE);
        meals.add(mixedNuts);

        Meal cheeseCrackers = new Meal("Low-Fat Cheese with Whole Grain Crackers", 
            "Low-fat cheese cubes, whole grain crackers", 
            MealType.SNACK, 160, 10, 18, 6, 3, 2);
        cheeseCrackers.addSuitableIllness(Illness.HEALTHY);
        cheeseCrackers.addSuitableIllness(Illness.DIABETES);
        cheeseCrackers.addSuitableIllness(Illness.HYPERTENSION);
        cheeseCrackers.addSuitableIllness(Illness.HEART_DISEASE);
        meals.add(cheeseCrackers);

        Meal hardBoiledEggs = new Meal("Hard Boiled Eggs", 
            "Two hard boiled eggs", 
            MealType.SNACK, 140, 12, 2, 10, 0, 0);
        hardBoiledEggs.addSuitableIllness(Illness.HEALTHY);
        hardBoiledEggs.addSuitableIllness(Illness.DIABETES);
        hardBoiledEggs.addSuitableIllness(Illness.HYPERTENSION);
        hardBoiledEggs.addSuitableIllness(Illness.HEART_DISEASE);
        meals.add(hardBoiledEggs);

        mealRepository.saveAll(meals);
    }
}
