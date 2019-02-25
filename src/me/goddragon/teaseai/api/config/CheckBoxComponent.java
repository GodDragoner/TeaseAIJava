package me.goddragon.teaseai.api.config;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;

public class CheckBoxComponent extends GUIComponent
{

    private PersonalityVariable variable;
    private CheckBox checkBox;
    
    public CheckBoxComponent(PersonalityVariable variable, String settingString)
    {
        super(settingString, variable.getDescription());
        // TODO Auto-generated constructor stub
        this.variable = variable;
        setUp();
    }

    public CheckBoxComponent(PersonalityVariable variable, String settingString, String description)
    {
        super(settingString, description);
        this.variable = variable;
        setUp();
    }
    private void setUp()
    {
        this.checkBox = new CheckBox();
        if (variable.getValue() == Boolean.TRUE)
        {
            checkBox.setSelected(true);
        }
        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (checkBox.isSelected())
                {
                    variable.setValue(true);
                }
                else
                {
                    variable.setValue(false);
                }
            }
        });
        this.setting = checkBox;
    }
}
