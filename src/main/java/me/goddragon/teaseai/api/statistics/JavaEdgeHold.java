package me.goddragon.teaseai.api.statistics;

public class JavaEdgeHold extends StatisticsBase
{
    public JavaEdgeHold()
    {
        super();
        isA = "EdgeHold";
    }
    @Override
    public String toString() {
        return "EdgeHold [StartTime=" + StartTime + ", EndTime=" + EndTime + ", isA=" + isA + "]";
    }
}
