package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.config.TeaseDate;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.Calendar;
import java.util.logging.Level;

/**
 * Created by GodDragon on 30.03.2018.
 */
public class SetDateFunction extends CustomFunction {

    public SetDateFunction() {
        super("setDate");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        switch(args.length) {
            case 1:
                if(args[0] instanceof String) {
                    return PersonalityManager.getManager().getActivePersonality().getVariableHandler().setVariable((String) args[0], new TeaseDate(Calendar.getInstance().getTime()));
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
