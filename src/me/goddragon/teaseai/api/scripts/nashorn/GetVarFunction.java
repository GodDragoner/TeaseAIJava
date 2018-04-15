package me.goddragon.teaseai.api.scripts.nashorn;

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

        switch (args.length) {
            case 1:
                return PersonalityManager.getManager().getActivePersonality().getVariableHandler().getVariableValue(args[0].toString());
            case 2:
                if(PersonalityManager.getManager().getActivePersonality().getVariableHandler().variableExist(args[0].toString())) {
                    return PersonalityManager.getManager().getActivePersonality().getVariableHandler().getVariableValue(args[0].toString());
                } else {
                    //Return default
                    return args[1];
                }
            case 0:
                TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method without parameters.");
                return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
