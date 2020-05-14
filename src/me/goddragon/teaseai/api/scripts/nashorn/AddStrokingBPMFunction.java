package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.session.StrokeHandler;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 02.04.2018.
 */
public class AddStrokingBPMFunction extends CustomFunction {

    public AddStrokingBPMFunction() {
        super("addStrokingBPM", "addMetronomeBPM");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        switch (args.length) {
            case 1:
                if (args[0] instanceof Number) {
                    if (StrokeHandler.getHandler().isStroking()) {
                        StrokeHandler.getHandler().startMetronome(StrokeHandler.getHandler().getCurrentBPM() + ((Number)args[0]).intValue(), 0);
                    } else {
                        TeaseLogger.getLogger().log(Level.SEVERE, "Tried to add " + args[0] + " stroking bpm but sub was not stroking.");
                    }
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
