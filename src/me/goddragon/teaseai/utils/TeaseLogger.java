package me.goddragon.teaseai.utils;

import java.util.logging.Level;

/**
 * Created by GodDragon on 24.03.2018.
 */
public class TeaseLogger {

    private static TeaseLogger logger = new TeaseLogger();

    public void log(Level level, String message) {
        log(level, message, level == Level.SEVERE);
    }

    public void log(Level level, String message, Exception e) {
        System.out.println(message);
    }

    public void log(Level level, String message, boolean stacktrace) {
        System.out.println(message);
    }

    public static TeaseLogger getLogger() {
        return logger;
    }

    public static void setLogger(TeaseLogger logger) {
        TeaseLogger.logger = logger;
    }
}
