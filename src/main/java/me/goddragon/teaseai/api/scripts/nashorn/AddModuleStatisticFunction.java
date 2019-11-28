package me.goddragon.teaseai.api.scripts.nashorn;

import java.util.Arrays;
import java.util.logging.Level;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.statistics.StatisticsManager;
import me.goddragon.teaseai.utils.TeaseLogger;

public class AddModuleStatisticFunction extends CustomFunction {

    public AddModuleStatisticFunction() {
        super("addModuleStatistic", "addModuleStat");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        StatisticsManager manager = null;
        if (TeaseAI.application.getSession() != null)
        {
            manager = TeaseAI.application.getSession().statisticsManager;
        }
        else
        {
            return null;
        }
        if (args.length == 1 && args[0] instanceof String)
        {
            return manager.addModule((String)args[0]);
        }
        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}

