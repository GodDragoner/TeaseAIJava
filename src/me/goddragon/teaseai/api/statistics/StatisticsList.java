package me.goddragon.teaseai.api.statistics;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StatisticsList extends ArrayList<JavaModule>
{
    private String filePath = "testing.json";
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    @Override
    public boolean add(JavaModule toAdd)
    {
        boolean toReturn = super.add(toAdd);
        if (toReturn)writeJson();
        return toReturn;
    }
    
    @Override
    public boolean remove(Object toRemove)
    {
        boolean toReturn = super.remove(toRemove);
        if (toReturn)writeJson();
        return toReturn;
    }
    
    public void writeJson(String filePath)
    {
        try (Writer writer = new FileWriter(filePath))
        {
            Gson test = new GsonBuilder().create();
            test.toJson(this, writer);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void writeJson()
    {
        writeJson(filePath);
    }
}
