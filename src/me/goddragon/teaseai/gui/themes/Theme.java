package me.goddragon.teaseai.gui.themes;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.config.ConfigHandler;
import me.goddragon.teaseai.gui.main.MainGuiController;
import me.goddragon.teaseai.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Theme {
    public String name;
    private ConfigHandler configHandler;

    private File cssFile;
    public final ArrayList<ThemeSetting> settings = new ArrayList<>();


    public Theme(String name) {
        this.name = name;
        this.configHandler = new ConfigHandler(getConfigFilePath());

        loadSettings();

        this.configHandler.loadConfig();

        //Load config values AFTER we initially loaded the config
        for (ThemeSetting setting : settings) {
            setting.fetchFromConfig();
        }

        this.cssFile = new File(getCSSFilePath());
    }

    public void saveToConfig() {
        this.configHandler.saveConfig();
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public String getStylesheetURI() {
        return new File(getCSSFilePath()).toURI().toString();
    }

    public String getCSSFilePath() {
        return ThemeHandler.THEME_FOLDER_NAME + File.separator + name + ".css";
    }

    public String getConfigFilePath() {
        return ThemeHandler.THEME_FOLDER_NAME + File.separator + name + ".tajth";
    }

    public void selectTheme() {
        boolean cssFileExist = cssFile.exists();

        for (Scene scene : MainGuiController.getController().getMainScenes()) {
            //handleNodeStyle(scene.getRoot(), cssFileExist);

            scene.getStylesheets().clear();

            if (cssFileExist) {
                scene.getStylesheets().add(getStylesheetURI());
            }
        }

        /*for (ThemeSetting setting : this.settings) {
            setting.applyToGui();
        }*/
    }

    private void handleNodeStyle(Node node, boolean css) {
        if(node instanceof Pane) {
            for(Node subNode : ((Pane) node).getChildren()) {
                handleNodeStyle(subNode, css);
            }

            ((Pane)node).getStylesheets().clear();
            if(css) {
                ((Pane)node).getStylesheets().add(getStylesheetURI());
            }
        }
    }

    public void setName(String name) {
        this.name = name;
        this.configHandler.changeConfigName(getConfigFilePath());
    }

    public boolean delete() {
        try {
            FileUtils.delete(this.configHandler.getConfigFile());
            ThemeHandler.getHandler().getThemes().remove(this);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void setChatWindowColor(Color color) {
        ((ThemeColor) this.settings.get(1)).setColor(color);
    }

    private void loadSettings() {
        MainGuiController mainGuiController = MainGuiController.getController();

        settings.add(new ThemeColor("Primary Color", this) {
            @Override
            public void applyToGui() {
                /*mainGuiController.baseAnchorPane.setBackground(new Background(new BackgroundFill(this.color, CornerRadii.EMPTY, Insets.EMPTY)));
                mainGuiController.leftWidgetBar.setBackground(new Background(new BackgroundFill(this.color, CornerRadii.EMPTY, Insets.EMPTY)));
                mainGuiController.rightWidgetBar.setBackground(new Background(new BackgroundFill(this.color, CornerRadii.EMPTY, Insets.EMPTY)));

                SettingsController settingsController = SettingsController.getController();
                if (settingsController != null) {
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
                        if (panel.getSettingsPanel() != null) {
                            panel.getSettingsPanel().getScrollPane().setStyle("-fx-background: " + getCSSColorString());
                        }
                    }
                }*/
            }
        });

        settings.add(new ThemeColor("Chat Window Color", this) {
            @Override
            public void applyToGui() {
                mainGuiController.chatPane.setStyle("-fx-background-color: " + getCSSColorString() + ";-fx-border-color: " + getCSSColorString() + "; -fx-background-radius:10 10 10 10; -fx-border-radius:10 10 10 10");
            }
        });

        settings.add(new ThemeColor("Chat Background Color", this) {
            @Override
            public void applyToGui() {
                mainGuiController.chatBackground.setBackground(new Background(new BackgroundFill(this.color, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        });

        settings.add(new ThemeColor("Date Color", this) {
            @Override
            public void applyToGui() {
                ChatHandler.getHandler().setDateColor(this.color);
            }
        });

        settings.add(new ThemeColor("Chat Color", this) {
            @Override
            public void applyToGui() {
                ChatHandler.getHandler().setDefaultChatColor(this.color);
            }
        });

        settings.add(new ThemeColor("Sub Name Color", this) {
            @Override
            public void applyToGui() {
                ChatHandler.getHandler().getParticipantById(0).setNameColor(this.color);
            }
        });

        settings.add(new ThemeColor("Dom Name Color", this) {
            @Override
            public void applyToGui() {
                ChatHandler.getHandler().getParticipantById(1).setNameColor(this.color);
            }
        });

        settings.add(new ThemeColor("Friend 1 Name Color", this) {
            @Override
            public void applyToGui() {
                ChatHandler.getHandler().getParticipantById(2).setNameColor(this.color);
            }
        });

        settings.add(new ThemeColor("Friend 2 Name Color", this) {
            @Override
            public void applyToGui() {
                ChatHandler.getHandler().getParticipantById(3).setNameColor(this.color);
            }
        });

        settings.add(new ThemeColor("Friend 3 Name Color", this) {
            @Override
            public void applyToGui() {
                ChatHandler.getHandler().getParticipantById(4).setNameColor(this.color);
            }
        });
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public ArrayList<ThemeSetting> getSettings() {
        return settings;
    }

}
