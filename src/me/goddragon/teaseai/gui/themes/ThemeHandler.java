package me.goddragon.teaseai.gui.themes;

import javafx.scene.paint.Color;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.config.ConfigValue;
import me.goddragon.teaseai.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class ThemeHandler {
    public static final String THEME_FOLDER_NAME = "Themes";

    public static final Theme TWILIGHT_THEME = new Theme("Twilight");

    static {
        //Setup default twilight values
        ((ThemeColor)TWILIGHT_THEME.settings.get(0)).setColor(Color.LIGHTGREY);
        ((ThemeColor)TWILIGHT_THEME.settings.get(1)).setColor(Color.LIGHTGREY);
        ((ThemeColor)TWILIGHT_THEME.settings.get(2)).setColor(Color.LIGHTGREY);
        ((ThemeColor)TWILIGHT_THEME.settings.get(3)).setColor(Color.valueOf("#666666"));
        ((ThemeColor)TWILIGHT_THEME.settings.get(4)).setColor(Color.BLACK);
        ((ThemeColor)TWILIGHT_THEME.settings.get(5)).setColor(Color.DARKCYAN);
        ((ThemeColor)TWILIGHT_THEME.settings.get(6)).setColor(Color.RED);
        ((ThemeColor)TWILIGHT_THEME.settings.get(7)).setColor(Color.ORANGE);
        ((ThemeColor)TWILIGHT_THEME.settings.get(8)).setColor(Color.LIGHTGREEN);
        ((ThemeColor)TWILIGHT_THEME.settings.get(9)).setColor(Color.MEDIUMVIOLETRED);
        ((ThemeColor)TWILIGHT_THEME.settings.get(10)).setColor(Color.valueOf("#f2f2f2"));

        TWILIGHT_THEME.saveToConfig();
    }

    private static ThemeHandler themeHandler = new ThemeHandler();

    private final ArrayList<Theme> themes = new ArrayList<>();


    private ConfigValue selectedTheme;

    public ThemeHandler() {
        //Create theme folder
        File themesFolder = new File(THEME_FOLDER_NAME);
        themesFolder.mkdirs();

        loadThemes();

        if(getTheme("Twilight") == null) {
            this.themes.add(TWILIGHT_THEME);
        }

        this.selectedTheme = new ConfigValue("selectedTheme", "Twilight", TeaseAI.getApplication().getConfigHandler());
    }


    public ThemeNameChangeResult checkThemeName(String name) {
        if(name == null) {
            return ThemeNameChangeResult.NULL;
        }

        if(getTheme(name) != null) {
            return ThemeNameChangeResult.ALREADY_EXISTS;
        }

        return ThemeNameChangeResult.OKAY;
    }

    public void loadThemes() {
        this.themes.clear();

        for(File file : getThemesFolder().listFiles()) {
            if(file.isFile() && FileUtils.getExtension(file).equalsIgnoreCase("tajth")) {
               addTheme(new Theme(FileUtils.stripExtension(file.getName())));
            }
        }
    }

    public void addTheme(Theme theme) {
        this.themes.add(theme);
    }

    public Theme getTheme(String name) {
        for(Theme theme : themes) {
            if(theme.getName().equalsIgnoreCase(name)) {
                return theme;
            }
        }

        //TeaseLogger.getLogger().log(Level.SEVERE, "Theme '" + name + "' does not exist!");
        return null;
    }

    public void setSelectedTheme(Theme theme) {
        this.selectedTheme.setValue(theme.getName()).save();
    }

    public Theme getSelectedTheme() {
        return getTheme(this.selectedTheme.getValue());
    }

    public ArrayList<Theme> getThemes() {
        return themes;
    }

    public static ThemeHandler getHandler() {
        return themeHandler;
    }

    public static void setHandler(ThemeHandler themeHandler) {
        ThemeHandler.themeHandler = themeHandler;
    }


    public static File getThemesFolder() {
        File themesFolder = new File(THEME_FOLDER_NAME);
        return themesFolder;
    }

    public enum ThemeNameChangeResult {
        ALREADY_EXISTS, INVALID_CHARACTERS, OKAY, NULL
    }
}
