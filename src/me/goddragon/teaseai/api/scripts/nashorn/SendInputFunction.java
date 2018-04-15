package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.chat.Answer;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class SendInputFunction extends CustomFunction {

    public SendInputFunction() {
        super("sendInput");
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
                return ChatHandler.getHandler().getSelectedSender().sendInput(args[0].toString());
            case 2:
                if (args[1] instanceof Integer) {
                    return ChatHandler.getHandler().getSelectedSender().sendInput(args[0].toString(), (Integer) args[1]);
                } else if (args[1] instanceof Answer) {
                    return ChatHandler.getHandler().getSelectedSender().sendInput(args[0].toString(), (Answer) args[1]);
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
