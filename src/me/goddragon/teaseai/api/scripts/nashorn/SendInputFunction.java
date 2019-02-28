package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.chat.Answer;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.utils.TeaseLogger;

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
            case 0:
                TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method without parameters.");
                return null;
            case 1:
                return ChatHandler.getHandler().getSelectedSender().sendInput(args[0].toString());
            default:
                Answer answer;
                int offset = 1;
                if (args[1] instanceof Integer) {
                    for (int x = offset + 1; x < args.length; x++) {
                        Answer.addOption(args[x].toString(), args[x].toString());
                    }

                    answer = ChatHandler.getHandler().getSelectedSender().sendInput(args[0].toString(), (Integer) args[1]);
                } else if (args[1] instanceof Answer) {
                    for (int x = offset + 1; x < args.length; x++) {
                        Answer.addOption(args[x].toString(), args[x].toString());
                    }

                    answer = ChatHandler.getHandler().getSelectedSender().sendInput(args[0].toString(), (Answer) args[1]);
                } else {
                    for (int x = offset; x < args.length; x++) {
                        Answer.addOption(args[x].toString(), args[x].toString());
                    }

                    answer = ChatHandler.getHandler().getSelectedSender().sendInput(args[0].toString());
                }

                return answer;
        }
    }
}
