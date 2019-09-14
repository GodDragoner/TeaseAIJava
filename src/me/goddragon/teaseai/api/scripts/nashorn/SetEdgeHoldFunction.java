package me.goddragon.teaseai.api.scripts.nashorn;

import java.util.Arrays;
import java.util.logging.Level;

import me.goddragon.teaseai.api.session.StrokeHandler;
import me.goddragon.teaseai.utils.TeaseLogger;

public class SetEdgeHoldFunction extends CustomFunction
{
    public SetEdgeHoldFunction() {
        super("setEdgeHold", "setEdgeHolding");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        switch (args.length) {
            case 0:
                StrokeHandler.getHandler().setEdgeHold(true);
                return null;
            case 1:
                StrokeHandler.getHandler().setEdgeHold((boolean)args[0]);
                return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
