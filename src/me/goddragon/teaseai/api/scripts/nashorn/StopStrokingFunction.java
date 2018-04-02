package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.session.StrokeHandler;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 02.04.2018.
 */
public class StopStrokingFunction extends CustomFunction {

    public StopStrokingFunction() {
        super("stopStroking");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        switch (args.length) {
            case 0:
                StrokeHandler.getHandler().stopMetronome();
                return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args: " + Arrays.asList(args).toString());
        return null;
    }
}
