package me.goddragon.teaseai.api.statistics;

public class JavaFetishActivity extends StatisticsBase
{
    public JavaFetishActivity()
    {
        super();
        isA = "Fetish";
    }
    @Override
    public String toString() {
        return "Fetish [StartTime=" + StartTime + ", EndTime=" + EndTime + ", isA=" + isA + "]";
    }
}
