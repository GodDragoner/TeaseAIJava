package me.goddragon.teaseai.gui.settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;
import me.goddragon.teaseai.TeaseAI;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import devices.TwoB.TwoBMode;
import estimAPI.Mode;

/**
 * Created by xman2B on 07.07.2019.
 */
public class EstimSettings {

    private final SettingsController settingsController;
    private final Set<Mode> enabledModes = string2HashSet(TeaseAI.application.ESTIM_METRONOME_ENABLED_MODES.getValue());
    private HashMap<Mode, CheckBox> checkBoxes = new HashMap<>();


    private String hashSet2String(Set<Mode> hashSet) {
    	return hashSet.stream()
    			.map(Object::toString)
    			.collect(Collectors.joining(","));
    }
    
    private Set<Mode> string2HashSet(String string) {
    	if (string.isEmpty()) {
    		return new HashSet<Mode>();
    	}
    	else {
    		return Stream.of(string.split(","))
        			.map(mode -> TwoBMode.valueOf(mode))
        			.collect(Collectors.toSet());
    	}
    }
    
    public EstimSettings(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    public void initiate() {
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            // Matches 0-infinity
            if (newText.matches("^([0-9]*)$")) {
                return change;
            }
            return null;
        };
        
        UnaryOperator<TextFormatter.Change> integerFilter0until100 = change -> {
            String newText = change.getControlNewText();
            // Matches 0-100
            if (newText.matches("^(100|[0-9]{0,2})$")) {
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
        
        settingsController.estimMetronomeUserControlsPower.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                TeaseAI.application.ESTIM_METRONOME_USER_CONTROLS_POWER.setValue(newValue.toString()).save();
            }
        });
        settingsController.estimMetronomeUserControlsPower.selectedProperty().set(TeaseAI.application.ESTIM_METRONOME_USER_CONTROLS_POWER.getBoolean());

        
        settingsController.estimDevicePathField.setText(TeaseAI.application.ESTIM_DEVICE_PATH.getValue());
        settingsController.estimDevicePathField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TeaseAI.application.ESTIM_DEVICE_PATH.setValue(settingsController.estimDevicePathField.getText()).save();
            }
        });
        
        
        addTagsToButton(settingsController.estimMetronomeEnabledModes);
        
        
        settingsController.estimMetronomeBPMMin.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
        settingsController.estimMetronomeBPMMin.setText(TeaseAI.application.ESTIM_METRONOME_BPM_MIN.getValue());
        settingsController.estimMetronomeBPMMin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TeaseAI.application.ESTIM_METRONOME_BPM_MIN.setValue(settingsController.estimMetronomeBPMMin.getText()).save();
            }
        });
        settingsController.estimMetronomeBPMMax.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
        settingsController.estimMetronomeBPMMax.setText(TeaseAI.application.ESTIM_METRONOME_BPM_MAX.getValue());
        settingsController.estimMetronomeBPMMax.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TeaseAI.application.ESTIM_METRONOME_BPM_MAX.setValue(settingsController.estimMetronomeBPMMax.getText()).save();
            }
        });
        
        // Min/Max values of channels
        settingsController.estimChannelAMin.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter0until100));
        settingsController.estimChannelAMin.setText(TeaseAI.application.ESTIM_CHANNEL_A_MIN.getValue());
        settingsController.estimChannelAMin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TeaseAI.application.ESTIM_CHANNEL_A_MIN.setValue(settingsController.estimChannelAMin.getText()).save();
            }
        });
        settingsController.estimChannelAMax.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter0until100));
        settingsController.estimChannelAMax.setText(TeaseAI.application.ESTIM_CHANNEL_A_MAX.getValue());
        settingsController.estimChannelAMax.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TeaseAI.application.ESTIM_CHANNEL_A_MAX.setValue(settingsController.estimChannelAMax.getText()).save();
            }
        });
        
        settingsController.estimChannelBMin.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter0until100));
        settingsController.estimChannelBMin.setText(TeaseAI.application.ESTIM_CHANNEL_B_MIN.getValue());
        settingsController.estimChannelBMin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TeaseAI.application.ESTIM_CHANNEL_B_MIN.setValue(settingsController.estimChannelBMin.getText()).save();
            }
        });
        settingsController.estimChannelBMax.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter0until100));
        settingsController.estimChannelBMax.setText(TeaseAI.application.ESTIM_CHANNEL_B_MAX.getValue());
        settingsController.estimChannelBMax.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TeaseAI.application.ESTIM_CHANNEL_B_MAX.setValue(settingsController.estimChannelBMax.getText()).save();
            }
        });
        
        settingsController.estimChannelCMin.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter0until100));
        settingsController.estimChannelCMin.setText(TeaseAI.application.ESTIM_CHANNEL_C_MIN.getValue());
        settingsController.estimChannelCMin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TeaseAI.application.ESTIM_CHANNEL_C_MIN.setValue(settingsController.estimChannelCMin.getText()).save();
            }
        });
        settingsController.estimChannelCMax.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter0until100));
        settingsController.estimChannelCMax.setText(TeaseAI.application.ESTIM_CHANNEL_C_MAX.getValue());
        settingsController.estimChannelCMax.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TeaseAI.application.ESTIM_CHANNEL_C_MAX.setValue(settingsController.estimChannelCMax.getText()).save();
            }
        });
        
        settingsController.estimChannelDMin.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter0until100));
        settingsController.estimChannelDMin.setText(TeaseAI.application.ESTIM_CHANNEL_D_MIN.getValue());
        settingsController.estimChannelDMin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TeaseAI.application.ESTIM_CHANNEL_D_MIN.setValue(settingsController.estimChannelDMin.getText()).save();
            }
        });
        settingsController.estimChannelDMax.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter0until100));
        settingsController.estimChannelDMax.setText(TeaseAI.application.ESTIM_CHANNEL_D_MAX.getValue());
        settingsController.estimChannelDMax.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TeaseAI.application.ESTIM_CHANNEL_D_MAX.setValue(settingsController.estimChannelDMax.getText()).save();
            }
        });

    }
    
    public void addTagsToButton(MenuButton button) {
    	Mode[] availableModes = TwoBMode.values();
    	if (button.getItems().isEmpty()) {
            for (int i = 0; i < availableModes.length; i++) {
                CheckBox thisCheckBox = new CheckBox(availableModes[i].toString());
                checkBoxes.put(availableModes[i], thisCheckBox);
                
                Mode thisMode = availableModes[i];
                thisCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if (newValue) {
                            enabledModes.add(thisMode);
                        } else {
                            enabledModes.remove(thisMode);
                        }
                        TeaseAI.application.ESTIM_METRONOME_ENABLED_MODES.setValue(hashSet2String(enabledModes)).save();;
                    }
                });

                CustomMenuItem thisItem = new CustomMenuItem(thisCheckBox);
                thisItem.setHideOnClick(false);
                button.getItems().add(thisItem);
            }
        }
    	
    	for (Mode mode : availableModes) {
            if (enabledModes.contains(mode)) {
                checkBoxes.get(mode).setSelected(true);
            } else {
                checkBoxes.get(mode).setSelected(false);
            }
        }
    }
}
