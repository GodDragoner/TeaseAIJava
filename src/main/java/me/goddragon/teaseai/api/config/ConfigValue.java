package me.goddragon.teaseai.api.config;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class ConfigValue {
    private final String name;
    private final Object defaultValue;
    private final ConfigHandler configHandler;

    private String value;

    public ConfigValue(String name, Object defaultValue, ConfigHandler configHandler) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.configHandler = configHandler;
        configHandler.getConfigValues().add(this);
    }


    public ConfigValue setValue(String value) {
        this.value = value;
        configHandler.getProperties().setProperty(name, value);
        return this;
    }

    public String getValue() {
        if (value != null) {
            return value;
        }

        if (configHandler.getProperties().containsKey(name)) {
            value = configHandler.getProperties().getProperty(name);
            return value;
        }

        createDefault();

        if (configHandler.isConfig()) {
            save();
        }

        this.value = defaultValue.toString();
        return defaultValue.toString();
    }

    public void reloadValue() {
        value = null;
        getValue();
    }

    public boolean getBoolean() {
        return Boolean.valueOf(getValue());
    }

    public int getInt() {
        return Integer.parseInt(getValue());
    }

    public double getDouble() {
        return Double.parseDouble(getValue());
    }

    public void createDefault() {
        configHandler.getProperties().setProperty(name, defaultValue.toString());
    }

    public void save() {
        configHandler.saveConfig();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getValue();
    }

    public Object getDefaultValue() {
        return defaultValue;
    }


}
