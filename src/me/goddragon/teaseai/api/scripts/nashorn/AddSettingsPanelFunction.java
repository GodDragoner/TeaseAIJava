package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.config.PersonalitySettingsPanel;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.logging.Level;

public class AddSettingsPanelFunction extends CustomFunction
{
    public AddSettingsPanelFunction() {
        super("addSettingsPanel");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        PersonalitySettingsPanel panel = null;
        if (args.length != 1)
        {
            TeaseLogger.getLogger().log(Level.SEVERE, "Called " + getFunctionName() + " method without parameters.");
        }
        else
        {
            if (TeaseAI.application.getSession() == null)
            {
                panel = PersonalityManager.getManager().getLoadingPersonality().getSettingsHandler().addPanel((String)args[0]);
            }
            else
            {
                panel = PersonalityManager.getManager().getActivePersonality().getSettingsHandler().addPanel((String)args[0]);
            }
            
        }

        return panel;
    }
}
