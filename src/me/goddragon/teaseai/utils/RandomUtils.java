package me.goddragon.teaseai.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by GodDragon on 23.03.2018.
 */
public class RandomUtils {

    public static int randInt(int min, int max) {
        if (min == max || min > max) {
            return min;
        }
        
        //This is a better way to do random than using a list with shuffle
        return new Random().nextInt(max - min + 1) + min;
    }

    public static Object getWinner(HashMap<?, ? extends Number> idChances) {
        if(idChances.isEmpty()) {
            return null;
        }

        double maxChance = 0;

        for(Number chance : idChances.values()) {
            if(chance instanceof Double) {
                maxChance += (Double) chance;
            } else if(chance instanceof Integer) {
                maxChance += (Integer) chance;
            }
        }

        return getWinner(idChances, maxChance);
    }



    public static Object getWinner(HashMap<?, ? extends Number> idChances, double maxChance) {
        double randInt = ThreadLocalRandom.current().nextDouble(0, maxChance);

        double lastMax = 0;
        for(Map.Entry<?, ? extends Number> mapEntry : idChances.entrySet()) {
            if(mapEntry.getValue() instanceof Double) {
                lastMax += (Double) mapEntry.getValue();
            } else if(mapEntry.getValue() instanceof Integer) {
                lastMax += (Integer) mapEntry.getValue();
            }

            if(randInt <= lastMax)
                return mapEntry.getKey();

        }
        //No Winner found
        return null;
    }
}
