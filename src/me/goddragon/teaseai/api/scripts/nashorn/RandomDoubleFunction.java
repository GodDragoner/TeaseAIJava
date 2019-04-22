package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

/**
 * Created by GodDragon on 06.09.2018.
 */
public class RandomDoubleFunction extends CustomFunction {

    public RandomDoubleFunction() {
        super("randomDouble");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        switch (args.length) {
            case 2:
                if (args[0] instanceof Number && args[1] instanceof Number) {
                    if (args[0] == args[1]) {
                        return args[0];
                    }

                    // nextInt is normally exclusive of the top value,
                    // So add 1 to make it inclusive
                    return ThreadLocalRandom.current().nextDouble((Double) args[0], (Double) args[1] + 1);
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
