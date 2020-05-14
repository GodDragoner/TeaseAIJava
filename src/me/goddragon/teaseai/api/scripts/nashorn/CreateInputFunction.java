package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.Answer;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 11.04.2018.
 */
public class CreateInputFunction extends CustomFunction {

    public CreateInputFunction() {
        super("createInput", "waitForInput");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        Answer answer;
        switch (args.length) {
            case 0:
                answer = new Answer();
                break;
            default:
                int offset = 0;
                if (args[0] instanceof Number) {
                    answer = new Answer(((Number)args[0]).longValue());
                    offset = 1;
                }else {
                    answer = new Answer();
                }

                for (int x = offset; x < args.length; x++) {
                    answer.addOption(args[x].toString(), args[x].toString());
                }
                break;
        }

        if (answer != null) {
            ChatHandler.getHandler().setCurrentCallback(answer);

            //Reset timeout (normally the answer is a new object, but we don't know whether they might reuse an old answer)
            answer.setTimeout(false);

            //Reset the latest answer message
            answer.setAnswer(null);


            answer.setStartedAt(System.currentTimeMillis());

            //Wait for answer
            TeaseAI.application.waitPossibleScripThread(answer.getMillisTimeout());
            answer.checkTimeout();

            return answer;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
