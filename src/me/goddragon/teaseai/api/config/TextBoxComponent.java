package me.goddragon.teaseai.api.config;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class TextBoxComponent extends GUIComponent
{
    private PersonalityVariable variable;
    private TextField textField;
    
    public TextBoxComponent(PersonalityVariable variable, String settingString)
    {
        super(settingString);
        // TODO Auto-generated constructor stub
        this.variable = variable;
        setUp();
    }

    public TextBoxComponent(PersonalityVariable variable, String settingString, String description)
    {
        super(settingString, description);
        this.variable = variable;
        setUp();
    }
    private void setUp()
    {
        this.textField = new TextField();
        if (variable.getValue() != null)
        {
            textField.setText(variable.getValue().toString());
        }
        textField.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
            {
                if (!newPropertyValue)
                {
                    variable.setValue(textField.getText());
                }
            }
        });
        this.setting = textField;
    }

}
