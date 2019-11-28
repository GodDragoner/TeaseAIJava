package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.session.StrokeHandler;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 02.04.2018.
 */
public class StartStrokingFunction extends CustomFunction {

    public StartStrokingFunction() {
        super("startStroking", "startMetronome");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        switch (args.length) {
            case 1:
                if (args[0] instanceof Integer) {
                    StrokeHandler.getHandler().startStroking((Integer) args[0], 0);
                    return null;
                }
                break;
            case 2:
                if (args[0] instanceof Integer && args[1] instanceof Integer) {
                    StrokeHandler.getHandler().startStroking((Integer) args[0], (Integer) args[1]);
                    return null;
                }
                break;
            case 0:
                TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method without parameters.");
                return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
