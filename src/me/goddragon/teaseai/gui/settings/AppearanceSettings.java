package me.goddragon.teaseai.gui.settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import me.goddragon.teaseai.api.config.GUISettingComponent;
import me.goddragon.teaseai.gui.main.MainGuiController;
import me.goddragon.teaseai.gui.themes.Theme;
import me.goddragon.teaseai.gui.themes.ThemeHandler;
import me.goddragon.teaseai.gui.themes.ThemeSetting;

import java.util.ArrayList;
import java.util.Optional;

public class AppearanceSettings {
    private final SettingsController settingsController;
    private final MainGuiController mainGuiController;

    private final int rowHeight = 30;
    private ArrayList<GUISettingComponent> components = new ArrayList<>();
    private MultiColumnSettingsPannel settingsPannel;


    public AppearanceSettings(SettingsController settingsController, MainGuiController mainGuiController) {
        this.settingsController = settingsController;
        this.mainGuiController = mainGuiController;
    }

    public static void loadSelectedTheme() {
        Theme theme = ThemeHandler.getHandler().getSelectedTheme();
        if (theme != null) {
            ThemeHandler.getHandler().getSelectedTheme().selectTheme();
        } else {
            Theme twilight = ThemeHandler.getHandler().getTheme("Twilight");
            if (twilight == null) {
                twilight = ThemeHandler.TWILIGHT_THEME;
                ThemeHandler.getHandler().addTheme(ThemeHandler.TWILIGHT_THEME);
            }

            ThemeHandler.getHandler().setSelectedTheme(twilight);
        }
    }

    public void initiate() {
        GridPane baseGridPane = SettingsController.getController().appearanceMainGridPane;

        AnchorPane.setRightAnchor(baseGridPane, 0.0);
        AnchorPane.setTopAnchor(baseGridPane, 0.0);
        AnchorPane.setLeftAnchor(baseGridPane, 0.0);
        AnchorPane.setBottomAnchor(baseGridPane, 0.0);
        baseGridPane.getRowConstraints().add(new RowConstraints(rowHeight, rowHeight, rowHeight));

        this.settingsPannel = new MultiColumnSettingsPannel(SettingsController.getController().AppearanceTab, components);
        baseGridPane.add(this.settingsPannel.getScrollPane(), 0, 1);

        Theme selectedTheme = ThemeHandler.getHandler().getSelectedTheme();

        for (Theme theme : ThemeHandler.getHandler().getThemes()) {
            settingsController.selectedThemeComboBox.getItems().add(theme);
        }

        settingsController.selectedThemeComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Theme>() {
            @Override
            public void changed(ObservableValue<? extends Theme> observable, Theme oldValue, Theme newValue) {
                newValue.selectTheme();
                ThemeHandler.getHandler().setSelectedTheme(newValue);
                updateSelectedTheme();
            }
        });

        settingsController.selectedThemeComboBox.getSelectionModel().select(selectedTheme);

        settingsController.saveThemeButton.setOnAction(e -> {
            ThemeHandler.getHandler().getSelectedTheme().saveToConfig();
        });

        settingsController.setThemeNameButton.setOnAction(e -> {
            Theme currentTheme = ThemeHandler.getHandler().getSelectedTheme();
            TextInputDialog dialog = new TextInputDialog(currentTheme.name);
            dialog.setTitle("Change Theme Name");
            //dialog.setHeaderText("Look, a Text Input Dialog");
            dialog.setContentText("Please enter a new theme name:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String name = result.get();

                ThemeHandler.ThemeNameChangeResult changeResult = ThemeHandler.getHandler().checkThemeName(name);

                if (changeResult == ThemeHandler.ThemeNameChangeResult.OKAY) {
                    currentTheme.setName(name);
                    settingsController.selectedThemeComboBox.setValue(currentTheme);
                } else if (changeResult == ThemeHandler.ThemeNameChangeResult.ALREADY_EXISTS) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Warning Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("That name is already in use!");
                    alert.showAndWait();
                }
            }
        });

        settingsController.newThemeButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog("New Theme Name");
            dialog.setTitle("New Theme Name");
            //dialog.setHeaderText("Look, a Text Input Dialog");
            dialog.setContentText("Please enter a name for your new theme:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String name = result.get();

                ThemeHandler.ThemeNameChangeResult changeResult = ThemeHandler.getHandler().checkThemeName(name);

                if (changeResult == ThemeHandler.ThemeNameChangeResult.OKAY) {
                    Theme newTheme = new Theme(name);
                    ThemeHandler.getHandler().addTheme(newTheme);
                    settingsController.selectedThemeComboBox.getItems().add(newTheme);
                    settingsController.selectedThemeComboBox.getSelectionModel().select(newTheme);
                    updateSelectedTheme();
                } else if (changeResult == ThemeHandler.ThemeNameChangeResult.ALREADY_EXISTS) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Warning Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("That name is already in use!");
                    alert.showAndWait();
                }
            }
        });

        settingsController.deleteThemeButton.setOnAction(e -> {
            Theme currentTheme = ThemeHandler.getHandler().getSelectedTheme();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Theme");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete the selected Theme?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                if (currentTheme.delete()) {
                    if (ThemeHandler.getHandler().getThemes().size() == 0) {
                        //Recover and recreate twilight theme
                        loadSelectedTheme();
                    } else {
                        Theme newTheme = ThemeHandler.getHandler().getThemes().get(0);
                        settingsController.selectedThemeComboBox.getSelectionModel().select(newTheme);
                        settingsController.selectedThemeComboBox.getItems().remove(currentTheme);
                    }
                }
            }
        });

        settingsController.updateGUIButton.setOnAction(e -> {
            ThemeHandler.getHandler().getSelectedTheme().selectTheme();
        });
    }

    public void updateSelectedTheme() {
        this.components.clear();

        for (ThemeSetting themeSetting : ThemeHandler.getHandler().getSelectedTheme().getSettings()) {
            GUISettingComponent component = new GUISettingComponent(themeSetting.getName()) {
                @Override
                public Node getSetting() {
                    return themeSetting.getControlElement();
                }
            };

            components.add(component);
        }

        this.settingsPannel.updateSettings();
    }
}
