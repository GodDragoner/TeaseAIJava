package me.goddragon.teaseai.gui.themes;

import javafx.scene.paint.Color;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.logging.Level;

public abstract class CSSThemeColor extends ThemeColor {
    protected String cssKey;
    private String cssSecondaryKey;

    public CSSThemeColor(String name, Theme theme, String cssKey, String cssSecondaryKey) {
        this(name, Color.WHITE, theme, cssKey, cssSecondaryKey);
    }

    public CSSThemeColor(String name, Color color, Theme theme, String cssKey, String cssSecondaryKey) {
        super(name, color, theme);

        this.cssKey = cssKey;
        this.cssSecondaryKey = cssSecondaryKey;

        this.colorPicker.setOnAction(e -> {
            updateColor();
            applyToGui();

            theme.applyCSSFile();
        });
    }


    @Override
    public void fetchFromConfig() {
        String value = this.theme.readFromCSSFile(this.cssKey, this.cssSecondaryKey);

        if (value != null) {
            this.setColor(Color.valueOf(value));
        } else {
            TeaseLogger.getLogger().log(Level.SEVERE, "CSS Theme color " + this.name + " was unable to read from css file.");
        }
    }
}
