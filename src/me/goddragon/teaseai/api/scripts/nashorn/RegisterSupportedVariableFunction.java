package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;
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
            
            Personality personality;
            if (TeaseAI.application.getSession() == null)
            {
                personality = PersonalityManager.getManager().getLoadingPersonality();
            }
            else
            {
                personality = PersonalityManager.getManager().getActivePersonality();
            }
            
            String variableName = args[0].toString();
            String customName = args[1].toString();
            String description = args[2].toString();

            personality.getVariableHandler().setVariableSupport(variableName, customName, description);
            return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}
