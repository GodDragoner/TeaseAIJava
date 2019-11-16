package me.goddragon.teaseai.api.chat;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.config.ConfigValue;

import java.io.File;

/**
 * Created by GodDragon on 31.03.2018.
 */
public class Contact {

    private String configPrefix;
    private String defaultName;

    public final ConfigValue NAME;
    public final ConfigValue IMAGE_PATH;
    public final ConfigValue IMAGE_SET_PATH;

    public Contact(String configPrefix, String defaultName) {
        this.configPrefix = configPrefix;
        this.defaultName = defaultName;
        this.NAME = new ConfigValue(configPrefix + "Name", defaultName, TeaseAI.application.getConfigHandler());
        this.IMAGE_PATH = new ConfigValue(configPrefix + "ImagePath", "null", TeaseAI.application.getConfigHandler());
        this.IMAGE_SET_PATH = new ConfigValue(configPrefix + "ImageSetPath", "null", TeaseAI.application.getConfigHandler());
    }

    public void save() {
        //One option saves the whole config
        NAME.save();
    }

    public File getImageFolder() {
        if (IMAGE_SET_PATH.getValue().equalsIgnoreCase("null")) {
            return null;
        }

        return new File(IMAGE_SET_PATH.getValue());
    }

    public File getImage() {
        if (IMAGE_PATH.getValue().equalsIgnoreCase("null")) {
            return null;
        }

        return new File(IMAGE_PATH.getValue());
    }
}
