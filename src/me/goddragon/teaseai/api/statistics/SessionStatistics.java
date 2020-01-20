package me.goddragon.teaseai.api.statistics;

import java.util.ArrayList;

public class SessionStatistics
{
    private ArrayList<JavaModule> statistics;
    private ArrayList<JavaStroke> strokes;
    private ArrayList<JavaEdge> edges;
    private ArrayList<JavaEdgeHold> edgeHolds;
    private ArrayList<JavaFetishActivity> fetishActivities;
    
    public SessionStatistics (ArrayList<JavaModule> statistics)
    {
        this.statistics = statistics;
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<JavaModule> getFullSessionInfo()
    {
        return (ArrayList<JavaModule>)statistics;
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<JavaStroke> getStrokes()
    {
        loadLists();
        return (ArrayList<JavaStroke>)strokes;
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<JavaEdge> getEdges()
    {
        loadLists();
        return (ArrayList<JavaEdge>)edges;
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<JavaEdgeHold> getEdgeHolds()
    {
        loadLists();
        return (ArrayList<JavaEdgeHold>)edgeHolds;
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<JavaFetishActivity> getFetishActivities()
    {
        loadLists();
        return (ArrayList<JavaFetishActivity>)fetishActivities;
    }
    
    private void loadLists()
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
    
    @Override
    public String toString(){
        return "statistics list: " + statistics.toString(); 
    }
}
