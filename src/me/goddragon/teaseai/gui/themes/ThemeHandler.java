package me.goddragon.teaseai.gui.themes;

import javafx.scene.paint.Color;
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
        ((ThemeColor)TWILIGHT_THEME.settings.get(3)).setColor(Color.BLACK);
        ((ThemeColor)TWILIGHT_THEME.settings.get(4)).setColor(Color.GRAY);
        ((ThemeColor)TWILIGHT_THEME.settings.get(5)).setColor(Color.DARKCYAN);
        ((ThemeColor)TWILIGHT_THEME.settings.get(6)).setColor(Color.RED);
        ((ThemeColor)TWILIGHT_THEME.settings.get(7)).setColor(Color.ORANGE);
        ((ThemeColor)TWILIGHT_THEME.settings.get(8)).setColor(Color.LIGHTGREEN);
        ((ThemeColor)TWILIGHT_THEME.settings.get(9)).setColor(Color.MEDIUMVIOLETRED);

        TWILIGHT_THEME.saveToConfig();
    }

    private static ThemeHandler themeHandler = new ThemeHandler();

    private final ArrayList<Theme> themes = new ArrayList<>();


    public ThemeHandler() {
        //Create theme folder
        File themesFolder = new File(THEME_FOLDER_NAME);
        themesFolder.mkdirs();

        loadThemes();
        this.themes.add(TWILIGHT_THEME);
    }

    public void loadThemes() {
        this.themes.clear();

        for(File file : getThemesFolder().listFiles()) {
            if(file.isFile() && FileUtils.getExtension(file).equalsIgnoreCase("theme")) {
                this.themes.add(new Theme(FileUtils.stripExtension(file.getName())));
            }
        }
    }

    public Theme getActiveTheme() {
        //TODO: Default!!! Change
        return themes.get(0);
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
}
