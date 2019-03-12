package me.goddragon.teaseai.gui.settings;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import me.goddragon.teaseai.api.config.GUISettingComponent;

import java.util.ArrayList;

public class MultiColumnSettingsPannel {
    private final AnchorPane basePane;
    private ArrayList<GUISettingComponent> components;

    private final GridPane gridPane;
    private final ScrollPane scrollPane;

    private int rowHeight = 30;
    private int gridPaneRows = 0;

    public MultiColumnSettingsPannel(AnchorPane basePane, ArrayList<GUISettingComponent> components) {
        this.basePane = basePane;
        this.components = components;

        gridPane = new GridPane();
        scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);

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
        addSettings();
    }

    public void updateSettings() {
        clearSettings();
        addSettings();
    }

    private void clearSettings() {
        gridPane.getChildren().clear();
    }

    private void addSettings() {
        ArrayList<GUISettingComponent> firstColumn = new ArrayList<>();
        ArrayList<GUISettingComponent> secondColumn = new ArrayList<>();
        ArrayList<GUISettingComponent> flexible = new ArrayList<>();

        for (int i = 0; i < components.size(); i++) {
            int id = components.get(i).getColumnID();
            if (id == 1) {
                firstColumn.add(components.get(i));
            } else if (id == 2) {
                secondColumn.add(components.get(i));
            } else {
                flexible.add(components.get(i));
            }

        }

        int halfCapacity = components.size() / 2 + ((components.size() % 2 == 0) ? 0 : 1);
        if (firstColumn.size() >= halfCapacity) {
            secondColumn.addAll(flexible);
        } else if (secondColumn.size() > halfCapacity) {
            firstColumn.addAll(flexible);
        } else {
            for (int i = 0; i < flexible.size(); i++) {
                if (firstColumn.size() < halfCapacity) {
                    firstColumn.add(flexible.get(i));
                } else {
                    secondColumn.add(flexible.get(i));
                }
            }
        }

        addCompontentsToColumn(firstColumn, 1);
        addCompontentsToColumn(secondColumn, 4);
    }

    public void addCompontentsToColumn(ArrayList<GUISettingComponent> settingComponents, int columnId) {
        for (int i = 0; i < settingComponents.size(); i++) {
            GUISettingComponent component = settingComponents.get(i);

            if (i >= gridPaneRows) {
                gridPane.getRowConstraints().add(new RowConstraints(rowHeight, rowHeight, rowHeight));
                gridPaneRows++;
            }

            Label label = component.getLabel();
            label.setAlignment(Pos.CENTER_LEFT);

            Node componentNode = component.getSetting();
            componentNode.getStyleClass().add("button-color");
            
            gridPane.add(label, columnId, i);
            gridPane.add(componentNode, columnId + 1, i);
            GridPane.setHalignment(label, HPos.LEFT);
        }
    }

    public AnchorPane getBasePane() {
        return basePane;
    }

    public ArrayList<GUISettingComponent> getComponents() {
        return components;
    }

    public void setComponents(ArrayList<GUISettingComponent> components) {
        this.components = components;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
    }

    public int getGridPaneRows() {
        return gridPaneRows;
    }

    public void setGridPaneRows(int gridPaneRows) {
        this.gridPaneRows = gridPaneRows;
    }
}
