package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class SetVarFunction extends CustomFunction {

    public SetVarFunction() {
        super("setVar", "setVariable", "setFlag");
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
            case 2:
                if (args[0] instanceof String) {
                    return personality.getVariableHandler().setVariable((String) args[0], args[1]);
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
