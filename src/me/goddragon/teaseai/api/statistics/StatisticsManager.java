package me.goddragon.teaseai.api.statistics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Stack;
import java.util.logging.Level;

import me.goddragon.teaseai.utils.TeaseLogger;
import me.goddragon.teaseai.utils.stream.TeeOutputStream;

public class StatisticsManager
{
    private Stack<JavaModule> moduleStatistics;
    private StatisticsList statisticsList;
    private HashSet<Integer> ignoredModules;
    private JavaEdge currentEdge = null;
    private JavaEdgeHold currentEdgeHold = null;
    private JavaStroke currentStroke = null;
    public static boolean edgeDetection = true;
    public static boolean strokeDetection = true;
    public static boolean moduleDetection = true;
    public static boolean edgeHoldDetection = true;
    private String outputPath;
    
    public StatisticsManager()
    {
        moduleStatistics = new Stack<JavaModule>();
        statisticsList = new StatisticsList();
        ignoredModules = new HashSet<Integer>();

    }
    
    public static void toggleEdgeDetection(boolean state)
    {
        edgeDetection = state;
    }
    
    public static void toggleStrokeDetection(boolean state)
    {
        strokeDetection = state;
    }
    
    public static void toggleModuleDetection(boolean state)
    {
        moduleDetection = state;
    }
    
    public static void toggleEdgeHoldDetection(boolean state)
    {
        edgeHoldDetection = state;
    }

    public JavaModule getCurrentModule()
    {
        JavaModule toReturn = null;
        if (moduleStatistics != null)
        {
            if (!moduleStatistics.isEmpty())
            {
                toReturn = moduleStatistics.peek();
            }
        }
        return toReturn;
    }
    
    public JavaModule addModule(String fileName)
    {
        JavaModule toPush = new JavaModule(fileName);
        if (moduleStatistics.isEmpty() || lowestNonIgnored() == 0)
        {
            moduleStatistics.push(toPush);
            statisticsList.add(toPush);
            statisticsList.writeJson();
        }
        else 
        {
            int lowestNonIgnore = lowestNonIgnored();
            ((JavaModule)moduleStatistics.toArray()[lowestNonIgnore - 1]).add(toPush);
            moduleStatistics.push(toPush);
            statisticsList.writeJson();
        }
        return toPush;
    }
    
    private int lowestNonIgnored()
    {
        int i;
        for (i = moduleStatistics.size(); ignoredModules.contains(i); i--)
        {
            
        }
        return i;
    }
    
    public void endModule()
    {
        if (!moduleStatistics.isEmpty())
        {
            if (ignoredModules.contains(moduleStatistics.size()))
            {
                ignoredModules.remove(moduleStatistics.size());
                moduleStatistics.pop();
            }
            else
            {
                moduleStatistics.pop().EndCleanly();
                statisticsList.writeJson();
            }
        }
    }
    
    public void ignoreCurrentModule()
    {
        TeaseLogger.getLogger().log(Level.INFO, "debug 2");
        if (!moduleStatistics.isEmpty())
        {
            statisticsList.remove(moduleStatistics.peek());
            int stackDepth = moduleStatistics.size();
            ignoredModules.add(stackDepth);
            statisticsList.writeJson();
            //lastmoduleignored = true;
        }
    }
    
    public JavaEdge addEdge()
    {
        JavaEdge toAdd = new JavaEdge();
        if (moduleStatistics.isEmpty())
        {
            TeaseLogger.getLogger().log(Level.WARNING, "Edge was started but there are currently no active modules!!"
                    + " Could a module have been marked to be ignored when it shouldn't be?");
            addModule("PlaceHolderBaseNotARealScript.js");
        }
        currentEdge = toAdd;
        moduleStatistics.peek().add(toAdd);
        statisticsList.writeJson();
        return toAdd;
    }
    
    public void endEdge()
    {
        if (currentEdge == null)
        {
            TeaseLogger.getLogger().log(Level.WARNING, "End Edge was called but can't find an active edge!");
            return;
        }
        currentEdge.EndCleanly();
        statisticsList.writeJson();
        currentEdge = null;
    }
    
    public JavaEdgeHold addEdgeHold()
    {
        JavaEdgeHold toAdd = new JavaEdgeHold();
        if (moduleStatistics.isEmpty())
        {
            TeaseLogger.getLogger().log(Level.WARNING, "EdgeHold was started but there are currently no active modules!!"
                    + " Could a module have been marked to be ignored when it shouldn't be?");
            addModule("PlaceHolderBaseNotARealScript.js");
        }
        currentEdgeHold = toAdd;
        moduleStatistics.peek().add(toAdd);
        return toAdd;
    }
    
    public void endEdgeHold()
    {
        if (currentEdgeHold == null)
        {
            TeaseLogger.getLogger().log(Level.WARNING, "End EdgeHold was called but can't find an active edgeHold!");
            return;
        }
        currentEdgeHold.EndCleanly();
        statisticsList.writeJson();
        currentEdge = null;
    }
    
    public JavaStroke addStroke()
    {
        JavaStroke toAdd = new JavaStroke();
        if (moduleStatistics.isEmpty())
        {
            TeaseLogger.getLogger().log(Level.WARNING, "Stroke was started but there are currently no active modules!!"
                    + " Could a module have been marked to be ignored when it shouldn't be?");
            addModule("PlaceHolderBaseNotARealScript.js");
        }
        currentStroke = toAdd;
        moduleStatistics.peek().add(toAdd);
        statisticsList.writeJson();
        return toAdd;
    }
    
    public void endStroke()
    {
        if (currentStroke == null)
        {
            TeaseLogger.getLogger().log(Level.WARNING, "End Stroke was called but can't find an active Stroke!");
            return;
        }
        currentStroke.EndCleanly();
        statisticsList.writeJson();
        currentStroke = null;
    }
    
    public JavaFetishActivity addFetish()
    {
        JavaFetishActivity toAdd = new JavaFetishActivity();
        if (moduleStatistics.isEmpty())
        {
            TeaseLogger.getLogger().log(Level.WARNING, "FetishActivity was started but there are currently no active modules!!"
                    + " Could a module have been marked to be ignored when it shouldn't be?");
            addModule("PlaceHolderBaseNotARealScript.js");
        }
        moduleStatistics.peek().add(toAdd);
        statisticsList.writeJson();
        return toAdd;
    }
    
    public void endFetish(JavaFetishActivity activity)
    {
        activity.EndCleanly();
        statisticsList.writeJson();
        currentStroke = null;
    }
       
    public SessionStatistics getThisSession()
    {
        return new SessionStatistics(StatisticsList.deserialize(statisticsList.getPath()), true);
    }
    
}
