package me.goddragon.teaseai.api.statistics;

import java.util.Date;
import java.util.Stack;
import java.util.logging.Level;

import me.goddragon.teaseai.utils.TeaseLogger;

public class StatisticsManager
{
    private Stack<JavaModule> moduleStatistics;
    private StatisticsList statisticsList;
    private boolean lastmoduleignored;
    public StatisticsManager()
    {
        moduleStatistics = new Stack<JavaModule>();
        statisticsList = new StatisticsList();
    }
    
    public void addModule(String fileName)
    {
        JavaModule toPush = new JavaModule(fileName);
        toPush.StartTime = new Date();
        moduleStatistics.push(toPush);
        statisticsList.add(toPush);
        statisticsList.writeJson();
    }
    
    public void endModule()
    {
        if (!lastmoduleignored)
        {
            moduleStatistics.pop().EndTime = new Date();
            statisticsList.writeJson();
        }
        lastmoduleignored = false;
    }
    
    public void ignoreCurrentModule()
    {
        TeaseLogger.getLogger().log(Level.INFO, "debug 2");
        if (!moduleStatistics.isEmpty())
        {
            TeaseLogger.getLogger().log(Level.INFO, "debug " + statisticsList.remove(moduleStatistics.peek()));
            moduleStatistics.pop();
            statisticsList.writeJson();
            lastmoduleignored = true;
        }
    }
}
