package me.goddragon.teaseai.api.config;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import me.goddragon.teaseai.gui.settings.MultiColumnSettingsPannel;

import java.util.ArrayList;

public class PersonalitySettingsPanel {
    private String name;
    private AnchorPane basePane;
    private final int rowHeight = 30;
    private ArrayList<GUISettingComponent> components;
    private MultiColumnSettingsPannel settingsPannel;

    public PersonalitySettingsPanel(String panelName) {
        this.name = panelName;
        this.basePane = new AnchorPane();
        this.components = new ArrayList<>();
    }

    private void setUp() {
        basePane.getChildren().clear();
        GridPane baseGridPane = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(100);
        baseGridPane.getColumnConstraints().add(column1);
        basePane.getChildren().add(baseGridPane);
        AnchorPane.setRightAnchor(baseGridPane, 0.0);
        AnchorPane.setTopAnchor(baseGridPane, 0.0);
        AnchorPane.setLeftAnchor(baseGridPane, 0.0);
        AnchorPane.setBottomAnchor(baseGridPane, 0.0);
        baseGridPane.getRowConstraints().add(new RowConstraints(rowHeight, rowHeight, rowHeight));
        Label label = new Label(name);
        baseGridPane.add(label, 0, 0);
        GridPane.setHalignment(label, HPos.CENTER);
        label.setAlignment(Pos.CENTER);
        label.setFont(new Font(15.0));

        this.settingsPannel = new MultiColumnSettingsPannel(basePane, components);

        baseGridPane.add(this.settingsPannel.getScrollPane(), 0, 1);
    }

    public void addGuiComponents() {
        setUp();
    }

    public void addCheckBox(PersonalityVariable variable) {
        components.add(new CheckBoxComponent(variable, variable.getCustomName()));
    }

    public void addTextBox(PersonalityVariable variable) {
        components.add(new TextBoxComponent(variable, variable.getCustomName()));
    }

    public void addOptionsList(PersonalityVariable variable, ArrayList<String> options) {
        components.add(new OptionsListComponent(variable, variable.getCustomName(), options));
    }

    public void addIntegerSpinner(PersonalityVariable variable, int min, int max) {
        components.add(new IntegerSpinnerComponent(variable, variable.getCustomName(), min, max));
    }

    public void addDoubleSpinner(PersonalityVariable variable, double min, double max) {
        components.add(new DoubleSpinnerComponent(variable, variable.getCustomName(), min, max));
    }

    public AnchorPane getAnchorPane() {
        return basePane;
    }

    public String getName() {
        return name;
    }

    public MultiColumnSettingsPannel getSettingsPanel() {
        return settingsPannel;
    }
}
