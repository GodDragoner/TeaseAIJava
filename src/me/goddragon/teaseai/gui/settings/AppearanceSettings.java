package me.goddragon.teaseai.gui.settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.config.ConfigValue;
import me.goddragon.teaseai.api.config.PersonalitiesSettingsHandler;
import me.goddragon.teaseai.api.config.PersonalitySettingsHandler;
import me.goddragon.teaseai.api.config.PersonalitySettingsPanel;
import me.goddragon.teaseai.gui.main.MainGuiController;
import me.goddragon.teaseai.gui.themes.Theme;
import me.goddragon.teaseai.gui.themes.ThemeHandler;
import me.goddragon.teaseai.gui.themes.ThemeSetting;

public class AppearanceSettings {
    private final SettingsController settingsController;
    private final MainGuiController mainGuiController;

    private ConfigValue selectedTheme;


    public AppearanceSettings(SettingsController settingsController, MainGuiController mainGuiController) {
        this.settingsController = settingsController;
        this.mainGuiController = mainGuiController;
        this.selectedTheme = new ConfigValue("selectedTheme", "Twilight", TeaseAI.getApplication().getConfigHandler());
    }

    public static void loadSelectedTheme() {
        ConfigValue selectedTheme = new ConfigValue("selectedTheme", "Twilight", TeaseAI.getApplication().getConfigHandler());
        for (Theme theme : ThemeHandler.getHandler().getThemes()) {
            if (theme.name.equals(selectedTheme.getValue())) {
                //selectTheme2(theme);
                theme.selectTheme();
            }
        }
    }

    public void initiate() {
        /*Theme custom = ThemeHandler.getHandler().getActiveTheme();

        for (Theme theme : themes) {
            settingsController.ThemesList.getItems().add(theme.name);
            if (theme.name.equals(selectedTheme.getValue())) {
                settingsController.ThemesList.getSelectionModel().select(theme.name);
                selectTheme(theme);
            }
        }*/

        settingsController.ThemesList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                for (Theme theme : ThemeHandler.getHandler().getThemes()) {
                    if (theme.name.equalsIgnoreCase(newValue)) {
                        theme.selectTheme();
                    }
                }

                selectedTheme.setValue(newValue).save();
            }

        });

        for (ThemeSetting themeSetting : ThemeHandler.getHandler().getActiveTheme().getSettings()) {
            ((Pane) settingsController.ThemesList.getParent()).getChildren().add(themeSetting.getControlElement());
        }

