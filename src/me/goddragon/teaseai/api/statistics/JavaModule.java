package me.goddragon.teaseai.api.statistics;

public class JavaModule extends StatisticsBase
{
    public String FileName;
    public JavaModule(String fileName)
    {
        this.FileName = fileName.replaceAll(".js", "");
    }
}
