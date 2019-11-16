package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.config.TeaseDate;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.Calendar;
import java.util.logging.Level;

/**
 * Created by GodDragon on 30.03.2018.
 */
public class SetTempDateFunction extends CustomFunction {

    public SetTempDateFunction() {
        super("setTempDate");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        Personality personality;
        if (TeaseAI.application.getSession() == null) {
            personality = PersonalityManager.getManager().getLoadingPersonality();
        } else {
            personality = PersonalityManager.getManager().getActivePersonality();
        }

        switch (args.length) {
            case 1:
                if (args[0] instanceof String) {
                    return personality.getVariableHandler().setVariable((String) args[0], new TeaseDate(Calendar.getInstance().getTime()), true);
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
