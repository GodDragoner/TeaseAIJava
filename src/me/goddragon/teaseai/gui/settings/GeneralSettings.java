package me.goddragon.teaseai.gui.settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

        settingsController.preferredTeaseLengthField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                TeaseAI.application.PREFERRED_SESSION_DURATION.setValue(settingsController.preferredTeaseLengthField.getText()).save();
            }
        });

        settingsController.fontSizeComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Double>() {

            @Override
            public void changed(ObservableValue<? extends Double> observable,
                                Double oldValue, Double newValue) {
                TeaseAI.application.CHAT_TEXT_SIZE.setValue(settingsController.fontSizeComboBox.getSelectionModel().getSelectedItem().toString()).save();
            }
        });

        settingsController.defaultTypeSpeedComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TypeSpeed>() {

            @Override
            public void changed(ObservableValue<? extends TypeSpeed> observable,
                                TypeSpeed oldValue, TypeSpeed newValue) {
                TeaseAI.application.DEFAULT_TYPE_SPEED.setValue(settingsController.defaultTypeSpeedComboBox.getSelectionModel().getSelectedItem().toString()).save();
                //if the session hasn't started yet we can adjust the type speed
                if (!TeaseAI.application.getSession().isStarted()) {
                    for (ChatParticipant chatParticipant : ChatHandler.getHandler().getParticipants()) {
                        chatParticipant.setTypeSpeed(TypeSpeed.valueOf(TeaseAI.application.DEFAULT_TYPE_SPEED.getValue()));
                    }
                }

            }
        });

        settingsController.textToSpeechComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                switch (newValue) {
                    case "Enabled":
                        TeaseAI.application.TEXT_TO_SPEECH.setValue("1").save();
                        TeaseAI.application.setTTS(true);

                        break;
                    case "Disabled":
                        TeaseAI.application.TEXT_TO_SPEECH.setValue("0").save();
                        TeaseAI.application.setTTS(false);

                        break;
                    case "Personality Decides (Recommended)":
                        TeaseAI.application.TEXT_TO_SPEECH.setValue("2").save();
                        TeaseAI.application.setTTS(false);
                        break;

                    default:
                        break;
                }
            }
        });

        settingsController.debugCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                TeaseAI.application.DEBUG_MODE.setValue(newValue.toString());
            }
        });

        settingsController.debugCheckbox.selectedProperty().set(TeaseAI.application.DEBUG_MODE.getBoolean());

        settingsController.textToSpeechComboBox.getItems().addAll("Personality Decides (Reccomended)", "Enabled", "Disabled");
        int varvalue = TeaseAI.application.TEXT_TO_SPEECH.getInt();

        if (varvalue == 0) {
            settingsController.textToSpeechComboBox.getSelectionModel().select("Disabled");
        } else if (varvalue == 1) {
            settingsController.textToSpeechComboBox.getSelectionModel().select("Enabled");
        } else {
            settingsController.textToSpeechComboBox.getSelectionModel().select("Personality Decides (Reccomended)");
        }

        for (double x = 10; x <= 60; x += .5) {
            settingsController.fontSizeComboBox.getItems().add(x);
        }

        settingsController.fontSizeComboBox.getSelectionModel().select(TeaseAI.application.CHAT_TEXT_SIZE.getDouble());

        for (TypeSpeed typeSpeed : TypeSpeed.values()) {
            settingsController.defaultTypeSpeedComboBox.getItems().add(typeSpeed);
        }

        settingsController.defaultTypeSpeedComboBox.getSelectionModel().select(TypeSpeed.valueOf(TeaseAI.application.DEFAULT_TYPE_SPEED.getValue()));
    }
}
