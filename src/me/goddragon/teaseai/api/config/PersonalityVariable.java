package me.goddragon.teaseai.api.config;

/**
 * Created by GodDragon on 06.04.2018.
 */
public class PersonalityVariable<T> {

    private final String configName;
    private Object value;

    //The personality this variable belongs to
    private String personalityName;
    private String customName;
    private String description;
    private boolean supportedByPersonality = false;
    private boolean temporary = false;

    public PersonalityVariable(String configName, Object value, String personalityName) {
        this.configName = configName.toLowerCase();
        this.value = value;
        this.personalityName = personalityName;
    }

    public PersonalityVariable(String configName, Object value, String customName, String description, String personalityName) {
        this.configName = configName.toLowerCase();
        this.value = value;
        this.customName = customName;
        this.description = description;
        this.supportedByPersonality = true;
        this.personalityName = personalityName;
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

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

    public boolean isTemporary() {
        return temporary;
    }

    @Override
    public String toString() {
        return customName != null ? customName : configName;
    }

    public boolean equals(PersonalityVariable variable) {
        return configName.equals(variable.getConfigName()) && customName.equals(variable.getCustomName()) && personalityName.equals(variable.getPersonalityString());
    }

    public String getPersonalityString() {
        return personalityName;
    }
}
