package me.goddragon.teaseai.api.statistics;

import java.util.ArrayList;

public class SessionStatistics
{
    private ArrayList<JavaModule> statistics;
    private ArrayList<JavaStroke> strokes;
    private ArrayList<JavaEdge> edges;
    private ArrayList<JavaEdgeHold> edgeHolds;
    private ArrayList<JavaFetishActivity> fetishActivities;
    private boolean inProgressSession;
    
    public SessionStatistics (ArrayList<JavaModule> statistics, boolean inProgressSession)
    {
        this.statistics = statistics;
        this.inProgressSession = inProgressSession;
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<JavaModule> getFullSessionInfo()
    {
        return (ArrayList<JavaModule>)statistics.clone();
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<JavaStroke> getStrokes()
    {
        loadLists();
        return (ArrayList<JavaStroke>)strokes.clone();
    }
    
    private void loadLists()
    {
        if (strokes == null || edges == null)
        {
            strokes = new ArrayList<JavaStroke>();
            edges = new ArrayList<JavaEdge>();
            edgeHolds = new ArrayList<JavaEdgeHold>();
            fetishActivities = new ArrayList<JavaFetishActivity>();
            for (JavaModule module: statistics)
            {
                proccessListsHelper(module);
            }
        }
    }
    
    //Recursive iterator to iterate thru the session statistics
    private void proccessListsHelper(JavaModule module)
    {
        if (module.SubStatistics == null)
        {
            return;
        }
        for (StatisticsBase statistic: module.SubStatistics)
        {
            if (statistic instanceof JavaEdge)
            {
                edges.add((JavaEdge)statistic);
            }
            else if (statistic instanceof JavaEdgeHold)
            {
                edgeHolds.add((JavaEdgeHold)statistic);
            }
            else if (statistic instanceof JavaStroke)
            {
                strokes.add((JavaStroke)statistic);
            }
            else if (statistic instanceof JavaFetishActivity)
            {
                fetishActivities.add((JavaFetishActivity)statistic);
            }
            else if (statistic instanceof JavaModule)
            {
                proccessListsHelper((JavaModule)statistic);
            }
        }
    }
}
