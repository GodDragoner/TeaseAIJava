package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.media.MediaHandler;
import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.StringUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class ErrorMessageFunction extends CustomFunction {

    public ErrorMessageFunction() {
        super("errorMessage", "eM", "EM", "em");
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
                if (args[0] instanceof String) {
                    ChatHandler.getHandler().addLine(StringUtils.processString("<c=red b fs=16>Personality Error: <><c=darkslategrey b>" + (String) args[0]));
                } else {
                    TeaseLogger.getLogger().log(Level.SEVERE, "errorMessage must have a String for the first argument");
                }
                return null;
            case 2:
                if (!(args[0] instanceof String)) {
                    TeaseLogger.getLogger().log(Level.SEVERE, "errorMessage must have a String for the first argument");
                    return null;
                }
                if (args[1] instanceof Integer) {
                    ChatHandler.getHandler().addLine(StringUtils.processString("<c=red b fs=16>Personality Error: <><c=darkslategrey b>" + (String) args[0]));
                    if ((int) args[1] != 0) {
                        long delay = (int) args[1];
                        if (delay == -1) {
                            delay = ChatHandler.getHandler().getMillisToPause(args[0].toString());
                        }
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    return null;
                } else if (args[1] instanceof String) {
                    ChatHandler.getHandler().addLine(StringUtils.processString("<c=red b fs=16>Personality Error: <><c=darkslategrey b>" + (String) args[0]));

                    //TODO: Support for urls, video etc.
                    File file = FileUtils.getRandomMatchingFile((String) args[1]);

                    if (file != null) {
                        MediaHandler.getHandler().showPicture(file, (int) (ChatHandler.getHandler().getMillisToPause(args[0].toString()) / 1000));
                    }

                    return null;
                } else {
                    TeaseLogger.getLogger().log(Level.SEVERE, "errorMessage only supports an integer or a string to a picture file as a second parameter. Args given:" + Arrays.asList(args).toString());
                    return null;
                }
            case 0:
                TeaseLogger.getLogger().log(Level.SEVERE, "Called errorMessage method without parameters.");
                return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
