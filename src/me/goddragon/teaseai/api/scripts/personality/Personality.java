package me.goddragon.teaseai.api.scripts.personality;

import me.goddragon.teaseai.api.config.ConfigHandler;
import me.goddragon.teaseai.api.config.ConfigValue;
import me.goddragon.teaseai.api.config.VariableHandler;
import me.goddragon.teaseai.api.picture.PictureSelector;

import java.io.File;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class Personality {
    public static final String PROPERTIES_NAME = "personality.properties";

    public final ConfigValue name;
    public final ConfigValue version;
    //public final ConfigValue githubLink;

    private final String folderName;
    private final ConfigHandler configHandler;
    private final VariableHandler variableHandler;

    private PictureSelector pictureSelector = new PictureSelector();

    public Personality(String folderName) {
        this.folderName = folderName;
        this.configHandler = new ConfigHandler(getFolder().getAbsolutePath() + "\\" + PROPERTIES_NAME);
        this.variableHandler = new VariableHandler(this);

        name = new ConfigValue("name", "Default Personality", configHandler);
        version = new ConfigValue("version", "1.0", configHandler);
        //githubLink = new ConfigValue("githubLink", "null", configHandler);

        configHandler.loadConfig();
    }

    /*public String getGithubPath() {
        String githubLinkString = githubLink.getValue();
        String path = githubLinkString.substring(githubLinkString.indexOf("github.com/") + "github.com/".length());
        path = path.replace("blob/", "");
        return path;
    }

    public String getPropertiesGithubLink() {
        return "https://raw.githubusercontent.com/" + getGithubPath() + "/" + PROPERTIES_NAME;
    }*/

    public File getFolder() {
        return new File(PersonalityManager.PERSONALITY_FOLDER_NAME + "\\" + folderName);
    }

    public String getFolderName() {
        return folderName;
    }

    public ConfigValue getName() {
        return name;
    }

    public ConfigValue getVersion() {
        return version;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public VariableHandler getVariableHandler() {
        return variableHandler;
    }

    public PictureSelector getPictureSelector() {
        return pictureSelector;
    }

    public void setPictureSelector(PictureSelector pictureSelector) {
        this.pictureSelector = pictureSelector;
    }

    @Override
    public String toString() {
        return name + " (" + version + ")";
    }
}
