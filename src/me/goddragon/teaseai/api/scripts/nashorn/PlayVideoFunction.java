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
public class PlayVideoFunction extends CustomFunction {

    public PlayVideoFunction() {
        super("playVideo", "showVideo", "displayVideo");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        switch(args.length) {
            case 1:
                if(args[0] instanceof String) {
                    File file = FileUtils.getRandomMatchingFile(args[0].toString());
                    if(file == null) {
                        TeaseLogger.getLogger().log(Level.SEVERE, "Matching video file for path " + args[0] + " does not exist.");
                        return null;
                    }

                    return MediaHandler.getHandler().playVideo(file);
                }
                break;
            case 2:
                if(args[1] instanceof Boolean) {
                    File file = FileUtils.getRandomMatchingFile(args[0].toString());
                    if(file == null) {
                        TeaseLogger.getLogger().log(Level.SEVERE, "Matching video file for path " + args[0] + " does not exist.");
                        return null;
                    }

                    return MediaHandler.getHandler().playVideo(file, (Boolean) args[1]);
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
