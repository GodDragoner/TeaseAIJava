package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.media.MediaHandler;
import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class SendMessageFunction extends CustomFunction {

    public SendMessageFunction() {
        super("sendMessage", "sm", "SM");
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
                if (args[0] instanceof String)
                {
                    ChatHandler.getHandler().getSelectedSender().customMessage((String)args[0], -1, true);
                }
                else {
                    TeaseLogger.getLogger().log(Level.SEVERE, "sendMessage must have a String for the first argument");
                }
                return null;
            case 2:
                if (!(args[0] instanceof String))
                {
                    TeaseLogger.getLogger().log(Level.SEVERE, "sendMessage must have a String for the first argument");
                    return null;
                }
                if(args[1] instanceof Integer) {
                    ChatHandler.getHandler().getSelectedSender().customMessage((String)args[0], (int)args[1] * 1000, true);
                    return null;
                } else if(args[1] instanceof String) {
                    ChatHandler.getHandler().getSelectedSender().customMessage((String)args[0], 0, true);

                    //TODO: Support for urls, video etc.
                    File file = FileUtils.getRandomMatchingFile((String) args[1]);

                    if(file != null) {
                        MediaHandler.getHandler().showPicture(file, (int) (ChatHandler.getHandler().getMillisToPause(args[0].toString())/1000));
                    }

                    return null;
                } else {
                    TeaseLogger.getLogger().log(Level.SEVERE, "sendMessage only supports an integer or a string to a picture file as a second parameter. Args given:" + Arrays.asList(args).toString());
                    return null;
                }
            case 3:
                if (!(args[0] instanceof String))
                {
                    TeaseLogger.getLogger().log(Level.SEVERE, "sendMessage must have a String for the first argument");
                    return null;
                }
                if (!(args[1] instanceof Integer))
                {
                    TeaseLogger.getLogger().log(Level.SEVERE, "sendMessage must have a integer for the second argument");
                    return null;
                }
                if (!(args[2] instanceof Integer))
                {
                    TeaseLogger.getLogger().log(Level.SEVERE, "sendMessage must have a boolean for the third argument");
                    return null;
                }
                ChatHandler.getHandler().getSelectedSender().customMessage((String)args[0], (int)args[2], (Boolean) args[3]);
                
                break;
            case 0:
                TeaseLogger.getLogger().log(Level.SEVERE, "Called sendMessage method without parameters.");
                return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
