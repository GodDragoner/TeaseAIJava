package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.media.MediaHandler;
import me.goddragon.teaseai.api.media.MediaType;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 28.03.2018.
 */
public class ShowTeaseImageFunction extends CustomFunction {

    public ShowTeaseImageFunction() {
        super("showTeaseImage", "showTeasePicture", "displayTeaseImage", "displayTeasePicture");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        File file = TeaseAI.application.getMediaCollection().getRandomTeaseFile(MediaType.IMAGE);

        if (file == null || !file.exists()) {
            return null;
        }

        if (args.length >= 1 && args[0] instanceof Number) {
            MediaHandler.getHandler().showPicture(file, ((Number)args[0]).intValue());
            return file;
        } else if (args.length == 0) {
            MediaHandler.getHandler().showPicture(file);
            return file;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
