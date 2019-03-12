package me.goddragon.teaseai.gui.themes;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.Control;
import javafx.scene.paint.Color;
import me.goddragon.teaseai.api.config.ConfigValue;

public abstract class ThemeColor extends ThemeSetting {

    protected ColorPicker colorPicker = new ColorPicker();
    protected Color color;


    public ThemeColor(String name, Theme theme) {
        this(name, Color.WHITE, theme);
    }

    public ThemeColor(String name, Color color, Theme theme) {
        super(name, theme);
        this.color = color;
        this.colorPicker.setValue(color);
        this.configValue = new ConfigValue(this.configName, this.color.toString(), theme.getConfigHandler());

        this.colorPicker.setOnAction(e -> {
            updateColor();
            applyToGui();

            theme.applyCSSFile();
        });
    }

    @Override
    public Control getControlElement() {
        return colorPicker;
    }

    @Override
    public void fetchFromConfig() {
        this.setColor(Color.valueOf(this.configValue.getValue()));
    }

    public Color getColor() {
        return color;
    }

    protected void updateColor() {
        this.color = colorPicker.getValue();
        this.configValue.setValue(this.color.toString());
    }

    public void setColor(Color color) {
        this.colorPicker.setValue(color);
        updateColor();
    }


    public String getCSSColorString() {
        return this.color.toString().replace("0x", "#");
    }
}
