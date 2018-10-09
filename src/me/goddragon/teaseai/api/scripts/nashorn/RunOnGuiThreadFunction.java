package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 09.10.2018.
 */
public class RunOnGuiThreadFunction extends CustomFunction {

    public RunOnGuiThreadFunction() {
        super("runGui", "runGUI", "runOnGUI", "runOnGui", "runOnGUIThread", "runOnGuiThread");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        if(args.length >= 1) {
            if(args[0] instanceof Runnable) {
                TeaseAI.application.runOnUIThread((Runnable) args[0]);
                return null;
            }
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
