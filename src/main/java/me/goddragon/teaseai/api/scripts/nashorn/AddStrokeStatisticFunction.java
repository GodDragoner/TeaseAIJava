package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.statistics.StatisticsManager;

public class AddStrokeStatisticFunction extends CustomFunction {

    public AddStrokeStatisticFunction() {
        super("addStrokeStatistic", "addStrokeStat");
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
        return manager.addStroke();
    }
}

