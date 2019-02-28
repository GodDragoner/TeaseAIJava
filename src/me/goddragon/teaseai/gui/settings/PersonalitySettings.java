package me.goddragon.teaseai.gui.settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.config.PersonalitiesSettingsHandler;
import me.goddragon.teaseai.api.config.PersonalitySettingsPanel;
import me.goddragon.teaseai.api.config.PersonalityVariable;
import me.goddragon.teaseai.api.config.VariableHandler;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;

import java.util.ArrayList;
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
            PersonalitySettingsPanel panel = personality.getSettingsHandler().getPanel("General Settings");
            boolean needToAdd = true;
            for (Tab tab : PersonalitiesSettingsHandler.getHandler().getTabsToAdd()) {
                if (tab.getText().equals(personality.getName().getValue())) {
                    needToAdd = false;
                }
            }
            for (PersonalityVariable thisVar : variableHandler.getVariables().values()) {
                if (!PersonalitiesSettingsHandler.getHandler().hasComponent(thisVar)) {

                    if (thisVar.isSupportedByPersonality()) {
                        if (needToAdd) {
                            if (personality.getSettingsHandler().getPanel("General Settings") == null) {
                                personality.getSettingsHandler().addPanel("General Settings");
                                panel = personality.getSettingsHandler().getPanel("General Settings");
                            }
                            needToAdd = false;
                        }
                        if (thisVar.getValue() == Boolean.FALSE || thisVar.getValue() == Boolean.TRUE) {
                            PersonalitiesSettingsHandler.getHandler().addGuiComponent(thisVar);
                            panel.addCheckBox(thisVar);
                        } else if (thisVar.getValue() instanceof String) {
                            PersonalitiesSettingsHandler.getHandler().addGuiComponent(thisVar);
                            panel.addTextBox(thisVar);
                        }
                    }
                }
            }

            for (PersonalitySettingsPanel panel2 : personality.getSettingsHandler().getSettingsPanels()) {
                panel2.addGuiComponents();
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
