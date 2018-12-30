package me.goddragon.teaseai.api.config;

import java.util.ArrayList;
import java.util.HashSet;

public class PersonalitiesSettingsHandler
{
    private static HashSet<GUIComponent> addedComponents;
    private static PersonalitiesSettingsHandler thisHandler;
    private static ArrayList<PersonalitySettingsHandler> personalitySettingsHandlers;
    
    public PersonalitiesSettingsHandler()
    {
        thisHandler = this;
        personalitySettingsHandlers = new ArrayList<PersonalitySettingsHandler>();
    }
    
    public static PersonalitiesSettingsHandler getHandler()
    {
        return thisHandler;
    }
    
    public void addPersonalitySettingsHandler(PersonalitySettingsHandler handler)
    {
        personalitySettingsHandlers.add(handler);
    }
    
    public boolean containsComponent(GUIComponent comp)
    {
        return addedComponents.contains(comp);
    }
    
    public void addGuiComponent(GUIComponent comp)
    {
        if (addedComponents.contains(comp))
        {
            throw new IllegalCallerException("Gui component added that has already been added. Check if it has been added first!");
        }
        else
        {
            addedComponents.add(comp);
        }
    }
}
