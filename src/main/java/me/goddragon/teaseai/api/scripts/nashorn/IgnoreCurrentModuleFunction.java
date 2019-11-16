package me.goddragon.teaseai.api.scripts.nashorn;

import java.util.logging.Level;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.utils.TeaseLogger;

public class IgnoreCurrentModuleFunction extends CustomFunction
{

    public IgnoreCurrentModuleFunction()
    {
        super("ignoreCurrentModule", "ignoreCurrent", "ignoreModule");
    }

    @Override
    public boolean isFunction()
    {
        return true;
    }

    @Override
    public Object call(Object object, Object... args)
    {
        super.call(object, args);
        TeaseLogger.getLogger().log(Level.INFO, getFunctionName() + " called");
        if (TeaseAI.application.getSession() != null)
            TeaseAI.application.getSession().statisticsManager.ignoreCurrentModule();
        return null;
    }

}
