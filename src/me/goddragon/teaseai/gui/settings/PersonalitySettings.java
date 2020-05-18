package me.goddragon.teaseai.gui.settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.SelectionMode;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.config.PersonalitiesSettingsHandler;
import me.goddragon.teaseai.api.config.PersonalitySettingsPanel;
import me.goddragon.teaseai.api.config.PersonalityVariable;
import me.goddragon.teaseai.api.config.VariableHandler;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;

import java.util.TreeMap;

/**
 * Created by GodDragon on 05.04.2018.
 */
public class PersonalitySettings {

    private final VariableHandler variableHandler = TeaseAI.application.getSession().getActivePersonality() == null ? null : TeaseAI.application.getSession().getActivePersonality().getVariableHandler();
    private final SettingsController settingsController;

    public PersonalitySettings(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    public void initiate() {
        settingsController.onlySupportedVariablesCheckbox.setSelected(true);

        updateVariableList();

        addPersonalityGUIs();

        settingsController.variableListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PersonalityVariable>() {
            @Override
            public void changed(ObservableValue<? extends PersonalityVariable> observable, PersonalityVariable PersonalityVariable, PersonalityVariable newValue) {
                if (newValue != null) {
                    settingsController.variableValueTextField.setDisable(false);
                    updateVariableData();
                } else {
                    settingsController.variableValueTextField.setText("");
                    settingsController.descriptionLabel.setText("");
                    settingsController.variableValueTextField.setDisable(true);
                }
            }
        });

        settingsController.variableListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        settingsController.variableListView.getSelectionModel().selectFirst();

        settingsController.variableValueTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (variableHandler == null) {
                return;
            }

            PersonalityVariable personalityVariable = getSelectedVariable();

            if (personalityVariable == null) {
                return;
            }

            variableHandler.setVariable(personalityVariable.getConfigName(), variableHandler.getObjectFromString(newValue));
        });

        settingsController.onlySupportedVariablesCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                updateVariableList();
            }
        });
    }

    public void addPersonalityGUIs() {
        for (Personality personality : PersonalityManager.getManager().getPersonalities()) {
            VariableHandler variableHandler = personality.getVariableHandler();
            PersonalitySettingsPanel generalSettingsPanel = personality.getSettingsHandler().getPanel("General Settings");

            for (PersonalityVariable personalityVariable : variableHandler.getVariables().values()) {
                //Was the variable already assigned to a custom gui?
                if (!PersonalitiesSettingsHandler.getHandler().hasComponent(personalityVariable)) {
                    if (personalityVariable.isSupportedByPersonality()) {
                        //If we have a value we created the panel
                        if (generalSettingsPanel == null) {
                            personality.getSettingsHandler().addPanel("General Settings");
                            generalSettingsPanel = personality.getSettingsHandler().getPanel("General Settings");
                        }

                        if (personalityVariable.getValue() == Boolean.FALSE || personalityVariable.getValue() == Boolean.TRUE) {
                            PersonalitiesSettingsHandler.getHandler().addGuiComponent(personalityVariable);
                            generalSettingsPanel.addCheckBox(personalityVariable);
                        } else if (personalityVariable.getValue() instanceof String) {
                            PersonalitiesSettingsHandler.getHandler().addGuiComponent(personalityVariable);
                            generalSettingsPanel.addTextBox(personalityVariable);
                        }
                    }
                }
            }

            for (PersonalitySettingsPanel additionalPanel : personality.getSettingsHandler().getSettingsPanels()) {
                additionalPanel.addGuiComponents();
            }
        }
    }

    public void updateVariableList() {
        if (variableHandler != null) {
            settingsController.variableListView.getSelectionModel().clearSelection();
            settingsController.variableListView.getItems().clear();

            //ArrayList<PersonalitySettingsPanel> panels = TeaseAI.application.getSession().getActivePersonality().getSettingsHandler().getSettingsPanels();
            for (PersonalityVariable entry : new TreeMap<>(variableHandler.getVariables()).values()) {
                //TODO: Show and support array variables
                if (entry.getValue() != null && entry.getValue().getClass().isArray() || entry.getValue() instanceof ScriptObjectMirror && ((ScriptObjectMirror) entry.getValue()).isArray()) {
                    continue;
                }

                if (!settingsController.onlySupportedVariablesCheckbox.isSelected() || entry.isSupportedByPersonality()) {
                    settingsController.variableListView.getItems().add(entry);
                }
                
                /*if (entry.isSupportedByPersonality())
                {
                    if (entry.getValue() == Boolean.FALSE || entry.getValue() == Boolean.TRUE)
                    {
                        panels.get(panels.size() - 1).addCheckBox(entry);
                    }
                    else if (entry.getValue() instanceof String)
                    {
                        panels.get(panels.size() - 1).addTextBox(entry);
                    }
                }*/
            }
            //panels.get(panels.size() - 1).addGuiComponents();
        }

        if (settingsController.variableListView.getItems().isEmpty()) {
            settingsController.variableValueTextField.setDisable(true);
        }
    }

    public void updateVariableData() {
        PersonalityVariable variable = getSelectedVariable();

        if (variable != null && variableHandler != null) {
            Object object = variable.getValue();

            if (object != null) {
                settingsController.variableValueTextField.setText(object.toString());
            }

            if (variable.isSupportedByPersonality()) {
                settingsController.descriptionLabel.setText(variable.getDescription());
            } else {
                settingsController.descriptionLabel.setText("");
            }
        }
    }

    private PersonalityVariable getSelectedVariable() {
        if (settingsController.variableListView.getSelectionModel().getSelectedItems().size() == 1) {
            return (PersonalityVariable) settingsController.variableListView.getSelectionModel().getSelectedItem();
        }

        return null;
    }
}
