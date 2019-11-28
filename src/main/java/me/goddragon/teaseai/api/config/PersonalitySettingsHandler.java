package me.goddragon.teaseai.api.config;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.ArrayList;

public class PersonalitySettingsHandler {
    private ArrayList<PersonalitySettingsPanel> settingsPanels;

    private Tab PersonalityTab;

    private String personalityName;

    private GridPane personalityGridPane;

    private ListView<Label> menusList;

    private AnchorPane currentlyStored;

    public PersonalitySettingsHandler(String personalityName) {
        settingsPanels = new ArrayList<PersonalitySettingsPanel>();
        this.personalityName = personalityName;
        PersonalitiesSettingsHandler.getHandler().addPersonalitySettingsHandler(this);
    }

    public PersonalitySettingsPanel addPanel(String panelName) {
        return addSettingsPanel(panelName);
    }

    public PersonalitySettingsPanel addSettingsPanel(String panelName) {
        if (settingsPanels.isEmpty()) {
            PersonalityTab = PersonalitiesSettingsHandler.getHandler().addPersonalityTab(personalityName);
            setupPersonalityTab();
        }
        Label toAdd = new Label(panelName);
        toAdd.setAlignment(Pos.CENTER);
        menusList.getItems().add(toAdd);
        PersonalitySettingsPanel thisPanel = new PersonalitySettingsPanel(panelName);
        settingsPanels.add(thisPanel);
        menusList.getSelectionModel().select(toAdd);
        updateMenuContent();
        return thisPanel;
    }

    private void updateMenuContent() {
        String currentlySelected = menusList.getSelectionModel().getSelectedItem().getText();
        for (PersonalitySettingsPanel panel : settingsPanels) {
            if (panel.getName().equalsIgnoreCase(currentlySelected)) {
                if (currentlyStored != null) {
                    personalityGridPane.getChildren().remove(currentlyStored);
                }
                currentlyStored = panel.getAnchorPane();
                personalityGridPane.add(currentlyStored, 0, 1);
            }
        }
    }

    private void setupPersonalityTab() {
        personalityGridPane = new GridPane();
        PersonalityTab.setContent(personalityGridPane);
        PersonalityTab.getStyleClass().add("button-color");
        ColumnConstraints column = new ColumnConstraints();
        column.setPercentWidth(100);
        personalityGridPane.getColumnConstraints().add(column);

        /*ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(60);
        ObservableList<ColumnConstraints> constraints = personalityGridPane.getColumnConstraints();
        personalityGridPane.getColumnConstraints().add(column1);
        personalityGridPane.getColumnConstraints().add(column2);
        Label testLabel = new Label("Setting Menus");
        testLabel.setAlignment(Pos.CENTER);
        Label testLabel2 = new Label("testing2");
        testLabel2.setAlignment(Pos.CENTER);
        personalityGridPane.add(testLabel, 0, 0);
        personalityGridPane.add(testLabel2, 1, 0);*/

        personalityGridPane.getRowConstraints().add(new RowConstraints(30, 40, 40));
        menusList = new ListView<>();
        menusList.setOrientation(Orientation.HORIZONTAL);
        personalityGridPane.add(menusList, 0, 0);
        menusList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Label>() {

            @Override
            public void changed(ObservableValue<? extends Label> observable, Label oldValue, Label newValue) {
                updateMenuContent();
            }
        });
    }

    public ArrayList<PersonalitySettingsPanel> getSettingsPanels() {
        return settingsPanels;
    }

    public PersonalitySettingsPanel getPanel(String panelName) {
        PersonalitySettingsPanel toReturn = null;

        for (PersonalitySettingsPanel panel : settingsPanels) {
            if (panel.getName().equalsIgnoreCase(panelName)) {
                toReturn = panel;
                break;
            }
        }

        return toReturn;
    }
}
