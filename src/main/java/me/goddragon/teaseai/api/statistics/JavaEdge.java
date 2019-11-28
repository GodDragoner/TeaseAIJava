package me.goddragon.teaseai.api.statistics;

public class JavaEdge extends StatisticsBase
{
    
    public Integer BPM = null;
    public JavaEdge()
    {
        super();
        isA = "Edge";
    }
    @Override
    public String toString() {
        return "Edge [StartTime=" + StartTime + ", EndTime=" + EndTime + ", isA=" + isA + "]";
    }
}
