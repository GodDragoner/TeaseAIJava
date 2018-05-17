package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by GodDragon on 17.05.2018.
 */
public class RegisterSupportedVariableFunction extends CustomFunction {

    public RegisterSupportedVariableFunction() {
        super("registerVariable", "registerVar", "setSupportedVariable");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        if(args.length >= 3) {
            String variableName = args[0].toString();
            String customName = args[1].toString();
            String description = args[2].toString();

            TeaseAI.application.getSession().getActivePersonality().getVariableHandler().setVariableSupport(variableName, customName, description);
            return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
