package me.goddragon.teaseai.gui.settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.config.PersonalityVariable;
import me.goddragon.teaseai.api.config.VariableHandler;

import java.util.Map;

/**
 * Created by GodDragon on 05.04.2018.
 */
public class DebugSettings {

    private final VariableHandler variableHandler = TeaseAI.application.getSession().getActivePersonality() == null? null : TeaseAI.application.getSession().getActivePersonality().getVariableHandler();
    private final SettingsController settingsController;

    public DebugSettings(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    public void initiate() {
        if(variableHandler != null) {
            for (Map.Entry<String, PersonalityVariable> entry : variableHandler.getVariables().entrySet()) {
                settingsController.variableListView.getItems().add(entry.getValue());
            }
        }

        settingsController.variableListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PersonalityVariable>() {
            @Override
            public void changed(ObservableValue<? extends PersonalityVariable> observable, PersonalityVariable PersonalityVariable, PersonalityVariable newValue) {
                if (newValue != null) {
                    settingsController.variableValueTextField.setDisable(false);
                    updateVariableData();
                } else {
                    settingsController.variableValueTextField.setDisable(true);
                }
            }
        });

        settingsController.variableListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        settingsController.variableListView.getSelectionModel().selectFirst();

        settingsController.variableSaveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String variable = getSelectedVariable();

                if (variable != null && variableHandler != null) {
                    variableHandler.setVariable(variable, variableHandler.getObjectFromString(settingsController.variableValueTextField.getText()));
                }
            }
        });
    }

    public void updateVariableData() {
        String variable = getSelectedVariable();

        if (variable != null && variableHandler != null) {
            Object object = variableHandler.getVariableValue(variable);

            if(object != null) {
                settingsController.variableValueTextField.setText(object.toString());
            }
        }
    }

    private String getSelectedVariable() {
        if (settingsController.variableListView.getSelectionModel().getSelectedItems().size() == 1) {
            return settingsController.variableListView.getSelectionModel().getSelectedItem().toString();
        }

        return null;
    }
}
