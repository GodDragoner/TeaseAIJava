package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.media.MediaHandler;
import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

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
        switch(args.length) {
            case 1:
                if(args[0] instanceof String) {
                    MediaHandler.getHandler().showPicture(FileUtils.getRandomMatchingFile(args[0].toString()));
                    return null;
                }
                break;
            case 2:
                if(args[1] instanceof Integer) {
                    MediaHandler.getHandler().showPicture(FileUtils.getRandomMatchingFile(args[0].toString()), (Integer) args[1]);
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
