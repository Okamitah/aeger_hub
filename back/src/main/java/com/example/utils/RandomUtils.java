package com.example.util;

import java.util.SplittableRandom;

public final class RandomUtils {

    private RandomUtils() {}

    public static <T> T pick(T[] values, SplittableRandom rng) {
        return values[rng.nextInt(values.length)];
    }
}

