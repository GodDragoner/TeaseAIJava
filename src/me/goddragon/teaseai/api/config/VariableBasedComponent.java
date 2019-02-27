package me.goddragon.teaseai.api.config;

import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.logging.Level;

public abstract class VariableBasedComponent extends GUIComponent {

    protected final PersonalityVariable variable;

    public VariableBasedComponent(String settingString, int columnNumber, PersonalityVariable variable) {
        super(settingString, columnNumber);
        this.variable = variable;
    }

    public VariableBasedComponent(String settingString, PersonalityVariable variable) {
        super(settingString);
        this.variable = variable;
    }

    public VariableBasedComponent(String settingString, String description, int columnNumber, PersonalityVariable variable) {
        super(settingString, description, columnNumber);
        this.variable = variable;
    }

    public VariableBasedComponent(String settingString, String description, PersonalityVariable variable) {
        super(settingString, description);
        this.variable = variable;
    }

    protected void handleMissAssignedValue(String expectedType) {
        TeaseLogger.getLogger().log(Level.SEVERE, "Variable '" + variable.getConfigName() + "' was assigned to the custom setting '" +
                "" + settingLabel.getText() + "' but is malformed/has the wrong type. Expected Type: " + expectedType);
    }
}
