package me.goddragon.teaseai.api.picture;

import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.File;
import java.util.logging.Level;

/**
 * Created by GodDragon on 26.03.2018.
 */
public enum PictureTag {

    FACE("TagFace"),
    BOOBS("TagBoobs"),
    PUSSY("TagPussy"),
    ASS("TagAss"),
    LEGS("TagLegs"),
    FEET("TagFeet"),
    MASTURBATING("TagMasturbating"),
    SUCKING("TagSucking"),
    SMILING("TagSmiling"),
    GLARING("TagGlaring"),
    SIDE_VIEW("TagSeeThrough"),
    CLOSE_UP("TagAllFours"),
    ALL_FOURS("TagAllFours"),
    PIERCING("TagPiercing");

    private final String tagName;

    PictureTag(String tagName) {
        this.tagName = tagName;
    }

    public static PictureTag getByTag(File folder, String tagName) {
        for (PictureTag pictureTag : values()) {
            if (pictureTag.tagName.equalsIgnoreCase(tagName)) {
                return pictureTag;
            }
        }

        TeaseLogger.getLogger().log(Level.WARNING, "Invalid tag '" + tagName + "' detected in folder " + folder.getAbsolutePath());

        return null;
    }
}
