package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.media.MediaHandler;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 31.03.2018.
 */
public class StopAudioFunction extends CustomFunction {

    public StopAudioFunction() {
        super("stopAudio");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        switch (args.length) {
            case 0:
                MediaHandler.getHandler().stopAllAudio();
                return null;
            case 1:
                MediaHandler.getHandler().stopAudio(args[0].toString());
                return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
