package me.goddragon.teaseai.api.scripts.nashorn;

import java.util.Arrays;
import java.util.logging.Level;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.statistics.StatisticsManager;
import me.goddragon.teaseai.utils.TeaseLogger;

public class GetThisSessionStatisticsFunction extends CustomFunction {

    public GetThisSessionStatisticsFunction() {
        super("getThisSession", "getThisSessionStatistic", "getThisSessionStatistics", "getThisSessionInfo");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        switch (args.length) {
            case 0:
                StatisticsManager manager = null;
                if (TeaseAI.application.getSession() != null)
                {
                    manager = TeaseAI.application.getSession().statisticsManager;
                }
                else
                {
                    return null;
                }
                return manager.getThisSession();
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }

}
