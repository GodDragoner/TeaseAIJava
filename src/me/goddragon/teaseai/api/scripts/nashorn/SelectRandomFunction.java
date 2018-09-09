package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.utils.RandomUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.logging.Level;

/**
 * Created by GodDragon on 06.09.2018.
 */
public class SelectRandomFunction extends CustomFunction {

    public SelectRandomFunction() {
        super("selectRandom", "random");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        if(args.length > 0) {
            if(args.length == 1) {
                return args[0];
            }

            return args[RandomUtils.randInt(0, args.length - 1)];
        } else {
            TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method without parameters.");
            return null;
        }
    }
}
