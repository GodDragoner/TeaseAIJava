package me.goddragon.teaseai.gui.settings;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import me.goddragon.teaseai.TeaseAI;

/**
 * Created by GodDragon on 04.04.2018.
 */
public class FetishesSettings {

    private final SettingsController settingsController;

    public FetishesSettings(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    public void initiate() {
        settingsController.saveFetishesSettingsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                saveSettings();
            }
        });
        
        String[] fetishOptions = new String[] {"Undecided", "Dislike", "Ok", "Like", "Love", "Ok as Punishment"};
            
        for (String thisOption: fetishOptions)
        {
            settingsController.analBox.getItems().add(thisOption);
        }
        settingsController.analBox.setValue(TeaseAI.application.FETISH_ANAL.getValue().toString());
        for (String thisOption: fetishOptions)
        {
            settingsController.feetBox.getItems().add(thisOption);
        }
        settingsController.feetBox.setValue(TeaseAI.application.FETISH_FEET.getValue().toString());
        for (String thisOption: fetishOptions)
        {
            settingsController.cocktortureBox.getItems().add(thisOption);
        }
        settingsController.cocktortureBox.setValue(TeaseAI.application.FETISH_COCKTORTURE.getValue().toString());
        for (String thisOption: fetishOptions)
        {
            settingsController.balltortureBox.getItems().add(thisOption);
        }
        settingsController.balltortureBox.setValue(TeaseAI.application.FETISH_BALLTORTURE.getValue().toString());
        for (String thisOption: fetishOptions)
        {
            settingsController.nippletortureBox.getItems().add(thisOption);
        }
        settingsController.nippletortureBox.setValue(TeaseAI.application.FETISH_NIPPLETORTURE.getValue().toString());
        for (String thisOption: fetishOptions)
        {
            settingsController.cumeatingBox.getItems().add(thisOption);
        }
        settingsController.cumeatingBox.setValue(TeaseAI.application.FETISH_CUMEATING.getValue().toString());
        for (String thisOption: fetishOptions)
        {
            settingsController.exerciseBox.getItems().add(thisOption);
        }
        settingsController.exerciseBox.setValue(TeaseAI.application.FETISH_EXERCISE.getValue().toString());
        for (String thisOption: fetishOptions)
        {
            settingsController.bdsmpositionsBox.getItems().add(thisOption);
        }
        settingsController.bdsmpositionsBox.setValue(TeaseAI.application.FETISH_BDSMPOSITIONS.getValue().toString());
        for (String thisOption: fetishOptions)
        {
            settingsController.bondageBox.getItems().add(thisOption);
        }
        settingsController.bondageBox.setValue(TeaseAI.application.FETISH_BONDAGE.getValue().toString());
        for (String thisOption: fetishOptions)
        {
            settingsController.sissyBox.getItems().add(thisOption);
        }
        settingsController.sissyBox.setValue(TeaseAI.application.FETISH_SISSY.getValue().toString());
        for (String thisOption: fetishOptions)
        {
            settingsController.cocksuckingBox.getItems().add(thisOption);
        }
        settingsController.cocksuckingBox.setValue(TeaseAI.application.FETISH_COCKSUCKING.getValue().toString());
        for (int i = 0; i < fetishOptions.length; i++)
        {
            if (i == 1)
            {
                settingsController.selffellatioBox.getItems().add("Unable or Dislike");
                continue;
            }
            settingsController.selffellatioBox.getItems().add(fetishOptions[i]);           
        }
        settingsController.selffellatioBox.setValue(TeaseAI.application.FETISH_SELFFELLATIO.getValue().toString());
        for (String thisOption: fetishOptions)
        {
            settingsController.bodymarkingBox.getItems().add(thisOption);
        }
        settingsController.bodymarkingBox.setValue(TeaseAI.application.FETISH_BODYMARKING.getValue().toString());
        for (String thisOption: fetishOptions)
        {
            settingsController.bladdercontrolBox.getItems().add(thisOption);
        }
        settingsController.bladdercontrolBox.setValue(TeaseAI.application.FETISH_BLADDERCONTROL.getValue().toString());
    }

    public void saveSettings() {
        TeaseAI.application.FETISH_ANAL.setValue(settingsController.analBox.getSelectionModel().getSelectedItem().toString()).save();
        TeaseAI.application.FETISH_BALLTORTURE.setValue(settingsController.balltortureBox.getSelectionModel().getSelectedItem().toString()).save();
        TeaseAI.application.FETISH_BDSMPOSITIONS.setValue(settingsController.bdsmpositionsBox.getSelectionModel().getSelectedItem().toString()).save();
        TeaseAI.application.FETISH_BLADDERCONTROL.setValue(settingsController.bladdercontrolBox.getSelectionModel().getSelectedItem().toString()).save();
        TeaseAI.application.FETISH_BODYMARKING.setValue(settingsController.bodymarkingBox.getSelectionModel().getSelectedItem().toString()).save();
        TeaseAI.application.FETISH_BONDAGE.setValue(settingsController.bondageBox.getSelectionModel().getSelectedItem().toString()).save();
        TeaseAI.application.FETISH_COCKSUCKING.setValue(settingsController.cocksuckingBox.getSelectionModel().getSelectedItem().toString()).save();
        TeaseAI.application.FETISH_COCKTORTURE.setValue(settingsController.cocktortureBox.getSelectionModel().getSelectedItem().toString()).save();
        TeaseAI.application.FETISH_CUMEATING.setValue(settingsController.cumeatingBox.getSelectionModel().getSelectedItem().toString()).save();
        TeaseAI.application.FETISH_EXERCISE.setValue(settingsController.exerciseBox.getSelectionModel().getSelectedItem().toString()).save();
        TeaseAI.application.FETISH_FEET.setValue(settingsController.feetBox.getSelectionModel().getSelectedItem().toString()).save();
        TeaseAI.application.FETISH_NIPPLETORTURE.setValue(settingsController.nippletortureBox.getSelectionModel().getSelectedItem().toString()).save();
        TeaseAI.application.FETISH_SELFFELLATIO.setValue(settingsController.selffellatioBox.getSelectionModel().getSelectedItem().toString()).save();
        TeaseAI.application.FETISH_SISSY.setValue(settingsController.sissyBox.getSelectionModel().getSelectedItem().toString()).save();
    }
}
