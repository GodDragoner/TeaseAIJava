package me.goddragon.teaseai.gui.settings;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;
import me.goddragon.teaseai.TeaseAI;

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
    }

    public void saveSettings() {
        TeaseAI.application.PREFERRED_SESSION_DURATION.setValue(settingsController.preferredTeaseLengthField.getText()).save();

    }
}
