package me.goddragon.teaseai.utils.update;

import me.goddragon.teaseai.Main;
import me.goddragon.teaseai.api.config.ConfigHandler;
import me.goddragon.teaseai.api.config.ConfigValue;
import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 * Created by GodDragon on 18.06.2018.
 */
public class UpdateHandler {
    private static final String[] toKeepLocalLibraries = {"uber-EstimAPI-0.0.1-SNAPSHOT.jar"};

    public static String TEASE_AI_PROPERTIES_DEFAULT_LINK = "https://gist.githubusercontent.com/GodDragoner/6c7193903cb0695ff891e8468ad279cd/raw/TeaseAI.properties";

    private static UpdateHandler handler = new UpdateHandler();

    ConfigHandler urlConfig;
    ConfigValue version;
    ConfigValue downloadLink;
    ConfigValue libraryList;

    public boolean loadConfig() {
        String propertiesLink = TEASE_AI_PROPERTIES_DEFAULT_LINK;

        //No link given
        if (propertiesLink == null ||  propertiesLink.equals("null")) {
            return false;
        }

        try {
            URL url = new URL(propertiesLink);
            this.urlConfig = new ConfigHandler(url);
            this.version = new ConfigValue("version", "null", urlConfig);
            this.downloadLink = new ConfigValue("downloadLink", "null", urlConfig);
            this.libraryList = new ConfigValue("libraryList", "null", urlConfig);

            urlConfig.loadConfig();

            if (version.getValue() == null || version.getValue().equals("null")) {
                TeaseLogger.getLogger().log(Level.SEVERE, "Invalid tease ai properties file from url '" + propertiesLink + "'. Version number is missing.");                return false;

            }

            return true;
        } catch (MalformedURLException e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid tease ai properties url '" + propertiesLink + "'.");
        }

        return false;
    }

    public boolean checkLibraries() {
        if (urlConfig == null) {
            if(!loadConfig()) {
                return false;
            }
        }

        String libraries = libraryList.getValue();

        if(libraries.equalsIgnoreCase("null")) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid tease ai properties file from url. Library list is missing.");
            return false;
        }

        String[] urls = libraries.split(";");

        Set<String> loadedLibraries = new HashSet<>();

        for(File file : FileUtils.getLibFolder().listFiles()) {
            if(file.isFile()) {
                loadedLibraries.add(file.getName());
            }
        }

        Set<String> librariesListed = new HashSet<>();
        Set<String> libraryURLsToFetch = new HashSet<>();

        //https://github.com/GodDragoner/TeaseAIJava/raw/master/Resources/commons-collections-3.2.1.jar
        for(String url : urls) {
            int lastIndex = url.lastIndexOf("/");

            if(lastIndex < 0) {
                TeaseLogger.getLogger().log(Level.SEVERE, "Malformed library link '" + url + "'.");
                continue;
            }

            String fileName = url.substring(lastIndex + 1);

            if(!loadedLibraries.contains(fileName)) {
                libraryURLsToFetch.add(url);
                TeaseLogger.getLogger().log(Level.INFO, "Missing library " + fileName + " queued for fetch.");
            }

            librariesListed.add(fileName);
        }

        for(String localKeepLibrary : toKeepLocalLibraries) {
            TeaseLogger.getLogger().log(Level.INFO, "Library " + localKeepLibrary + " is only local but keeping and not deleting.");
        }

        librariesListed.addAll(Arrays.asList(toKeepLocalLibraries));

        //Delete unused libraries
        for(String loaded : loadedLibraries) {
            if(!librariesListed.contains(loaded)) {
                TeaseLogger.getLogger().log(Level.INFO, "Deleting unused library " + loaded + ".");

                try {
                    FileUtils.deleteFileOrFolder(new File(FileUtils.getLibFolder().getPath() + File.separator + loaded).toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(!libraryURLsToFetch.isEmpty()) {
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(null, "Missing libraries found. Would you like to download?", "Missing libraries", dialogButton);

            if (dialogResult != JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(null, "Can't run without all libraries. Exiting...");
                System.exit(0);
                return false;
            }

            ProgressMonitor progressMonitor = new ProgressMonitor(null, "Downloading...", "", 0, 0);

            for(String url : libraryURLsToFetch) {
                try {
                    if(!fetchLibrary(url, progressMonitor)) {
                        TeaseLogger.getLogger().log(Level.SEVERE, "Failed to fetch library '" + url + "'.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Main.restart();
        }

        return true;
    }


    public boolean fetchLibrary(String downloadLink, ProgressMonitor progressMonitor) throws IOException {
        String libFolderPath = FileUtils.getLibFolder().getPath() + File.separator;
        String libName = downloadLink.substring(downloadLink.lastIndexOf("/") + 1);

        URL url = new URL(downloadLink);
        HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
        long completeFileSize = httpConnection.getContentLength();

        progressMonitor.setNote("Downloading " + libName + "...");
        progressMonitor.setMaximum((int) completeFileSize);
        progressMonitor.setMinimum(0);

        BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
        FileOutputStream fos = new FileOutputStream(libFolderPath + libName);
        BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);

        byte[] data = new byte[1024];
        long downloadedFileSize = 0;
        int x;
        //int oldProgress = 0;

        while ((x = in.read(data, 0, 1024)) >= 0) {
            downloadedFileSize += x;

            progressMonitor.setProgress((int) downloadedFileSize);

            bout.write(data, 0, x);
        }

        bout.close();
        in.close();


        progressMonitor.close();
        return true;
    }

    public static UpdateHandler getHandler() {
        return handler;
    }

    public static void setHandler(UpdateHandler handler) {
        UpdateHandler.handler = handler;
    }
}
