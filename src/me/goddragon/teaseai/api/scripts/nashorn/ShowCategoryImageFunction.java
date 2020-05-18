package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.media.MediaFetishType;
import me.goddragon.teaseai.api.media.MediaHandler;
import me.goddragon.teaseai.api.media.MediaType;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 26.03.2018.
 */
public class ShowCategoryImageFunction extends CustomFunction {

    public ShowCategoryImageFunction() {
        super("showCategoryImage", "showCategoryPicture", "displayCategoryImage", "displayCategoryPicture");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        if (args.length >= 1 && args[0] instanceof String) {
            String category = (String) args[0];

            MediaFetishType mediaFetishType;
            try {
                mediaFetishType = MediaFetishType.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException ex) {
                TeaseLogger.getLogger().log(Level.SEVERE, "'" + category + "' is not a valid image category.");
                return null;
            }

            File picture = TeaseAI.application.getMediaCollection().getRandomFile(mediaFetishType, MediaType.IMAGE);

            if (picture == null) {
                TeaseLogger.getLogger().log(Level.SEVERE, "'" + category + "' did not hold any valid image.");
                return null;
            }

            switch (args.length) {
                case 1:
                    MediaHandler.getHandler().showPicture(picture);
                    return picture;
                case 2:
                    if (args[1] instanceof Number) {
                        MediaHandler.getHandler().showPicture(picture, ((Number)args[1]).intValue());
                        return picture;
                    }

                    break;
                case 0:
                    TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method without parameters.");
                    return null;
            }
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
