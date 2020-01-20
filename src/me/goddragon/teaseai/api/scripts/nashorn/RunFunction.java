package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.scripts.ScriptHandler;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;



/**
 * Created by GodDragon on 10.04.2018.
 */
public class RunFunction extends CustomFunction {

    public RunFunction() {
        super("run");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        if (args.length == 1) {
            ScriptHandler.getHandler().evalScript(args[0].toString());
            return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
