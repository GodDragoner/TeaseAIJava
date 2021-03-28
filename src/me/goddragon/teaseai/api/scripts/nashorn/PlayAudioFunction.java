package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.media.MediaHandler;
import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class PlayAudioFunction extends CustomFunction {

    public PlayAudioFunction() {
        super("playAudio", "playSound");
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
                File file = FileUtils.getRandomMatchingFile(args[0].toString());
                if (file == null) {
                    TeaseLogger.getLogger().log(Level.SEVERE, "Matching audio file for path " + args[0] + " does not exist.");
                    return null;
                }

                MediaHandler.getHandler().playAudio(file);
                return null;
            case 2:
                if (args[1] instanceof Boolean) {
                    file = FileUtils.getRandomMatchingFile(args[0].toString());
                    if (file == null) {
                        TeaseLogger.getLogger().log(Level.SEVERE, "Matching audio file for path " + args[0] + " does not exist.");
                        return null;
                    }

                    MediaHandler.getHandler().playAudio(file, (Boolean) args[1]);
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
