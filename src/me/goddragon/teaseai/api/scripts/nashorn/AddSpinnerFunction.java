package me.goddragon.teaseai.api.scripts.nashorn;

import java.util.logging.Level;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.config.PersonalitiesSettingsHandler;
import me.goddragon.teaseai.api.config.PersonalitySettingsPanel;
import me.goddragon.teaseai.api.config.PersonalityVariable;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;
import me.goddragon.teaseai.utils.TeaseLogger;

public class AddSpinnerFunction extends CustomFunction
{
    public AddSpinnerFunction() {
        super("addSpinner", "addValueRange", "addRange");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        if (args.length != 4)
        {
            TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method without parameters.");
        }
        else
        {
            Personality personality;
            if (TeaseAI.application.getSession() == null)
            {
                personality = PersonalityManager.getManager().getLoadingPersonality();
            }
            else
            {
                personality = PersonalityManager.getManager().getActivePersonality();
            }
            PersonalitySettingsPanel panel = personality.getSettingsHandler().getPanel((String)args[0]);
            if (panel == null)
            {
                TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method with invalid panel name " + args[0]);
                return null;
            }
            else
            {
                PersonalityVariable variable = personality.getVariableHandler().getVariable((String)args[1]);
                if (variable == null)
                {
                    TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method with invalid variable name " + args[1]);
                    return null;
                }
                else
                {
                    if (!PersonalitiesSettingsHandler.getHandler().hasComponent(variable))
                    {
                        PersonalitiesSettingsHandler.getHandler().addGuiComponent(variable);
                        int min = 0;
                        int max = 0;
                        
                        if (args[2] instanceof Integer)
                        {
                            min = (Integer) args[2];
                        }
                        else if (isInteger((String)args[2]))
                        {
                            min = Integer.parseInt((String)args[2]);
                        }
                        
                        if (args[3] instanceof Integer)
                        {
                            max = (Integer) args[3];
                        }
                        else if (isInteger((String)args[3]))
                        {
                            max = Integer.parseInt((String)args[3]);
                        }
                        
                        panel.addSpinner(variable, min, max);
                    }
                }
            }
        }

        return null;
    }
    private static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    private static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
}
