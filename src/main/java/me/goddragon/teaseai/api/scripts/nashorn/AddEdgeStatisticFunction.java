package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.statistics.StatisticsManager;

public class AddEdgeStatisticFunction extends CustomFunction {

    public AddEdgeStatisticFunction() {
        super("addEdgeStatistic", "addEdgeStat");
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
        return manager.addEdge();
    }
}

