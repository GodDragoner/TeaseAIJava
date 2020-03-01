package me.goddragon.teaseai.api.statistics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class StatisticsList
{
    protected ArrayList<JavaModule> Statistics;
    private String filePath;
    
    public StatisticsList()
    {
        Statistics = new ArrayList<JavaModule>();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy H-mm-ss");
        Date date = new Date();

        if (!new File("Statistics").exists()) {
            new File("Statistics").mkdir();
        }
        filePath = "Statistics" + File.separator + "Session-" + dateFormat.format(date) + ".json";
    }
    
    public ArrayList<JavaModule> toList()
    {
        return Statistics;
    }
    
    public boolean add(JavaModule toAdd)
    {
        boolean toReturn = Statistics.add(toAdd);
        if (toReturn)writeJson();
        return toReturn;
    }
    
    public boolean remove(Object toRemove)
    {
        boolean toReturn = Statistics.remove(toRemove);
        if (toReturn)writeJson();
        return toReturn;
    }
    
    public void writeJson(String filePath)
    {
        try (Writer writer = new FileWriter(filePath))
        {
            getGSON().toJson(Statistics, writer);
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
    
    public String getPath()
    {
        return filePath;
    }
    
    private static Gson getGSON()
    {
        GsonBuilder gb = new GsonBuilder();
        List<StatisticsBase> al = new ArrayList<StatisticsBase>();
        CustomDeserializer cDeserializer = new CustomDeserializer();
        CustomSerializer cSerializer = new CustomSerializer();
        gb.registerTypeAdapter(al.getClass(), cDeserializer);
        gb.registerTypeAdapter(al.getClass(), cSerializer);
        return gb.create();
    }
    
    public static ArrayList<JavaModule> deserialize(String filePath)
    {
        Gson gson = getGSON();
        JsonReader reader;
        ArrayList<JavaModule> list = null;
        try
        {
            reader = new JsonReader(new FileReader(filePath));
            list = gson.fromJson(reader, ArrayList.class); // contains the whole reviews list
        }
        catch (FileNotFoundException e)
        {
        }
        return list;
    }
}
