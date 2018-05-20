package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.media.MediaHandler;
import me.goddragon.teaseai.api.media.MediaURL;
import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class ShowImageFunction extends CustomFunction {

    public ShowImageFunction() {
        super("showImage", "showPicture", "displayImage", "displayPicture");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        File file = null;
        if (args.length > 0) {
            if (args[0] instanceof String) {
                file = FileUtils.getRandomMatchingFile(args[0].toString());
                if (file == null) {
                    TeaseLogger.getLogger().log(Level.SEVERE, "Matching image file for path " + args[0] + " does not exist.");
                } else {
                    MediaHandler.getHandler().showPicture(file);
                }
            } else if (args[0] instanceof File) {
                file = (File) args[0];
            } else if (args[0] instanceof MediaURL) {
                file = ((MediaURL)args[0]).getRandomMedia();
            }

            if (args.length == 2) {
                if (args[1] instanceof Integer) {
                    MediaHandler.getHandler().showPicture(file, (Integer) args[1]);
                    return file;
                }
            } else {
                MediaHandler.getHandler().showPicture(file, 0);
                return file;
            }
        } else {
            TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method without parameters.");
            return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
