package me.goddragon.teaseai;

import me.goddragon.teaseai.utils.TeaseLogger;
import me.goddragon.teaseai.utils.ZipUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.logging.Level;


public class Main {

    public static double JAVA_VERSION = getJavaVersion();

    public static void main(String[] args) {
        if (args.length == 0 && JAVA_VERSION > 10) {
            try {
                //Re-launch the app itself with VM option passed
                File currentDir = Paths.get(System.getProperty("user.dir")).toFile();

                File libFolder = null;
                for (File file : currentDir.listFiles()) {
                    if (file.isDirectory()) {
                        for (File dirFile : file.listFiles()) {
                            if (dirFile.isDirectory() && dirFile.getName().equals("lib")) {
                                libFolder = dirFile;
                            }
                        }
                    }
                }

                if (libFolder == null) {
                    TeaseLogger.getLogger().log(Level.SEVERE, "No JavaFX installation found. Starting download...");

                    String fileName = "openJFX";

                    if(!new File( fileName + ".zip").exists()) {
                        URL url = new URL("http://download2.gluonhq.com/openjfx/11/openjfx-11_windows-x64_bin-sdk.zip");
                        HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
                        long completeFileSize = httpConnection.getContentLength();

                        java.io.BufferedInputStream in = new java.io.BufferedInputStream(httpConnection.getInputStream());
                        java.io.FileOutputStream fos = new java.io.FileOutputStream(fileName + ".zip");
                        java.io.BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
                        byte[] data = new byte[1024];
                        long downloadedFileSize = 0;
                        int x;
                        int oldProgress = 0;

                        while ((x = in.read(data, 0, 1024)) >= 0) {
                            downloadedFileSize += x;

                            //Calculate progress
                            final int currentProgress = (int) ((((double) downloadedFileSize) / ((double) completeFileSize)) * 100d);

                            if (currentProgress > oldProgress) {
                                TeaseLogger.getLogger().log(Level.INFO, "Download Progress at " + currentProgress + "%");
                                oldProgress = currentProgress;
                            }

                            bout.write(data, 0, x);
                        }
                        bout.close();
                        in.close();
                    }

                    TeaseLogger.getLogger().log(Level.INFO, "Finished downloading OpenJFX. Unzipping...");

                    //Unzip the downloaded file
                    ZipUtils.unzipFile(new File(fileName + ".zip"), currentDir);

                    //Delete the downloaded zip file
                    //newUpdateZipFile.delete();

                    TeaseLogger.getLogger().log(Level.SEVERE, "No JavaFX installation found...");
                } else {
                    Process process = Runtime.getRuntime().exec(new String[]{"java", "\"--module-path=" + libFolder.getPath() + "\"", "--add-modules=javafx.controls,javafx.fxml,javafx.base,javafx.media,javafx.graphics,javafx.swing,javafx.web", "-jar", "TeaseAI.jar", "test"});
                    BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = input.readLine()) != null) {
                        System.out.println(line);
                    }

                    input.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            System.exit(0);
        } else {
            TeaseAI.main(args);
        }
    }


    static double getJavaVersion() {
        return Double.parseDouble(System.getProperty("java.specification.version"));
    }
}
