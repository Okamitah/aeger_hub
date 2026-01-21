package com.example.entity;

public enum AthleticismLevel {
    SEDENTARY(0),
    LOW(1),
    MODERATE(2),
    ACTIVE(3),
    VERY_ACTIVE(4);

    private final int level;

    AthleticismLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
