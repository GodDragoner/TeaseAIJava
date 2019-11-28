package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
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
                TeaseAI.application.setResponsesDisabled(true);
                return ChatHandler.getHandler().getSelectedSender().sendInput(args[0].toString(), 0);
            case 3:
                if (args[1] instanceof Integer && args[2] instanceof Boolean) {
                    TeaseAI.application.setResponsesDisabled((boolean) args[2]);
                    return ChatHandler.getHandler().getSelectedSender().sendInput(args[0].toString(), (int) args[1]);
                }
            default:
                Answer answer;
                int offset = 1;

                TeaseAI.application.setResponsesDisabled(true);

                if (args[1] instanceof Integer) {
                    for (int x = offset + 1; x < args.length; x++) {
                        Answer.addOption(args[x].toString(), args[x].toString());
                    }

                    answer = ChatHandler.getHandler().getSelectedSender().sendInput(args[0].toString(), (Integer) args[1] );
                } else if (args[1] instanceof Answer) {
                    for (int x = offset + 1; x < args.length; x++) {
                        Answer.addOption(args[x].toString(), args[x].toString());
                    }

                    answer = ChatHandler.getHandler().getSelectedSender().sendInput(args[0].toString(), (Answer) args[1]);
                } else if (args[1] instanceof Boolean) {
                    for (int x = offset + 1; x < args.length; x++) {
                        Answer.addOption(args[x].toString(), args[x].toString());
                    }

                    TeaseAI.application.setResponsesDisabled((boolean) args[1]);

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
