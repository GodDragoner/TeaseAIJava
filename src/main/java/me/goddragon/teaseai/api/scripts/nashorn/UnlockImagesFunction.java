package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.media.MediaHandler;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 28.03.2018.
 */
public class UnlockImagesFunction extends CustomFunction {

    public UnlockImagesFunction() {
        super("unlockImages");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        if (args.length == 0) {
            MediaHandler.getHandler().setImagesLocked(false);
            return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " does not support any arguments. Given arguments were:" + Arrays.asList(args).toString());
        return null;
    }
}
