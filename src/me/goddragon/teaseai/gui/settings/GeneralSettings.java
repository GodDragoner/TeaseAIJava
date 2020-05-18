package me.goddragon.teaseai.gui.settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.chat.ChatParticipant;
import me.goddragon.teaseai.api.chat.TypeSpeed;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.function.UnaryOperator;
import java.util.logging.Level;

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

        settingsController.preferredTeaseLengthField.textProperty().addListener((observable, oldValue, newValue) ->
                TeaseAI.application.PREFERRED_SESSION_DURATION.setValue(settingsController.preferredTeaseLengthField.getText()).save());

        settingsController.fontSizeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                TeaseAI.application.CHAT_TEXT_SIZE.setValue(settingsController.fontSizeComboBox.getSelectionModel().getSelectedItem().toString()).save());

        settingsController.defaultTypeSpeedComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            TeaseAI.application.DEFAULT_TYPE_SPEED.setValue(settingsController.defaultTypeSpeedComboBox.getSelectionModel().getSelectedItem().toString()).save();

            //if the session hasn't started yet we can adjust the type speed
            if (!TeaseAI.application.getSession().isStarted()) {
                for (ChatParticipant chatParticipant : ChatHandler.getHandler().getParticipants()) {
                    chatParticipant.setTypeSpeed(TypeSpeed.valueOf(TeaseAI.application.DEFAULT_TYPE_SPEED.getValue()));
                }
            }
        });
        
        settingsController.debugCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                TeaseAI.application.DEBUG_MODE.setValue(newValue.toString()).save();
            }});
        settingsController.textToSpeechComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equalsIgnoreCase(TextToSpeechType.DISABLED.getName())) {
                TeaseAI.application.TEXT_TO_SPEECH.setValue(TextToSpeechType.DISABLED.getId() + "").save();
                TeaseAI.application.setTTS(false);
            } else if (newValue.equalsIgnoreCase(TextToSpeechType.ENABLED.getName())) {
                TeaseAI.application.TEXT_TO_SPEECH.setValue(TextToSpeechType.ENABLED.getId() + "").save();
                TeaseAI.application.setTTS(true);
            } else if (newValue.equalsIgnoreCase(TextToSpeechType.PERSONALITY.getName())) {
                TeaseAI.application.TEXT_TO_SPEECH.setValue(TextToSpeechType.PERSONALITY.getId() + "").save();
                TeaseAI.application.setTTS(false);
            } else {
                TeaseLogger.getLogger().log(Level.SEVERE, "Unknown text to speech value " + newValue);
            }
        });
        
        settingsController.capitalizeTextCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                TeaseAI.application.AUTO_CAPITALIZE.setValue(newValue.toString()).save();
            }
        });
        
        settingsController.debugCheckbox.selectedProperty().set(TeaseAI.application.DEBUG_MODE.getBoolean());
        
        settingsController.capitalizeTextCheckBox.selectedProperty().set(TeaseAI.application.AUTO_CAPITALIZE.getBoolean());

        settingsController.textToSpeechComboBox.getItems().addAll(TextToSpeechType.DISABLED.getName(), TextToSpeechType.ENABLED.getName(), TextToSpeechType.PERSONALITY.getName());
        int textToSpeechInt = TeaseAI.application.TEXT_TO_SPEECH.getInt();

        if (textToSpeechInt == TextToSpeechType.DISABLED.getId()) {
            settingsController.textToSpeechComboBox.getSelectionModel().select(TextToSpeechType.DISABLED.getName());
        } else if (textToSpeechInt == TextToSpeechType.ENABLED.getId()) {
            settingsController.textToSpeechComboBox.getSelectionModel().select(TextToSpeechType.ENABLED.getName());
        } else {
            settingsController.textToSpeechComboBox.getSelectionModel().select(TextToSpeechType.PERSONALITY.getName());
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

    public enum TextToSpeechType {
        DISABLED("Disabled", 0),
        ENABLED("Enabled", 1),
        PERSONALITY("Personality Decides (Recommended)", 2);

        private String name;
        private int id;

        TextToSpeechType(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
