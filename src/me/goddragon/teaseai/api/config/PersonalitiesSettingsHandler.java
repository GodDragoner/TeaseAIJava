package me.goddragon.teaseai.api.config;

import javafx.scene.control.Tab;

import java.util.ArrayList;
import java.util.HashSet;

public class PersonalitiesSettingsHandler {
    private static HashSet<PersonalityVariable> addedComponents;
    private static PersonalitiesSettingsHandler thisHandler;
    private static ArrayList<PersonalitySettingsHandler> personalitySettingsHandlers;
    private static ArrayList<Tab> personalityTabsToAdd;

    public PersonalitiesSettingsHandler() {
        thisHandler = this;
        personalitySettingsHandlers = new ArrayList<>();
        personalityTabsToAdd = new ArrayList<>();
        addedComponents = new HashSet<>();
    }

    public static PersonalitiesSettingsHandler getHandler() {
        return thisHandler;
    }

    public void addPersonalitySettingsHandler(PersonalitySettingsHandler handler) {
        personalitySettingsHandlers.add(handler);
    }

    public Tab addPersonalityTab(String personalityName) {
        Tab newTab = new Tab(personalityName);
        personalityTabsToAdd.add(newTab);
        return newTab;
    }

    public void addGuiComponent(PersonalityVariable comp) {
        if (addedComponents.contains(comp)) {
            throw new IllegalCallerException("Gui component added that has already been added. Check if it has been added first!");
        } else {
            addedComponents.add(comp);
        }
    }


    public boolean hasComponent(PersonalityVariable comp) {
        boolean equivalentVar = false;
        for (PersonalityVariable var : addedComponents) {
            if (var.equals(comp)) {
                equivalentVar = true;
            }
        }
        return addedComponents.contains(comp) || equivalentVar;
    }

    public ArrayList<Tab> getTabsToAdd() {
        return personalityTabsToAdd;
    }

    public ArrayList<PersonalitySettingsHandler> getSettingsHandlers() {
        return personalitySettingsHandlers;
    }
}
