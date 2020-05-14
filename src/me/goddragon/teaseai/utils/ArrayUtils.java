package me.goddragon.teaseai.utils;

import java.util.List;
import java.util.stream.Collectors;

public class ArrayUtils {

    public static Object convertToFittingType(List<String> arrayList) {
        List<Integer> integerList = arrayList.stream()
                .map(object -> {
                    try {
                        return Integer.parseInt(object);
                    } catch (NumberFormatException ex) {
                    }

                    return null;
                }).collect(Collectors.toList());

        if (!integerList.contains(null)) {
            return integerList;
        }

        List<Double> doubleList = arrayList.stream()
                .map(object -> {
                    try {
                        return Double.parseDouble(object);
                    } catch (NumberFormatException ex) {
                    }

                    return null;
                }).collect(Collectors.toList());

        if (!doubleList.contains(null)) {
            return doubleList;
        }

        return arrayList;
    }
}
