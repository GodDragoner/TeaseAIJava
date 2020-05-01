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
public class GetVarFunction extends CustomFunction {

    public GetVarFunction() {
        super("getVar", "checkVar", "getDate", "getVariable");
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

        Object value = null;

        switch (args.length) {
            case 1:
                value = personality.getVariableHandler().getVariableValue(args[0].toString());
                break;
            case 2:
                if (personality.getVariableHandler().variableExist(args[0].toString())) {
                    value = personality.getVariableHandler().getVariableValue(args[0].toString());
                } else {
                    //Return default
                    value = args[1];
                }
                break;
            case 0:
                TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method without parameters.");
                return null;
        }

        if(value == null) {
            TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args or variable was not found. Args:" + Arrays.asList(args).toString());
            TeaseLogger.getLogger().log(Level.SEVERE, "Infos about object given:  Class: " + object.getClass());
        }

        return value;
    }
}
