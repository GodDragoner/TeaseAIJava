package me.goddragon.teaseai.api.config;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.ArrayList;
import java.util.logging.Level;

public class OptionsListComponent extends VariableBasedComponent {
    private ArrayList<String> options;
    private ChoiceBox<String> choiceBox;

    public OptionsListComponent(PersonalityVariable variable, String settingString, ArrayList<String> options) {
        super(settingString, variable.getDescription(), variable);
        this.options = options;
        setUp();
    }

    public OptionsListComponent(PersonalityVariable variable, String settingString, ArrayList<String> options, String description) {
        super(settingString, description, variable);
        this.options = options;
        setUp();
    }

    private void setUp() {
        choiceBox = new ChoiceBox<>(FXCollections.observableArrayList(options));
        String toSet = "";
        for (String thisString : options) {
            if (variable.getValue().equals(thisString)) {
                toSet = thisString;
            }
        }
        if (!toSet.equals("")) {
            choiceBox.getSelectionModel().select(toSet);
        } else {
            TeaseLogger.getLogger().log(Level.WARNING, "Current value " + variable.getValue() + " of variable " + variable.getConfigName() + " does not match any of its possible options");
        }
        choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                variable.setValue(choiceBox.getItems().get((Integer) number2));
            }
        });
        this.setting = choiceBox;
    }
}
