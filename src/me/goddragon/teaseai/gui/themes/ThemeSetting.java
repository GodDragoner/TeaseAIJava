package me.goddragon.teaseai.gui.themes;

import javafx.scene.control.Control;
import me.goddragon.teaseai.api.config.ConfigValue;
import me.goddragon.teaseai.utils.StringUtils;

public abstract class ThemeSetting {

    protected String name;
    protected String configName;
    protected ConfigValue configValue;

    protected Theme theme;

    public ThemeSetting(String name, Theme theme) {
        this.name = name;
        this.configName = StringUtils.decapitalize(this.name.replace(" ", ""));
        this.theme = theme;
    }

    public abstract Control getControlElement();

    public abstract void fetchFromConfig();

    abstract void applyToGui();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfigName() {
        return configName;
    }

    public ConfigValue getConfigValue() {
        return configValue;
    }

    public Theme getTheme() {
        return theme;
    }
}
