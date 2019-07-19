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
public class EstimSettings {

    private final SettingsController settingsController;

    public EstimSettings(SettingsController settingsController) {
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
        
        settingsController.estimEnabledCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                TeaseAI.application.ESTIM_ENABLED.setValue(newValue.toString()).save();
            }
        });

        settingsController.estimEnabledCheckbox.selectedProperty().set(TeaseAI.application.ESTIM_ENABLED.getBoolean());
        
        
        settingsController.estimMetronomeCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                TeaseAI.application.ESTIM_METRONOME.setValue(newValue.toString()).save();
            }
        });

        settingsController.estimMetronomeCheckbox.selectedProperty().set(TeaseAI.application.ESTIM_METRONOME.getBoolean());

    }
}
