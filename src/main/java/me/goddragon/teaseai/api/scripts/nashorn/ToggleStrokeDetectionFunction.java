package me.goddragon.teaseai.api.scripts.nashorn;

import java.util.Arrays;
import java.util.logging.Level;

import me.goddragon.teaseai.api.statistics.StatisticsManager;
import me.goddragon.teaseai.utils.TeaseLogger;

public class ToggleStrokeDetectionFunction extends CustomFunction
{
    public ToggleStrokeDetectionFunction() {
        super("toggleStrokeDetection", "enableStrokeDetection");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        if (args.length >= 1 && args[0] instanceof Boolean)
        {
            StatisticsManager.toggleStrokeDetection((boolean)args[0]);
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
