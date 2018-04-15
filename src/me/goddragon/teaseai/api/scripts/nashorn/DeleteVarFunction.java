package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class DeleteVarFunction extends CustomFunction {

    public DeleteVarFunction() {
        super("deleteVar", "delVar", "deleteVariable");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        switch(args.length) {
            case 1:
                if(args[0] instanceof String) {
                    PersonalityManager.getManager().getActivePersonality().getVariableHandler().deleteVariable((String) args[0]);
                    return null;
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
