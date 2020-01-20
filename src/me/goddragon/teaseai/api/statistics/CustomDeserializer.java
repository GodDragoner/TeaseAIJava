package me.goddragon.teaseai.api.statistics;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import me.goddragon.teaseai.utils.TeaseLogger;

public class CustomDeserializer implements JsonDeserializer<List<StatisticsBase>> {

    protected static Map<String, Class> map = new TreeMap<String, Class>();

    static {
        map.put("Edge", JavaEdge.class);
        map.put("Response", JavaResponse.class);
        map.put("Module", JavaModule.class);
        map.put("Stroke", JavaStroke.class);
        map.put("StatisticsBase", StatisticsBase.class);
        map.put("EdgeHold", JavaEdgeHold.class);
    }

    public List<StatisticsBase> deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        List list = new ArrayList<StatisticsBase>();
        JsonArray ja = json.getAsJsonArray();

        for (JsonElement je : ja) {
            if (je.isJsonPrimitive())
            {
                if (je.getAsJsonPrimitive().isString())
                {
                    list.add(context.deserialize(je, String.class));
                }
                else if (je.getAsJsonPrimitive().isNumber())
                {
                    list.add(context.deserialize(je, Integer.class));
                }
                else if (je.getAsJsonPrimitive().isBoolean())
                {
                    list.add(context.deserialize(je, Boolean.class));
                }
                continue;
            }
            else if (je.getAsJsonObject().get("isA").isJsonNull())
            {
                list.add(context.deserialize(je, Object.class));
                continue;
            }
            String type = je.getAsJsonObject().get("isA").getAsString();
            Class c = map.get(type);
            if (c == null)
                throw new RuntimeException("Unknown class: " + type);
            list.add(context.deserialize(je, c));
        }

        return list;

    }

}