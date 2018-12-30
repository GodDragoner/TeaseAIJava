package me.goddragon.teaseai.api.config;

import javafx.scene.Node;
import javafx.scene.control.Label;

public abstract class GUIComponent
{
    
    protected Label settingLabel;
    protected String description;
    protected Node setting;
    protected int columnNumber = -1;
    
    public GUIComponent(String settingString, int columnNumber)
    {
        this(settingString);
        this.columnNumber = columnNumber;
    }
    
    public GUIComponent(String settingString)
    {
        settingString += ":";
        settingLabel = new Label(settingString);
    }
    
    public GUIComponent(String settingString, String description, int columnNumber)
    {
        this(settingString, description);
        this.columnNumber = columnNumber;
    }
    
    public GUIComponent(String settingString, String description)
    {
        this(settingString);
        this.description = description;
    }
    
    public Label getLabel()
    {
        return settingLabel;
    }
    
    public Node getSetting()
    {
        return setting;
    }
    
    public int getColumnID()
    {
        return columnNumber;
    }
}
