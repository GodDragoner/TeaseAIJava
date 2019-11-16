package me.goddragon.teaseai.api.statistics;

import java.util.Date;

public class StatisticsBase
{
    protected String isA = "StatisticsBase";
    protected Date StartTime;
    protected Date EndTime;
    
    public StatisticsBase()
    {
        StartTime = new Date();
    }
    
    public void init()
    {
        
    }
    
    public void setType(String type)
    {
        isA = type;
    }
    
    public void EndCleanly()
    {
        EndTime = new Date();
    }
    
    @Override
    public String toString() {
        return "StatisticsBase [StartTime=" + StartTime.toString() + ", EndTime=" + EndTime.toString() + ", isA=" + isA + "]";
    }
}
