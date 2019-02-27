package me.goddragon.teaseai.gui.themes;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.config.ConfigHandler;
import me.goddragon.teaseai.api.config.PersonalitiesSettingsHandler;
import me.goddragon.teaseai.api.config.PersonalitySettingsHandler;
import me.goddragon.teaseai.api.config.PersonalitySettingsPanel;
import me.goddragon.teaseai.gui.main.MainGuiController;
import me.goddragon.teaseai.gui.settings.SettingsController;

import java.io.File;
import java.util.ArrayList;

public class Theme {


    public String name;
    private ConfigHandler configHandler;

    public final ArrayList<ThemeSetting> settings = new ArrayList<>();


    public Theme(String name) {
        this.name = name;
        this.configHandler = new ConfigHandler(ThemeHandler.THEME_FOLDER_NAME + File.separator + name + ".theme");

        loadSettings();

        this.configHandler.loadConfig();

        //Load config values AFTER we initially loaded the config
        for(ThemeSetting setting : settings) {
            setting.fetchFromConfig();
        }
    }

    public void saveToConfig() {
        this.configHandler.saveConfig();
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public void selectTheme() {
        //TODO: Save selected theme name to config
        for (ThemeSetting setting : this.settings) {
            setting.applyToGui();
        }
    }

    public void setChatWindowColor(Color color) {
        ((ThemeColor)this.settings.get(1)).setColor(color);
    }

    private void loadSettings() {
        MainGuiController mainGuiController = MainGuiController.getController();
        SettingsController settingsController = SettingsController.getController();

        settings.add(new ThemeColor("Primary Color", this) {
            @Override
            void applyToGui() {
                mainGuiController.baseAnchorPane.setBackground(new Background(new BackgroundFill(this.color, CornerRadii.EMPTY, Insets.EMPTY)));
                mainGuiController.leftWidgetBar.setBackground(new Background(new BackgroundFill(this.color, CornerRadii.EMPTY, Insets.EMPTY)));
                mainGuiController.rightWidgetBar.setBackground(new Background(new BackgroundFill(this.color, CornerRadii.EMPTY, Insets.EMPTY)));

                if(settingsController != null) {
                    settingsController.SettingsPanes.setStyle("-fx-background-color: " + getCSSColorString());
                    settingsController.SettingsBackground.setStyle("-fx-background-color: " + getCSSColorString());
                    settingsController.GeneralTab.setBackground(new Background(new BackgroundFill(this.color, CornerRadii.EMPTY, Insets.EMPTY)));
                    settingsController.MediaTab.setBackground(new Background(new BackgroundFill(this.color, CornerRadii.EMPTY, Insets.EMPTY)));
                    settingsController.AppearanceTab.setBackground(new Background(new BackgroundFill(this.color, CornerRadii.EMPTY, Insets.EMPTY)));
                    settingsController.PersonalityTab.setBackground(new Background(new BackgroundFill(this.color, CornerRadii.EMPTY, Insets.EMPTY)));
                    settingsController.ContactsTab.setBackground(new Background(new BackgroundFill(this.color, CornerRadii.EMPTY, Insets.EMPTY)));
                }

                for (PersonalitySettingsHandler p : PersonalitiesSettingsHandler.getHandler().getSettingsHandlers()) {
                    for (PersonalitySettingsPanel panel : p.getSettingsPanels()) {
                        panel.getScrollPane().setStyle("-fx-background: " + this.color.toString().replace("0x", "#"));
                    }
                }
            }
        });

        settings.add(new ThemeColor("Chat Window Color", this) {
            @Override
            void applyToGui() {
                mainGuiController.chatPane.setStyle("-fx-background-color: " + getCSSColorString() + ";-fx-border-color: " + getCSSColorString() + "; -fx-background-radius:10 10 10 10; -fx-border-radius:10 10 10 10");
            }
        });

        settings.add(new ThemeColor("Chat Background Color", this) {
            @Override
            void applyToGui() {
                mainGuiController.chatBackground.setBackground(new Background(new BackgroundFill(this.color, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        });

        settings.add(new ThemeColor("Date Color", this) {
            @Override
            void applyToGui() {
                ChatHandler.getHandler().setDateColor(this.color);
            }
        });

        settings.add(new ThemeColor("Chat Color", this) {
            @Override
            void applyToGui() {
                ChatHandler.getHandler().setDefaultChatColor(this.color);
            }
        });

        settings.add(new ThemeColor("Sub Name Color", this) {
            @Override
            void applyToGui() {
                ChatHandler.getHandler().getParticipantById(0).setNameColor(this.color);
            }
        });

        settings.add(new ThemeColor("Dom Name Color", this) {
            @Override
            void applyToGui() {
                ChatHandler.getHandler().getParticipantById(1).setNameColor(this.color);
            }
        });

        settings.add(new ThemeColor("Friend 1 Name Color", this) {
            @Override
            void applyToGui() {
                ChatHandler.getHandler().getParticipantById(2).setNameColor(this.color);
            }
        });

        settings.add(new ThemeColor("Friend 2 Name Color", this) {
            @Override
            void applyToGui() {
                ChatHandler.getHandler().getParticipantById(3).setNameColor(this.color);
            }
        });

        settings.add(new ThemeColor("Friend 3 Name Color", this) {
            @Override
            void applyToGui() {
                ChatHandler.getHandler().getParticipantById(4).setNameColor(this.color);
            }
        });
    }

    public ArrayList<ThemeSetting> getSettings() {
        return settings;
    }
}
