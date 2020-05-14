package me.goddragon.teaseai.api.config;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.DoubleStringConverter;

import java.text.NumberFormat;

public class DoubleSpinnerComponent extends SpinnerComponent {
    private Spinner<Double> spinner;
    private final double min;
    private final double max;

    public DoubleSpinnerComponent(PersonalityVariable variable, String settingString, double min, double max) {
        super(settingString, variable.getDescription(), variable);
        this.min = min;
        this.max = max;

        setUp();
    }

    public DoubleSpinnerComponent(PersonalityVariable variable, String settingString, double min, double max, String description) {
        super(settingString, description, variable);
        this.min = min;
        this.max = max;

        setUp();
    }

    private void setUp() {
        double startingValue = min;

        if (variable.getValue() instanceof Integer) {
            startingValue = (Integer) variable.getValue();
        } else {
            try {
                startingValue = Integer.parseInt((String) variable.getValue());
            } catch (NumberFormatException ex) {
                handleMissAssignedValue("Double");
            }
        }

        //spinner = new Spinner<>(min, max, startingValue);

        spinner = new Spinner<>(min, max, startingValue);

        NumberFormat format = NumberFormat.getNumberInstance();

        TextFormatter<Double> priceFormatter = new TextFormatter<>(new DoubleStringConverter(), startingValue, getFilter(format));

        spinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, startingValue));
        spinner.setEditable(true);
        spinner.getEditor().setTextFormatter(priceFormatter);
        spinner.getValueFactory().setValue(startingValue);

        spinner.valueProperty().addListener((observable, oldValue, newValue) -> variable.setValueAndSave(newValue));

        this.setting = spinner;
    }
}
