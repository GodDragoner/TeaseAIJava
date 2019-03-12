package me.goddragon.teaseai.gui.themes;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.config.ConfigHandler;
import me.goddragon.teaseai.gui.main.MainGuiController;
import me.goddragon.teaseai.utils.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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


        this.cssFile = new File(getCSSFilePath());

        //Load config values AFTER we initially loaded the config
        for (ThemeSetting setting : settings) {
            setting.fetchFromConfig();
        }
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
        for (ThemeSetting setting : this.settings) {
            setting.applyToGui();
        }

        applyCSSFile();
    }

    public void applyCSSFile() {
        boolean cssFileExist = cssFile.exists();

        for (Scene scene : MainGuiController.getController().getMainScenes()) {
            scene.getStylesheets().clear();

            if (cssFileExist) {
                scene.getStylesheets().add(getStylesheetURI());
            }
        }

        TeaseAI.application.getController().getLazySubController().getFlowPane().getStylesheets().clear();
        TeaseAI.application.getController().getLazySubController().getFlowPane().getStylesheets().add(getStylesheetURI());
    }

    public String readFromCSSFile(String key, String value) {
        if (!cssFile.exists()) {
            try {
                cssFile.createNewFile();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileInputStream fstream = new FileInputStream(cssFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;

            int lineIndexFound = -1;
            int endBlock = -1;
            List<String> lines = new ArrayList<>();

            while ((strLine = br.readLine()) != null) {
                if (strLine.contains(key)) {
                    lineIndexFound = lines.size();
                }

                //We found an end to our block and can skip reading th rest
                if (strLine.equalsIgnoreCase("}") && lineIndexFound > -1 && endBlock == -1) {
                   break;
                }

                //Only if we are within the boundaries of our block
                if(lineIndexFound > -1) {
                    lines.add(strLine);
                }
            }

            //Close the input stream
            br.close();

            for(String line : lines) {
                if(line.contains(value)) {
                    return line.split(":")[1].replace(";", "").trim();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void writeToCSSFile(String key, String... values) {
        if (!cssFile.exists()) {
            try {
                cssFile.createNewFile();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileInputStream fstream = new FileInputStream(cssFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;

            int lineIndexFound = -1;
            int endBlock = -1;
            List<String> lines = new ArrayList<>();

            while ((strLine = br.readLine()) != null) {
                if (strLine.contains(key)) {
                    lineIndexFound = lines.size();
                }

                if (strLine.equalsIgnoreCase("}") && lineIndexFound > -1 && endBlock == -1) {
                    endBlock = lines.size();
                }

                lines.add(strLine);
            }

            //Close the input stream
            br.close();

            boolean change = false;

            if (lineIndexFound > -1 && endBlock > -1) {
                for (String value : values) {
                    String keyValue = value.split(":")[0];

                    for (int index = lineIndexFound; index <= endBlock; index++) {
                        String line = lines.get(index);

                        if (line.contains(keyValue) && !line.contains(value)) {
                            change = true;
                            lines.set(index, "    " + value);
                        }
                    }
                }
            } else {
                change = true;

                lines.add(key + " {");

                for (String value : values) {
                    lines.add("    " + value);
                }

                lines.add("}");
            }

            if (!change) {
                return;
            }

            StringBuffer inputBuffer = new StringBuffer();

            for (String line : lines) {
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }

            FileOutputStream fileOut = new FileOutputStream(cssFile);
            fileOut.write(inputBuffer.toString().getBytes());
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleNodeStyle(Node node, boolean css) {
        if (node instanceof Pane) {
            for (Node subNode : ((Pane) node).getChildren()) {
                handleNodeStyle(subNode, css);
            }

            ((Pane) node).getStylesheets().clear();
            if (css) {
                ((Pane) node).getStylesheets().add(getStylesheetURI());
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

        settings.add(new CSSThemeColor("Primary Color", this, ".primary-color", "fx-background-color") {
            @Override
            public void applyToGui() {
                writeToCSSFile(this.cssKey, "-fx-base: " + getCSSColorString() + ";", "-fx-background-color: " + getCSSColorString() + ";");

                for(Node node : TeaseAI.getApplication().getController().getLazySubController().getFlowPane().getChildren()) {
                    if(node instanceof Button) {
                        /*String newStyle = TeaseAI.getApplication().getController().getStartChatButton().getStyle() +  " -fx-background-color: " + getCSSColorString() + ";";
                        node.setStyle(newStyle);*/
                    }
                }
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

        settings.add(new CSSThemeColor("Chat Window Color", this, ".main-chat-background", "fx-background-color") {
            @Override
            public void applyToGui() {
                writeToCSSFile(this.cssKey, "-fx-base: " + getCSSColorString() + ";", "-fx-background-color: " + getCSSColorString() + ";");
            }
        });

        settings.add(new CSSThemeColor("Chat Background Color", this, "#main-chat-window", "fx-background-color") {
            @Override
            public void applyToGui() {
                writeToCSSFile(this.cssKey, "-fx-base: " + getCSSColorString() + ";", "-fx-background-color: " + getCSSColorString() + ";");
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
        
        settings.add(new CSSThemeColor("Button Color", this, ".button-color", "fx-background-color") {
            @Override
            public void applyToGui() {
                String colorString = getCSSColorString();
                writeToCSSFile(this.cssKey, "-fx-background-color: #D3D3D3, #D3D3D3," + colorString + "," + colorString + ";");
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
