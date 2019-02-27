package me.goddragon.teaseai.utils;

public class MathUtils {

    public static boolean isBetweenExcluding(double i, double min, double max) {
        return i > min && i < max;
    }

    public static boolean isBetweenIncluding(double i, double min, double max) {
        return i >= min && i <= max;
    }

    public static Integer tryParseInteger(Object o) throws NumberFormatException {
        if(o instanceof Integer) {
            return (Integer) o;
        } else {
            return Integer.parseInt(o.toString());
        }
    }

    public static Double tryParseDouble(Object o) throws NumberFormatException {
        if(o instanceof Double) {
            return (Double) o;
        } else {
            return Double.parseDouble(o.toString());
        }
    }
}
