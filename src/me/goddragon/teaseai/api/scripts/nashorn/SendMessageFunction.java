package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class SendMessageFunction extends CustomFunction {

    public SendMessageFunction() {
        super("sendMessage");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        switch(args.length) {
            case 1:
                ChatHandler.getHandler().getSelectedSender().sendMessage(args[0].toString());
                return null;
            case 2:
                if(args[1] instanceof Integer) {
                    ChatHandler.getHandler().getSelectedSender().sendMessage(args[0].toString(), (Integer) args[1]);
                    return null;
                } else {
                    TeaseLogger.getLogger().log(Level.SEVERE, "sendMessage only supports an integer as a second parameter. Args given:" + Arrays.asList(args).toString());
                    return null;
                }
            case 0:
                TeaseLogger.getLogger().log(Level.SEVERE, "Called sendMessage method without parameters.");
                return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
