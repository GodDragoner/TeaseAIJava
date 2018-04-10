package me.goddragon.teaseai.api.config;

/**
 * Created by GodDragon on 06.04.2018.
 */
public class PersonalityVariable {

    private final String configName;
    private Object value;

    private String customName;
    private String description;
    private boolean supportedByPersonality = false;


    public PersonalityVariable(String configName, Object value) {
        this.configName = configName.toLowerCase();
        this.value = value;
    }

    public PersonalityVariable(String configName, Object value, String customName, String description) {
        this.configName = configName.toLowerCase();
        this.value = value;
        this.customName = customName;
        this.description = description;
        this.supportedByPersonality = true;
    }

    public String getConfigName() {
        return configName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSupportedByPersonality() {
        return supportedByPersonality;
    }

    public void setSupportedByPersonality(boolean supportedByPersonality) {
        this.supportedByPersonality = supportedByPersonality;
    }

    @Override
    public String toString() {
        return customName != null? customName : configName;
    }
}
