package me.goddragon.teaseai.api.config;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class PersonalitySettingsPanel {
    private String name;
    private AnchorPane basePane;
    private final int rowHeight = 30;
    private ArrayList<GUIComponent> components;
    private GridPane gridPane;
    private int gridPaneRows = 0;
    private ScrollPane scrollPane;

    public PersonalitySettingsPanel(String panelName) {
        this.name = panelName;
        this.basePane = new AnchorPane();
        this.components = new ArrayList<>();

        setUp();
    }

    public void setUp() {
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

        gridPane = new GridPane();
        scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);

        baseGridPane.add(scrollPane, 0, 1);

        ColumnConstraints column2 = new ColumnConstraints();
        ColumnConstraints column3 = new ColumnConstraints();
        ColumnConstraints column4 = new ColumnConstraints();
        ColumnConstraints column5 = new ColumnConstraints();
        ColumnConstraints column6 = new ColumnConstraints();
        ColumnConstraints column7 = new ColumnConstraints();
        ColumnConstraints column8 = new ColumnConstraints();
        column2.setPercentWidth(2.5);
        column3.setPercentWidth(22.5);
        column4.setPercentWidth(22.5);
        column5.setPercentWidth(5);
        column6.setPercentWidth(22.5);
        column7.setPercentWidth(22.5);
        column8.setPercentWidth(2.5);
        gridPane.getColumnConstraints().addAll(column2, column3, column4, column5, column6, column7, column8);
    }

    public void addGuiComponents() {
        setUp();
        ArrayList<GUIComponent> firstColumn = new ArrayList<GUIComponent>();
        ArrayList<GUIComponent> seoondColumn = new ArrayList<GUIComponent>();
        ArrayList<GUIComponent> flexible = new ArrayList<GUIComponent>();
        for (int i = 0; i < components.size(); i++) {
            int id = components.get(i).getColumnID();
            if (id == 1) {
                firstColumn.add(components.get(i));
            } else if (id == 2) {
                seoondColumn.add(components.get(i));
            } else {
                flexible.add(components.get(i));
            }

        }

        int halfCapacity = components.size() / 2 + ((components.size() % 2 == 0) ? 0 : 1);
        if (firstColumn.size() >= halfCapacity) {
            seoondColumn.addAll(flexible);
        } else if (seoondColumn.size() > halfCapacity) {
            firstColumn.addAll(flexible);
        } else {
            for (int i = 0; i < flexible.size(); i++) {
                if (firstColumn.size() < halfCapacity) {
                    firstColumn.add(flexible.get(i));
                } else {
                    seoondColumn.add(flexible.get(i));
                }
            }
        }

        for (int i = 0; i < firstColumn.size(); i++) {
            //HBox testHbox = new HBox(30);
            gridPane.getRowConstraints()
                    .add(new RowConstraints(rowHeight, rowHeight, rowHeight));
            gridPaneRows++;

            Label label = firstColumn.get(i).getLabel();
            //label.setText("    " + label.getText());
            label.setAlignment(Pos.CENTER_LEFT);

            Node componentNode = firstColumn.get(i).getSetting();
            gridPane.add(label, 1, i);
            //testHbox.getChildren().addAll(label, componentNode);
            gridPane.add(componentNode, 2, i);
            GridPane.setHalignment(label, HPos.LEFT);
        }

        for (int i = 0; i < seoondColumn.size(); i++) {
            //HBox testHbox = new HBox(30);
            if (i >= gridPaneRows) {
                gridPane.getRowConstraints()
                        .add(new RowConstraints(rowHeight, rowHeight, rowHeight));
                gridPaneRows++;
            }

            Label label = seoondColumn.get(i).getLabel();
            //label.setText("    " + label.getText());
            label.setAlignment(Pos.CENTER_LEFT);

            Node componentNode = seoondColumn.get(i).getSetting();
            gridPane.add(label, 4, i);
            //testHbox.getChildren().addAll(label, componentNode);
            gridPane.add(componentNode, 5, i);
            GridPane.setHalignment(label, HPos.LEFT);
        }
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

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public String getName() {
        return name;
    }
}
