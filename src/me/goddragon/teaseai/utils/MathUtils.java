package me.goddragon.teaseai.utils;

public class MathUtils {

    public static boolean isBetweenExcluding(double i, double min, double max) {
        return i > min && i < max;
    }

    public static boolean isBetweenIncluding(double i, double min, double max) {
        return i >= min && i <= max;
    }
}
