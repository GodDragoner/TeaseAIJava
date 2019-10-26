package me.goddragon.teaseai.api.statistics;


public class JavaStroke extends StatisticsBase
{
    
    public Integer BPM = null;
    public JavaStroke()
    {
        super();
        isA = "Stroke";
    }
    
    @Override
    public String toString() {
        return "Stroke [StartTime=" + StartTime.toString() + ", EndTime=" + EndTime.toString() + ", isA=" + isA + "]";
    }
}
