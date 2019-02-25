package me.goddragon.teaseai.gui.settings;

import javafx.scene.paint.Color;

public class Theme
{
    public String name;
    public Color PrimaryColor;
    public Color ChatWindow;
    public Color ChatBackground;
    public Color ChatColor;
    public Color DateColor;
    public Color SubColor;
    public Color DomColor;
    public Color Friend1Color;
    public Color Friend2Color;
    public Color Friend3Color;
    
    public Theme(String name, Color PrimaryColor, Color ChatWindow, Color ChatBackground, Color ChatColor, Color DateColor, Color SubColor, Color DomColor, Color Friend1Color, Color Friend2Color, Color Friend3Color)
    {
        this.name = name;
        this.PrimaryColor = PrimaryColor;
        this.ChatWindow = ChatWindow;
        this.ChatBackground = ChatBackground;
        this.ChatColor = ChatColor;
        this.DateColor = DateColor;
        this.SubColor = SubColor;
        this.DomColor = DomColor;
        this.Friend1Color = Friend1Color;
        this.Friend2Color = Friend2Color;
        this.Friend3Color = Friend3Color;
    }
    
}
