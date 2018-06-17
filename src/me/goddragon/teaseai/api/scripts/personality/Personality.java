package me.goddragon.teaseai.api.scripts.personality;

import me.goddragon.teaseai.api.config.ConfigHandler;
import me.goddragon.teaseai.api.config.ConfigValue;
import me.goddragon.teaseai.api.config.VariableHandler;
import me.goddragon.teaseai.api.picture.PictureSelector;
import me.goddragon.teaseai.api.scripts.ScriptHandler;
import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.TeaseLogger;
import me.goddragon.teaseai.utils.ZipUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class Personality {
    public static final String PROPERTIES_NAME = "personality.properties";

    public final ConfigValue name;
    public final ConfigValue version;
    public final ConfigValue githubDownloadLink;
    public final ConfigValue githubDownloadPersonalityPath;
    public final ConfigValue personalityPropertiesLink;

    private final String folderName;
    private final ConfigHandler configHandler;
    private final VariableHandler variableHandler;

    private PictureSelector pictureSelector = new PictureSelector();

    public Personality(String folderName) {
        this.folderName = folderName;
        this.configHandler = new ConfigHandler(getFolder().getAbsolutePath() + File.separator + PROPERTIES_NAME);
        this.variableHandler = new VariableHandler(this);

        this.name = new ConfigValue("name", "Default Personality", configHandler);
        this.version = new ConfigValue("version", "1.0", configHandler);
        this.githubDownloadLink = new ConfigValue("githubDownloadLink", "null", configHandler);
        this.githubDownloadPersonalityPath = new ConfigValue("githubDownloadPersonalityPath", "null", configHandler);
        this.personalityPropertiesLink = new ConfigValue("personalityPropertiesLink", "null", configHandler);

        //Load in all config and variable values (we need to do this before the update because the update check needs the version config value)
        variableHandler.loadVariables();
        configHandler.loadConfig();

        checkForUpdate();
    }

    public void checkForUpdate() {
        //No link given
        if(personalityPropertiesLink == null || personalityPropertiesLink.getValue() == null || personalityPropertiesLink.getValue().equals("null")) {
            return;
        }

        try {
            URL url = new URL(personalityPropertiesLink.getValue());
            ConfigHandler urlConfig = new ConfigHandler(url);
            ConfigValue version = new ConfigValue("version", "null", urlConfig);
            urlConfig.loadConfig();

            if(version.getValue() == null || version.getValue().equals("null")) {
                TeaseLogger.getLogger().log(Level.SEVERE, "Fetched invalid properties file from url '" + personalityPropertiesLink.getValue() + "' for personality '" + name + "'. Version number is missing.");
                return;
            }

            if(version.getDouble() > this.version.getDouble()) {
                TeaseLogger.getLogger().log(Level.INFO, "Detected new version '" + version.getValue() + "' for personality '" + name + "'. Fetching update from remote...");
                fetchFromGithub(version.getDouble());
            }
        } catch (MalformedURLException e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid properties file url '" + personalityPropertiesLink.getValue() + "' for personality '" + name + "'.");
        }
    }

    public void fetchFromGithub(double newVersion) {
        try {
            String fileName = PersonalityManager.PERSONALITY_FOLDER_NAME + File.separator + name + " (" + newVersion + ")";
            URL url = new URL(githubDownloadLink.getValue());
            URLConnection conn = url.openConnection();
            InputStream in = conn.getInputStream();
            File newUpdateZipFile = new File(fileName + ".zip");

            FileOutputStream out = new FileOutputStream(newUpdateZipFile);

            byte[] b = new byte[1024];
            int count;
            while ((count = in.read(b)) >= 0) {
                out.write(b, 0, count);
            }

            out.flush();
            out.close();
            in.close();

            TeaseLogger.getLogger().log(Level.INFO, "Finished downloading update for personality '" + name + "'. Unzipping...");
            File target = new File(fileName);

            //Unzip the downloaded file
            ZipUtils.unzipFile(newUpdateZipFile, target);

            //Delete the downloaded zip file
            newUpdateZipFile.delete();

            //Copy variables and other stuff
            File newPersonalityFolder = new File(target.getAbsolutePath() + File.separator + githubDownloadPersonalityPath.getValue().replace("/", File.separator).replace("\\", File.separator));

            if(!newPersonalityFolder.exists()) {
                TeaseLogger.getLogger().log(Level.INFO, "Invalid github download personality path '" + githubDownloadPersonalityPath.getValue() + "' for personality '" + name + "'.");
                target.delete();
                return;
            }

            TeaseLogger.getLogger().log(Level.INFO, "Finished unzipping update for personality '" + name + "'. Copying old system folder...");

            //Copy system folder with variables etc.
            FileUtils.copyFolder(getSystemFolder(), new File(newPersonalityFolder.getAbsolutePath() + File.separator + "System"), true);

            TeaseLogger.getLogger().log(Level.INFO, "Finished copying system folder for personality '" + name + "'. Switching old personality with new one....");

            //Zip old personality folder
            ZipUtils.zipFolder(getFolder(), new File(PersonalityManager.PERSONALITY_FOLDER_NAME + File.separator + name + " (" + version.getDouble() + ").zip"));

            //Delete old personality
            FileUtils.deleteFileOrFolder(getFolder().toPath());

            //Replace old folder with new one (we need to replace the separators in the path for alternative system support)
            FileUtils.copyFolder(newPersonalityFolder, new File(getFolder().getAbsolutePath()), true);

            //Delete downloaded fetched update
            FileUtils.deleteFileOrFolder(target.toPath());

            //Load in all config and variable values yet again
            this.variableHandler.loadVariables();
            this.configHandler.loadConfig();

            TeaseLogger.getLogger().log(Level.INFO, "Finished updating personality '" + name.getValue() + "' to version " + version.getDouble());
        } catch (MalformedURLException e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid github download url '" + githubDownloadLink.getValue() + "' for personality '" + name + "'. Update failed.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        File loadFile = new File(getFolder().getAbsolutePath() + File.separator + "load.js");

        if(loadFile.exists()) {
            try {
                ScriptHandler.getHandler().runScript(loadFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void unload() {
        File loadFile = new File(getFolder().getAbsolutePath() + File.separator + "unload.js");

        if(loadFile.exists()) {
            try {
                ScriptHandler.getHandler().runScript(loadFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void reload() {
        unload();
        load();
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

    public File getSystemFolder() {
        return new File(PersonalityManager.PERSONALITY_FOLDER_NAME + File.separator + folderName + File.separator + "System");
    }

    public File getFolder() {
        return new File(PersonalityManager.PERSONALITY_FOLDER_NAME + File.separator + folderName);
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

    public ConfigValue getGithubDownloadLink() {
        return githubDownloadLink;
    }

    public ConfigValue getPersonalityPropertiesLink() {
        return personalityPropertiesLink;
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
