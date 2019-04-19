package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class SetTextToSpeechFunction extends CustomFunction {

    public SetTextToSpeechFunction() {
        super("setTextToSpeech", "setTTS", "setTts");
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
                if (args[0] instanceof Boolean)
                {
                    if (TeaseAI.application.TEXT_TO_SPEECH.getInt() == 2)
                    {
                        TeaseAI.application.setTTS((Boolean)args[0]);
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else {
                    TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method with invalid parameter. A boolean must be used");
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
