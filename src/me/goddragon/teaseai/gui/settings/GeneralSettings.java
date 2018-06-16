package me.goddragon.teaseai.gui.settings;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.chat.ChatParticipant;
import me.goddragon.teaseai.api.chat.TypeSpeed;

import java.util.function.UnaryOperator;

/**
 * Created by GodDragon on 04.04.2018.
 */
public class GeneralSettings {

    private final SettingsController settingsController;

    public GeneralSettings(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    public void initiate() {
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?([1-9][0-9]*)?")) {
                return change;
            }
            return null;
        };

        settingsController.preferredTeaseLengthField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));

        settingsController.preferredTeaseLengthField.setText(TeaseAI.application.PREFERRED_SESSION_DURATION.getValue());

        settingsController.saveGeneralSettingsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                saveSettings();
            }
        });

        for(double x = 10; x <= 60; x+= .5) {
            settingsController.fontSizeComboBox.getItems().add(x);
        }

        settingsController.fontSizeComboBox.getSelectionModel().select(TeaseAI.application.CHAT_TEXT_SIZE.getDouble());

        for(TypeSpeed typeSpeed : TypeSpeed.values()) {
            settingsController.defaultTypeSpeedComboBox.getItems().add(typeSpeed);
        }

        settingsController.defaultTypeSpeedComboBox.getSelectionModel().select(TypeSpeed.valueOf(TeaseAI.application.DEFAULT_TYPE_SPEED.getValue()));
    }

    public void saveSettings() {
        TeaseAI.application.PREFERRED_SESSION_DURATION.setValue(settingsController.preferredTeaseLengthField.getText()).save();
        TeaseAI.application.CHAT_TEXT_SIZE.setValue(settingsController.fontSizeComboBox.getSelectionModel().getSelectedItem().toString()).save();
        TeaseAI.application.DEFAULT_TYPE_SPEED.setValue(settingsController.defaultTypeSpeedComboBox.getSelectionModel().getSelectedItem().toString()).save();

        //if the session hasn't started yet we can adjust the type speed
        if(!TeaseAI.application.getSession().isStarted()) {
            for(ChatParticipant chatParticipant : ChatHandler.getHandler().getParticipants()) {
                chatParticipant.setTypeSpeed(TypeSpeed.valueOf(TeaseAI.application.DEFAULT_TYPE_SPEED.getValue()));
            }
        }
    }
}
