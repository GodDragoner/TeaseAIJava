package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.media.MediaType;
import me.goddragon.teaseai.api.media.MediaURL;
import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 20.05.2018.
 */
public class CreateMediaURLFromFileFunction extends CustomFunction {

    public CreateMediaURLFromFileFunction() {
        super("createMediaURL", "createMediaURLFromFile");
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        if (args.length == 1) {
            File file = FileUtils.getRandomMatchingFile(TeaseAI.application.getSession().getActivePersonality().getFolder().getAbsolutePath() + File.separator + args[0].toString());
            if (file == null || !file.exists()) {
                TeaseLogger.getLogger().log(Level.SEVERE, "Matching url file for path " + args[0] + " does not exist.");
            } else {
                return new MediaURL(MediaType.IMAGE, file);
            }

            return null;
        } else if (args.length > 0) {
            TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
            return null;
        } else {
            TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method without parameters.");
            return null;
        }
    }
}