/*
        settingsController.PrimaryColor.setOnAction(e -> {
            if (!settingsController.ThemesList.getSelectionModel().getSelectedItem().equals("Custom")) {
                settingsController.ThemesList.getSelectionModel().select(custom.name);
                selectTheme(custom);
            }
            custom.PrimaryColor = settingsController.PrimaryColor.getValue();
            setPrimaryColor(settingsController.PrimaryColor.getValue());
            customPrimaryColor.setValue(settingsController.PrimaryColor.getValue().toString()).save();
        });

        settingsController.ChatWindowColor.setOnAction(e -> {
            if (!settingsController.ThemesList.getSelectionModel().getSelectedItem().equals("Custom")) {
                settingsController.ThemesList.getSelectionModel().select(custom.name);
                selectTheme(custom);
            }
            custom.ChatWindow = settingsController.ChatWindowColor.getValue();
            setChatWindowColor(settingsController.ChatWindowColor.getValue());
            customChatWindowColor.setValue(settingsController.ChatWindowColor.getValue().toString()).save();
        });

        settingsController.ChatBackground.setOnAction(e -> {
            if (!settingsController.ThemesList.getSelectionModel().getSelectedItem().equals("Custom")) {
                settingsController.ThemesList.getSelectionModel().select(custom.name);
                selectTheme(custom);
            }
            custom.ChatBackground = settingsController.ChatBackground.getValue();
            setChatBackgroundColor(settingsController.ChatBackground.getValue());
            customChatBackgroundColor.setValue(settingsController.ChatBackground.getValue().toString()).save();
        });

        settingsController.DateColor.setOnAction(e -> {
            if (!settingsController.ThemesList.getSelectionModel().getSelectedItem().equals("Custom")) {
                settingsController.ThemesList.getSelectionModel().select(custom.name);
                selectTheme(custom);
            }
            custom.DateColor = settingsController.DateColor.getValue();
            setDateColor(settingsController.DateColor.getValue());
            customDateColor.setValue(settingsController.DateColor.getValue().toString()).save();
        });

        settingsController.ChatColor.setOnAction(e -> {
            if (!settingsController.ThemesList.getSelectionModel().getSelectedItem().equals("Custom")) {
                settingsController.ThemesList.getSelectionModel().select(custom.name);
                selectTheme(custom);
            }
            custom.ChatColor = settingsController.ChatColor.getValue();
            setChatColor(settingsController.ChatColor.getValue());
            customChatColor.setValue(settingsController.ChatColor.getValue().toString()).save();
        });

        settingsController.SubColor.setOnAction(e -> {
            if (!settingsController.ThemesList.getSelectionModel().getSelectedItem().equals("Custom")) {
                settingsController.ThemesList.getSelectionModel().select(custom.name);
                selectTheme(custom);
            }
            custom.SubColor = settingsController.SubColor.getValue();
            setParticipantColor(0, settingsController.SubColor.getValue());
            customSubColor.setValue(settingsController.SubColor.getValue().toString()).save();
        });

        settingsController.DomColor.setOnAction(e -> {
            if (!settingsController.ThemesList.getSelectionModel().getSelectedItem().equals("Custom")) {
                settingsController.ThemesList.getSelectionModel().select(custom.name);
                selectTheme(custom);
            }
            custom.DomColor = settingsController.DomColor.getValue();
            setParticipantColor(1, settingsController.DomColor.getValue());
            customDomColor.setValue(settingsController.DomColor.getValue().toString()).save();
        });

        settingsController.Friend1Color.setOnAction(e -> {
            if (!settingsController.ThemesList.getSelectionModel().getSelectedItem().equals("Custom")) {
                settingsController.ThemesList.getSelectionModel().select(custom.name);
                selectTheme(custom);
            }
            custom.Friend1Color = settingsController.Friend1Color.getValue();
            setParticipantColor(2, settingsController.Friend1Color.getValue());
            customFriend1Color.setValue(settingsController.Friend1Color.getValue().toString()).save();
        });

        settingsController.Friend2Color.setOnAction(e -> {
            if (!settingsController.ThemesList.getSelectionModel().getSelectedItem().equals("Custom")) {
                settingsController.ThemesList.getSelectionModel().select(custom.name);
                selectTheme(custom);
            }
            custom.Friend2Color = settingsController.Friend2Color.getValue();
            setParticipantColor(3, settingsController.Friend2Color.getValue());
            customFriend2Color.setValue(settingsController.Friend2Color.getValue().toString()).save();
        });

        settingsController.Friend3Color.setOnAction(e -> {
            if (!settingsController.ThemesList.getSelectionModel().getSelectedItem().equals("Custom")) {
                settingsController.ThemesList.getSelectionModel().select(custom.name);
                selectTheme(custom);
            }
            custom.Friend3Color = settingsController.Friend3Color.getValue();
            setParticipantColor(4, settingsController.Friend3Color.getValue());
            customFriend3Color.setValue(settingsController.Friend3Color.getValue().toString()).save();
        });*/

    }

    public void setPrimaryColor(Color newColor) {
        mainGuiController.baseAnchorPane.setBackground(new Background(new BackgroundFill(newColor, CornerRadii.EMPTY, Insets.EMPTY)));
        mainGuiController.leftWidgetBar.setBackground(new Background(new BackgroundFill(newColor, CornerRadii.EMPTY, Insets.EMPTY)));
        mainGuiController.rightWidgetBar.setBackground(new Background(new BackgroundFill(newColor, CornerRadii.EMPTY, Insets.EMPTY)));
        settingsController.SettingsPanes.setStyle("-fx-background-color: " + newColor.toString().replace("0x", "#"));
        settingsController.SettingsBackground.setStyle("-fx-background-color: " + newColor.toString().replace("0x", "#"));
        settingsController.GeneralTab.setBackground(new Background(new BackgroundFill(newColor, CornerRadii.EMPTY, Insets.EMPTY)));
        settingsController.MediaTab.setBackground(new Background(new BackgroundFill(newColor, CornerRadii.EMPTY, Insets.EMPTY)));
        settingsController.AppearanceTab.setBackground(new Background(new BackgroundFill(newColor, CornerRadii.EMPTY, Insets.EMPTY)));
        settingsController.PersonalityTab.setBackground(new Background(new BackgroundFill(newColor, CornerRadii.EMPTY, Insets.EMPTY)));
        settingsController.ContactsTab.setBackground(new Background(new BackgroundFill(newColor, CornerRadii.EMPTY, Insets.EMPTY)));
        for (PersonalitySettingsHandler p : PersonalitiesSettingsHandler.getHandler().getSettingsHandlers()) {
            for (PersonalitySettingsPanel panel : p.getSettingsPanels()) {
                panel.getScrollPane().setStyle("-fx-background: " + newColor.toString().replace("0x", "#"));
            }
        }
    }

    public static void setPrimaryColor2(Color newColor) {
        MainGuiController mainGuiController = MainGuiController.getController();
        mainGuiController.baseAnchorPane.setBackground(new Background(new BackgroundFill(newColor, CornerRadii.EMPTY, Insets.EMPTY)));
        mainGuiController.leftWidgetBar.setBackground(new Background(new BackgroundFill(newColor, CornerRadii.EMPTY, Insets.EMPTY)));
        mainGuiController.rightWidgetBar.setBackground(new Background(new BackgroundFill(newColor, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public static void setChatWindowColor(Color newColor) {
        //this needs to be like this otherwise there is a bug if you click the textflow its color will revert
        MainGuiController.getController().chatPane.setStyle("-fx-background-color: " + newColor.toString().replace("0x", "#") + ";-fx-border-color: " + newColor.toString().replace("0x", "#") + "; -fx-background-radius:10 10 10 10; -fx-border-radius:10 10 10 10");
    }

    public static void setChatBackgroundColor(Color newColor) {
        MainGuiController.getController().chatBackground.setBackground(new Background(new BackgroundFill(newColor, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    public static void setDateColor(Color newColor) {
        ChatHandler.getHandler().setDateColor(newColor);
    }

    public static void setChatColor(Color newColor) {
        ChatHandler.getHandler().setDefaultChatColor(newColor);
    }

   /* public static void setParticipantColor(int id, Color newColor) {
        ChatHandler.getHandler().getParticipantColors()[id] = newColor;
    }

    public void selectTheme(Theme theme) {
        setPrimaryColor(theme.PrimaryColor);
        setChatWindowColor(theme.ChatWindow);
        setChatBackgroundColor(theme.ChatBackground);
        setDateColor(theme.DateColor);
        setChatColor(theme.ChatColor);
        setParticipantColor(0, theme.SubColor);
        setParticipantColor(1, theme.DomColor);
        setParticipantColor(2, theme.Friend1Color);
        setParticipantColor(3, theme.Friend2Color);
        setParticipantColor(4, theme.Friend3Color);
        settingsController.PrimaryColor.setValue(theme.PrimaryColor);
        settingsController.ChatWindowColor.setValue(theme.ChatWindow);
        settingsController.ChatBackground.setValue(theme.ChatBackground);
        settingsController.DateColor.setValue(theme.DateColor);
        settingsController.ChatColor.setValue(theme.SubColor);
        settingsController.SubColor.setValue(theme.SubColor);
        settingsController.DomColor.setValue(theme.DomColor);
        settingsController.Friend1Color.setValue(theme.Friend1Color);
        settingsController.Friend2Color.setValue(theme.Friend2Color);
        settingsController.Friend3Color.setValue(theme.Friend3Color);
    }

    public static void selectTheme2(Theme theme) {
        setPrimaryColor2(theme.PrimaryColor);
        setChatWindowColor(theme.ChatWindow);
        setChatBackgroundColor(theme.ChatBackground);
        setDateColor(theme.DateColor);
        setChatColor(theme.ChatColor);
        setParticipantColor(0, theme.SubColor);
        setParticipantColor(1, theme.DomColor);
        setParticipantColor(2, theme.Friend1Color);
        setParticipantColor(3, theme.Friend2Color);
        setParticipantColor(4, theme.Friend3Color);
    }*/


}
