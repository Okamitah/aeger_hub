package com.example.generator;

import java.util.SplittableRandom;

public class NameGenerator {
    
    private static final String[] FIRST_NAMES = {
        "Emma", "Liam", "Olivia", "Noah", "Ava", "Ethan", "Sophia", "Mason",
        "Isabella", "William", "Mia", "James", "Charlotte", "Benjamin", "Amelia",
        "Lucas", "Harper", "Henry", "Evelyn", "Alexander", "Abigail", "Michael",
        "Emily", "Daniel", "Elizabeth", "Matthew", "Sofia", "Jackson", "Avery",
        "Sebastian", "Ella", "Jack", "Scarlett", "Aiden", "Grace", "Owen", "Chloe",
        "Samuel", "Victoria", "Joseph", "Riley", "John", "Aria", "David", "Lily",
        "Wyatt", "Aubrey", "Carter", "Zoey", "Julian", "Omar", "Mohamed", "Mamadou", 
        "Mehdi", "Bilal", "Hatim", "Ismail"
    };
    
    private static final String[] LAST_NAMES = {
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller",
        "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez",
        "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin",
        "Lee", "Perez", "Thompson", "White", "Harris", "Sanchez", "Clark",
        "Ramirez", "Lewis", "Robinson", "Walker", "Young", "Allen", "King",
        "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores", "Green",
        "Adams", "Nelson", "Baker", "Hall", "Rivera", "Campbell", "Mitchell",
        "Carter", "Roberts"
    };
    
    public static String randomName(SplittableRandom rng) {
        String firstName = FIRST_NAMES[rng.nextInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[rng.nextInt(LAST_NAMES.length)];
        return firstName + " " + lastName;
    }
}
