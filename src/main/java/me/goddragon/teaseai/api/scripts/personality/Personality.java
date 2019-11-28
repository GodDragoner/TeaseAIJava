package me.goddragon.teaseai.api.scripts.personality;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.config.ConfigHandler;
import me.goddragon.teaseai.api.config.ConfigValue;
import me.goddragon.teaseai.api.config.PersonalitySettingsHandler;
import me.goddragon.teaseai.api.config.VariableHandler;
import me.goddragon.teaseai.api.picture.PictureSelector;
import me.goddragon.teaseai.api.scripts.ScriptHandler;
import me.goddragon.teaseai.gui.ProgressForm;
import me.goddragon.teaseai.utils.ComparableVersion;
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
    public final ConfigValue downloadLink;
    public final ConfigValue personalityPropertiesLink;

    private final String folderName;
    private final ConfigHandler configHandler;
    private final VariableHandler variableHandler;
    private PersonalitySettingsHandler settingsHandler;

    private PictureSelector pictureSelector = new PictureSelector();

    public Personality(String folderName) {
        this.folderName = folderName;
        this.configHandler = new ConfigHandler(getFolder().getAbsolutePath() + File.separator + PROPERTIES_NAME);
        this.variableHandler = new VariableHandler(this);

        this.name = new ConfigValue("name", "Default Personality", configHandler);

        this.version = new ConfigValue("version", "1.0", configHandler);
        this.downloadLink = new ConfigValue("updateDownloadZipLink", "null", configHandler);
        this.personalityPropertiesLink = new ConfigValue("personalityPropertiesLink", "null", configHandler);

        //Load in all config and variable values (we need to do this before the update because the update check needs the version config value)
        configHandler.loadConfig();
        variableHandler.loadVariables();
        this.settingsHandler = new PersonalitySettingsHandler(this.name.getValue());
        PersonalityManager.getManager().setLoadingPersonality(this);
        onProgramStart();
    }

    public boolean checkForUpdate() {
        //No link given
        if (personalityPropertiesLink == null || personalityPropertiesLink.getValue() == null || personalityPropertiesLink.getValue().equals("null")) {
            return false;
        }

        try {
            URL url = new URL(personalityPropertiesLink.getValue());
            ConfigHandler urlConfig = new ConfigHandler(url);
            ConfigValue version = new ConfigValue("version", "null", urlConfig);
            ConfigValue downloadLink = new ConfigValue("updateDownloadZipLink", "null", urlConfig);

            urlConfig.loadConfig();

            if (version.getValue() == null || version.getValue().equals("null")) {
                TeaseLogger.getLogger().log(Level.SEVERE, "Fetched invalid properties file from url '" + personalityPropertiesLink.getValue() + "' for personality '" + name + "'. Version number is missing.");
                return false;
            }

            if (new ComparableVersion(version.getValue()).compareTo(new ComparableVersion(this.version.getValue())) > 0) {
                boolean[] update = {false};
                TeaseLogger.getLogger().log(Level.INFO, "Detected new version '" + version.getValue() + "' for personality '" + name + "'.");
                TeaseAI.application.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("New version found");
                            alert.setContentText("New version " + version.getValue() + "' for personality '" + name + "' was found. Would you like to update?");
                            ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                            ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
                            //ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                            alert.getButtonTypes().setAll(okButton, noButton);

                            alert.showAndWait().ifPresent(type -> {
                                if (type.getButtonData() == ButtonBar.ButtonData.YES) {
                                    update[0] = true;
                                }

                                synchronized (Personality.this) {
                                    Personality.this.notify();
                                }
                            });
                        } catch (Exception ex) {
                            synchronized (Personality.this) {
                                Personality.this.notify();
                            }
                        }
                    }
                });

                try {
                    synchronized (this) {
                        this.wait(1000 * 15);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (update[0]) {
                    TeaseLogger.getLogger().log(Level.INFO, "Update process accepted. Fetching update from remote...");
                    fetchFromGithub(version.getValue(), downloadLink.getValue());
                } else {
                    TeaseLogger.getLogger().log(Level.INFO, "Update process declined by user.");
                }

                return true;
            }
        } catch (MalformedURLException e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid properties file url '" + personalityPropertiesLink.getValue() + "' for personality '" + name + "'.");
        }

        return false;
    }

    public void fetchFromGithub(String newVersion, String downloadLink) {
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws InterruptedException {
                File oldPersonalityZip = null;
                File downloadedPersonalityFolder = null;

                try {
                    updateProgress(0, 10);

                    String fileName = PersonalityManager.PERSONALITY_FOLDER_NAME + File.separator + name + " (" + newVersion + ")";
                    URL url = new URL(downloadLink);
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

                    updateProgress(1, 10);
                    TeaseLogger.getLogger().log(Level.INFO, "Finished downloading update for personality '" + name + "'. Unzipping...");
                    File target = downloadedPersonalityFolder = new File(fileName);

                    //Unzip the downloaded file
                    ZipUtils.unzipFile(newUpdateZipFile, target);

                    updateProgress(2, 10);

                    //Delete the downloaded zip file
                    newUpdateZipFile.delete();

                    updateProgress(3, 10);

                    //Copy variables and other stuff
                    File newPersonalityFolder = FileUtils.findFile(target, "personality.properties").getParentFile();

                    //File newPersonalityFolder = new File(target.getAbsolutePath() + File.separator + downloadPersonalityPath.getValue().replace("/", File.separator).replace("\\", File.separator));

                    if (newPersonalityFolder == null || !newPersonalityFolder.exists()) {
                        TeaseLogger.getLogger().log(Level.INFO, "Unable to find personality.properties file in new version of '" + name + "'.");
                        target.delete();
                        return null;
                    }

                    TeaseLogger.getLogger().log(Level.INFO, "Finished unzipping update for personality '" + name + "'. Copying old system folder...");

                    //Copy system folder with variables etc.
                    FileUtils.copyFolder(getSystemFolder(), new File(newPersonalityFolder.getAbsolutePath() + File.separator + "System"), true);

                    updateProgress(4, 10);

                    TeaseLogger.getLogger().log(Level.INFO, "Finished copying system folder for personality '" + name + "'. Switching old personality with new one....");

                    //Zip old personality folder
                    ZipUtils.zipFolder(getFolder(), oldPersonalityZip = new File(PersonalityManager.PERSONALITY_FOLDER_NAME + File.separator + getFolder().getName() + " (" + version.getValue() + ").zip"));

                    updateProgress(5, 10);

                    //Delete old personality
                    FileUtils.deleteFileOrFolder(getFolder().toPath());

                    updateProgress(6, 10);

                    Thread.sleep(500);

                    //Replace old folder with new one (we need to replace the separators in the path for alternative system support)
                    FileUtils.copyFolder(newPersonalityFolder, new File(getFolder().getAbsolutePath()), true);

                    updateProgress(7, 10);

                    //Delete downloaded fetched update
                    FileUtils.deleteFileOrFolder(target.toPath());

                    updateProgress(8, 10);

                    //Load in all config and variable values yet again
                    Personality.this.variableHandler.loadVariables();
                    Personality.this.configHandler.loadConfig();

                    updateProgress(9, 10);
                    TeaseLogger.getLogger().log(Level.INFO, "Finished updating personality '" + name.getValue() + "' to version " + version.getValue());
                } catch (MalformedURLException e) {
                    TeaseLogger.getLogger().log(Level.SEVERE, "Invalid github download url '" + downloadLink + "' for personality '" + name + "'. Update failed.");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();

                    TeaseLogger.getLogger().log(Level.SEVERE, "Something went wrong while updating personality '" + name + "'.");

                    if (downloadedPersonalityFolder != null) {
                        try {
                            FileUtils.deleteFileOrFolder(downloadedPersonalityFolder.toPath());
                            Thread.sleep(500);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }

                    if (oldPersonalityZip != null) {
                        ZipUtils.unzipFile(oldPersonalityZip, getFolder());
                        TeaseLogger.getLogger().log(Level.SEVERE, "Restored previous state of personality '" + name + "'.");
                    }
                }
                return null;
            }
        };

        ProgressForm progressForm = new ProgressForm("Updating " + name + "...", task);

        TeaseAI.application.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                TeaseAI.application.startupProgressPane.addProgressBar(progressForm);
            }
        });

        Thread thread = new Thread(task);
        thread.run();

        TeaseAI.application.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                TeaseAI.application.startupProgressPane.removeProgressBar(progressForm);
            }
        });
    }

    public void onProgramStart() {
        File loadFile = new File(getFolder().getAbsolutePath() + File.separator + "programstart.js");

        if (loadFile.exists()) {
            try {
                ScriptHandler.getHandler().runScript(loadFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void load() {
        File loadFile = new File(getFolder().getAbsolutePath() + File.separator + "load.js");

        if (loadFile.exists()) {
            try {
                ScriptHandler.getHandler().runScript(loadFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void unload() {
        File loadFile = new File(getFolder().getAbsolutePath() + File.separator + "unload.js");

        if (loadFile.exists()) {
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

    public ConfigValue getDownloadLink() {
        return downloadLink;
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

    public PersonalitySettingsHandler getSettingsHandler() {
        return this.settingsHandler;
    }
}
