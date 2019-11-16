package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Created by GodDragon on 14.04.2018.
 */
public class WaitFunction extends CustomFunction {

    public WaitFunction() {
        super("wait");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        if (args.length <= 0) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method without parameters.");
            return null;
        }

        if (args[0] instanceof Integer || args[0] instanceof Double || args[0] instanceof Long) {
            if (args.length == 1) {
                TeaseAI.application.waitPossibleScripThread(Math.round(1000L * Double.valueOf(args[0].toString())));
                return null;
            } else {
                TimeUnit timeUnit = null;
                if (args[1] instanceof TimeUnit) {
                    timeUnit = (TimeUnit) args[1];
                } else {
                    try {
                        timeUnit = TimeUnit.valueOf(args[1].toString());
                    } catch (IllegalArgumentException ex) {
                        TeaseLogger.getLogger().log(Level.SEVERE, args[1] + " is not a valid time unit.");
                    }
                }

                if (timeUnit != null) {
                    TeaseAI.application.waitPossibleScripThread(timeUnit.toMillis(Math.round(Double.valueOf(args[0].toString()))));
                    return null;
                }
            }
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
