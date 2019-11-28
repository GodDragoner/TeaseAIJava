package me.goddragon.teaseai.api.statistics;

import java.util.ArrayList;

public class JavaModule extends StatisticsBase
{
    public Boolean Errored;
    public String FileName;
    public ArrayList<StatisticsBase> SubStatistics;
    public JavaModule(String fileName)
    {
        super();
        this.FileName = fileName.replaceAll(".js", "");
        isA = "Module";
    }
    
    public void setErrored(boolean value)
    {
        Errored = Boolean.valueOf(value);
    }
    
    public void add(StatisticsBase subStatistic)
    {
        if (SubStatistics == null)
            SubStatistics = new ArrayList<StatisticsBase>();   
        SubStatistics.add(subStatistic);
    }
    
    @Override
    public String toString() {
        return "Module [StartTime=" + StartTime + ", EndTime=" + EndTime + ", isA=" + isA + ", FileName=" + FileName + ", SubStatistics=" + SubStatistics + "]";
    }
}
