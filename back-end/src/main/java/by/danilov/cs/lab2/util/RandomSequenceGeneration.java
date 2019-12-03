package by.danilov.cs.lab2.util;

import org.apache.commons.lang3.RandomStringUtils;

public final class RandomSequenceGeneration {

    private RandomSequenceGeneration() {

    }

    public static String generateRandomRequestKey() {
        int length = 16;
        return RandomStringUtils.random(length, true, true);
    }

    public static String generateRandomNumber(int length) {
        return RandomStringUtils.random(length, false, true);
    }

}
