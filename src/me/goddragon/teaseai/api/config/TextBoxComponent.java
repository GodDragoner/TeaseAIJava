package me.goddragon.teaseai.api.config;

import javafx.scene.control.TextField;

public class TextBoxComponent extends VariableBasedComponent {
    private TextField textField;

    public TextBoxComponent(PersonalityVariable variable, String settingString) {
        super(settingString, variable.getDescription(), variable);
        setUp();
    }

    public TextBoxComponent(PersonalityVariable variable, String settingString, String description) {
        super(settingString, description, variable);
        setUp();
    }

    private void setUp() {
        this.textField = new TextField();

        if (variable.getValue() != null) {
            textField.setText(variable.getValue().toString());
        }

        textField.textProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> variable.setValueAndSave(newPropertyValue));

        setSetting(textField);
    }

}
