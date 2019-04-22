package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.chat.ChatParticipant;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class SetActiveSenderFunction extends CustomFunction {

    public SetActiveSenderFunction() {
        super("setSender", "setDom");
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
                if (args[0] instanceof Integer) {
                    Integer id = (Integer) args[0];

                    if (id == 0) {
                        TeaseLogger.getLogger().log(Level.SEVERE, "Sub can't be set as the current sender.");
                        return null;
                    }

                    ChatParticipant participant = ChatHandler.getHandler().getParticipantById(id);
                    ChatHandler.getHandler().setCurrentDom(participant);
                    return participant;
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
