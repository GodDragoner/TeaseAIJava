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

        Answer answer = null;
        switch (args.length) {
            case 1:
                if(args[0] instanceof Integer) {
                    answer = new Answer((Integer) args[0]);
                }
                break;
            case 0:
                answer = new Answer();
                break;
        }

        if(answer != null) {
            ChatHandler.getHandler().setCurrentCallback(answer);

            //Reset timeout (normally the answer is a new object, but we don't know whether they might reuse an old answer)
            answer.setTimeout(false);

            //Reset the latest answer message
            answer.setAnswer(null);

            //Wait for answer
            TeaseAI.application.waitThread(Thread.currentThread(), answer.getMillisTimeout());
            answer.checkTimeout();
            return answer;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
