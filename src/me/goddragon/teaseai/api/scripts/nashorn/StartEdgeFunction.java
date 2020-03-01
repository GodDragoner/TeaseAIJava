package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.chat.response.ResponseHandler;
import me.goddragon.teaseai.api.session.StrokeHandler;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 05.04.2018.
 */
public class StartEdgeFunction extends CustomFunction {

    public StartEdgeFunction() {
        super("startEdge", "startEdging");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        switch (args.length) {
            case 0:
                StrokeHandler.getHandler().setEdging(true);
                StrokeHandler.getHandler().setEdgeHold(false);
                ResponseHandler.getHandler().registerResponse(StrokeHandler.EDGE_RESPONSE);
                return null;
            case 1:
                StrokeHandler.getHandler().startEdging((int)args[0]);
                StrokeHandler.getHandler().setEdgeHold(false);
                return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
