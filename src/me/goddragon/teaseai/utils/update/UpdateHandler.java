package me.goddragon.teaseai.utils.update;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.config.ConfigHandler;
import me.goddragon.teaseai.api.config.ConfigValue;
import me.goddragon.teaseai.gui.ProgressForm;
import me.goddragon.teaseai.utils.ComparableVersion;
import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.TeaseLogger;
import me.goddragon.teaseai.utils.URLUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;

/**
 * Created by GodDragon on 18.06.2018.
 */
public class UpdateHandler {

    private static UpdateHandler handler = new UpdateHandler();

    public boolean checkForUpdate() {
        ConfigValue propertiesLink = TeaseAI.application.TEASE_AI_PROPERTIES_LINK;
        //No link given
        if (propertiesLink == null || propertiesLink.getValue() == null || propertiesLink.getValue().equals("null")) {
            return false;
        }

        try {
            URL url = new URL(propertiesLink.getValue());
            ConfigHandler urlConfig = new ConfigHandler(url);
            ConfigValue version = new ConfigValue("version", "null", urlConfig);
            ConfigValue downloadLink = new ConfigValue("downloadLink", "null", urlConfig);

            urlConfig.loadConfig();

            if (version.getValue() == null || version.getValue().equals("null")) {
                TeaseLogger.getLogger().log(Level.SEVERE, "Invalid tease ai properties file from url '" + propertiesLink.getValue() + "'. Version number is missing.");
                return false;
            }

            if (new ComparableVersion(version.getValue()).compareTo(new ComparableVersion(TeaseAI.application.VERSION)) > 0) {
                boolean[] update = {false};
                TeaseLogger.getLogger().log(Level.INFO, "New TAJ version " + version.getValue() + " available");
                TeaseAI.application.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("New TAJ version " + version.getValue() + " available");
                        alert.setContentText("New TAJ version " + version.getValue() + " available. Would you like to update?");
                        ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
                        alert.getButtonTypes().setAll(okButton, noButton);
                        alert.showAndWait().ifPresent(type -> {
                            if (type.getButtonData() == ButtonBar.ButtonData.YES) {
                                update[0] = true;
                            }

                            synchronized (UpdateHandler.this) {
                                UpdateHandler.this.notify();
                            }
                        });
                    }
                });

                try {
                    synchronized (UpdateHandler.this) {
                        UpdateHandler.this.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (update[0]) {
                    TeaseLogger.getLogger().log(Level.INFO, "Update process accepted. Fetching update from remote...");
                    fetchUpdate(version.getValue(), downloadLink.getValue());
                } else {
                    TeaseLogger.getLogger().log(Level.INFO, "Update process declined by user.");
                }

                return true;
            }
        } catch (MalformedURLException e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid tease ai properties url '" + propertiesLink.getValue() + "'.");
        }

        return false;
    }

    private void fetchUpdate(String version, String downloadLink) {
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws InterruptedException {
                try {
                    updateProgress(0, 10);

                    String updateFolderPath = TeaseAI.UPDATE_FOLDER + File.separator;
                    URL url = new URL(downloadLink);
                    int fileSize = URLUtils.getFileSize(url);

                    File newUpdateJarFile = new File(updateFolderPath + downloadLink.substring(downloadLink.lastIndexOf("/"), downloadLink.length()));
                    newUpdateJarFile.getParentFile().mkdirs();

                    ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                    FileOutputStream fos = new FileOutputStream(newUpdateJarFile);

                    //Update the progress bar based on the file size
                    new Thread() {
                        @Override
                        public void run() {
                            while (fos.getChannel().isOpen()) {
                                try {
                                    updateProgress(fos.getChannel().size(), fileSize);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.start();

                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    updateProgress(fileSize, fileSize);

                    TeaseLogger.getLogger().log(Level.INFO, "Update successfully downloaded. Launching Updater...");

                    String updaterName = "TAJUpdater.jar";

                    //Export the updater
                    FileUtils.exportResource("/" + updaterName);

                    try {
                        //Run taj in a separate system process
                        Runtime.getRuntime().exec("java -jar " + updaterName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    System.exit(0);
                } catch (MalformedURLException e) {
                    TeaseLogger.getLogger().log(Level.SEVERE, "Invalid download url '" + downloadLink + "' for TAJ '" + version + "'. Update failed.");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    TeaseLogger.getLogger().log(Level.SEVERE, "Something went wrong while updating TAJ to '" + version + "'.");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        };

        ProgressForm progressForm = new ProgressForm("Downloading TAJ " + version, task);

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

    public static UpdateHandler getHandler() {
        return handler;
    }

    public static void setHandler(UpdateHandler handler) {
        UpdateHandler.handler = handler;
    }
}
