package me.goddragon.teaseai.api.statistics;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CustomSerializer implements JsonSerializer<ArrayList<StatisticsBase>> {

    protected static Map<String, Class> map = new TreeMap<String, Class>();

    static {
        map.put("Edge", JavaEdge.class);
        map.put("Response", JavaResponse.class);
        map.put("Module", JavaModule.class);
        map.put("Stroke", JavaStroke.class);
        map.put("StatisticsBase", StatisticsBase.class);
        map.put("EdgeHold", JavaEdgeHold.class);
    }

    @Override
    public JsonElement serialize(ArrayList<StatisticsBase> src, Type typeOfSrc,
            JsonSerializationContext context) {
        if (src == null)
            return null;
        else {
            JsonArray ja = new JsonArray();
            for (StatisticsBase bc : src) {
                Class c = map.get(bc.isA);
                if (c == null)
                    throw new RuntimeException("Unknow class: " + bc.isA);
                ja.add(context.serialize(bc, c));

            }
            return ja;
        }
    }
}