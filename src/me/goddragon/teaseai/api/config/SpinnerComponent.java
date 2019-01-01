package me.goddragon.teaseai.api.config;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.function.UnaryOperator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

public class SpinnerComponent extends GUIComponent
{
    private PersonalityVariable variable;
    private Spinner<Integer> spinner;
    private int min;
    private int max;
    
    public SpinnerComponent(PersonalityVariable variable, String settingString, int min, int max)
    {
        super(settingString);
        // TODO Auto-generated constructor stub
        this.variable = variable;
        this.min = min;
        this.max = max;
        setUp();
    }

    public SpinnerComponent(PersonalityVariable variable, String settingString, int min, int max, String description)
    {
        super(settingString, description);
        this.variable = variable;
        this.min = min;
        this.max = max;
        setUp();
    }
    private void setUp()
    {
        int startingValue = min;
        if (variable.getValue() instanceof Integer)
        {
            startingValue = (Integer) variable.getValue();
        }
        else if (isInteger((String)variable.getValue()))
        {
            startingValue = Integer.parseInt((String)variable.getValue());
        }
        //spinner = new Spinner<>(min, max, startingValue);
        
        spinner = new Spinner<>(min, max, startingValue);
        NumberFormat format = NumberFormat.getIntegerInstance();
        UnaryOperator<TextFormatter.Change> filter = c -> {
            if (c.isContentChange()) {
                ParsePosition parsePosition = new ParsePosition(0);
                // NumberFormat evaluates the beginning of the text
                format.parse(c.getControlNewText(), parsePosition);
                if (parsePosition.getIndex() == 0 ||
                        parsePosition.getIndex() < c.getControlNewText().length()) {
                    // reject parsing the complete text failed
                    return null;
                }
            }
            return c;
        };
        TextFormatter<Integer> priceFormatter = new TextFormatter<Integer>(
                new IntegerStringConverter(), startingValue, filter);

        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                min, max, startingValue));
        spinner.setEditable(true);
        spinner.getEditor().setTextFormatter(priceFormatter);
        spinner.getValueFactory().setValue(startingValue);
        
        spinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                    variable.setValue(newValue);
            }
        });
        
        this.setting = spinner;
    }
    private static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    private static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
}
