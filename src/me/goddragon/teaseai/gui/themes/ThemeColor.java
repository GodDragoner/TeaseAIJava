package me.goddragon.teaseai.gui.themes;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.Control;
import javafx.scene.paint.Color;
import me.goddragon.teaseai.api.config.ConfigValue;

public abstract class ThemeColor extends ThemeSetting {

    private ColorPicker colorPicker = new ColorPicker();
    protected Color color = Color.WHITE;

    public ThemeColor(String name, Theme theme) {
        super(name, theme);
        this.configValue = new ConfigValue(this.configName, this.color, theme.getConfigHandler());
    }

    public ThemeColor(String name, Color color, Theme theme) {
        super(name, theme);
        this.color = color;
        this.configValue = new ConfigValue(this.configName, this.color, theme.getConfigHandler());
    }

    @Override
    public Control getControlElement() {
        return colorPicker;
    }

    @Override
    public void fetchFromConfig() {
        this.color = Color.valueOf(this.configValue.getValue());
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        this.colorPicker.setValue(color);
    }

    public String getCSSColorString() {
        return this.color.toString().replace("0x", "#");
    }
}
