package me.goddragon.teaseai;

import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.TeaseLogger;
import me.goddragon.teaseai.utils.ZipUtils;
import me.goddragon.teaseai.utils.update.UpdateHandler;

import javax.swing.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class Main {

    public static double JAVA_VERSION = getJavaVersion();
    public static String OPERATING_SYSTEM = System.getProperty("os.name").toLowerCase();

    public static void main(String[] args) {
        TeaseLogger.getLogger().log(Level.INFO, "Launching with command: '" + getCommandOfCurrentProcess() + "'");

        TeaseLogger.getLogger().log(Level.INFO, "Checking libraries for updates...");
        UpdateHandler.getHandler().checkLibraries();
        TeaseLogger.getLogger().log(Level.INFO, "Libraries checked and up-to-date.");

        List<String> input = ManagementFactory.getRuntimeMXBean().getInputArguments();

        boolean containsJavaFx = false;

        for (String s : input) {
            if (s.toLowerCase().contains("javafx")) {
                containsJavaFx = true;
                break;
            }
        }

        if (!containsJavaFx && JAVA_VERSION > 10) {
            try {
                //Re-launch the app itself with VM option passed
                File currentDir = Paths.get(System.getProperty("user.dir")).toFile();

                if (getJavaFXLibFolder() == null) {

                    TeaseLogger.getLogger().log(Level.SEVERE, "No JavaFX installation found. Asking for download...");

                    String fileName = "openJFX";

                    if (!new File(fileName + ".zip").exists()) {
                        int dialogButton = JOptionPane.YES_NO_OPTION;
                        int dialogResult = JOptionPane.showConfirmDialog(null, "No OpenJFX installation found. Would you like to download?", "OpenJFX", dialogButton);

                        if (dialogResult != JOptionPane.YES_OPTION) {
                            JOptionPane.showMessageDialog(null, "Can't run on Java 11 or higher without OpenJFX. Exiting...");
                            System.exit(0);
                            return;
                        }

                        if (!System.getProperty("os.arch").contains("64")) {
                            JOptionPane.showMessageDialog(null, "x86 systems are currently not supported by the auto updater." +
                                    " Please fetch your own version of OpenFX from https://gluonhq.com/products/javafx/");
                            System.exit(0);
                            return;
                        }

                        String downloadPath;
                        if (isWindows()) {
                            TeaseLogger.getLogger().log(Level.SEVERE, "Your running Windows. Fetching OpenJFX SDK...");
                            downloadPath = "https://download2.gluonhq.com/openjfx/11.0.2/openjfx-11.0.2_windows-x64_bin-sdk.zip";
                        } else if (isMac()) {
                            TeaseLogger.getLogger().log(Level.SEVERE, "Your running MacOS. Fetching OpenJFX SDK...");
                            downloadPath = "https://download2.gluonhq.com/openjfx/11.0.2/openjfx-11.0.2_osx-x64_bin-sdk.zip";
                        } else if (isUnix()) {
                            TeaseLogger.getLogger().log(Level.SEVERE, "Your running Linux/Unix. Fetching OpenJFX SDK...");
                            downloadPath = "https://download2.gluonhq.com/openjfx/11.0.2/openjfx-11.0.2_linux-x64_bin-sdk.zip";
                        } else {
                            JOptionPane.showMessageDialog(null, "Your OS is not supported by JavaFX yet! Exiting.");
                            System.exit(0);
                            return;
                        }


                        URL url = new URL(downloadPath);
                        HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
                        long completeFileSize = httpConnection.getContentLength();

                        ProgressMonitor progressMonitor = new ProgressMonitor(null, "Downloading OpenJFX...", "", 0, (int) completeFileSize);

                        BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
                        FileOutputStream fos = new FileOutputStream(fileName + ".zip");
                        BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);

                        byte[] data = new byte[1024];
                        long downloadedFileSize = 0;
                        int x;
                        //int oldProgress = 0;

                        while ((x = in.read(data, 0, 1024)) >= 0) {
                            downloadedFileSize += x;

                            //Calculate progress
                            //final int currentProgress = (int) ((((double) downloadedFileSize) / ((double) completeFileSize)) * 100d);

                            progressMonitor.setProgress((int) downloadedFileSize);

                            /*if (currentProgress > oldProgress) {
                                TeaseLogger.getLogger().log(Level.INFO, "Download Progress at " + currentProgress + "%");
                                oldProgress = currentProgress;
                            }*/

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
                }

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            TeaseLogger.getLogger().log(Level.INFO, "Restarting TAJ with JAVA-FX startup parameters...");
            restart();
        } else {
            TeaseLogger.getLogger().log(Level.INFO, "Initialization done.");
            TeaseAI.main(args);
        }
    }


    private static File getJavaFXLibFolder() {
        File currentDir = Paths.get(System.getProperty("user.dir")).toFile();

        for (File file : currentDir.listFiles()) {
            if (file.isDirectory()) {
                for (File dirFile : file.listFiles()) {
                    //Check if we have found the right lib folder containing the files we expect it to contain
                    if (dirFile.isDirectory() && dirFile.getName().equals("lib") && FileUtils.folderContains(dirFile, "javafx.base.jar")) {
                        //libFolder = dirFile;
                        return dirFile;
                    }
                }
            }
        }

        return null;
    }

    public static void restart() {
        try {
            File javaFXFolder = getJavaFXLibFolder();

            String launchParameter = getCurrentJavaPath();

            if(launchParameter == null) {
                launchParameter = "java";
            } else {
                if(isWindows()) {

                }
                else if(!launchParameter.startsWith("/")) {
                    launchParameter = Paths.get(System.getProperty("user.dir")).toAbsolutePath() + File.separator + launchParameter;
                }

                //launchParameter = "\"" + launchParameter + "\"";
            }

            TeaseLogger.getLogger().log(Level.INFO, "Restarting with installation " + launchParameter);

            if (javaFXFolder == null) {
                Process process = Runtime.getRuntime().exec(new String[]{launchParameter, "-jar", "TeaseAI.jar"});
            } else {
                String modulePath = "--module-path=" + getJavaFXLibFolder().getPath();
                String modules = "--add-modules=javafx.controls,javafx.fxml,javafx.base,javafx.media,javafx.graphics,javafx.swing,javafx.web";

                System.out.println("Starting with parameters: " + modulePath + " " + modules);

                Process process = Runtime.getRuntime().exec(new String[]{launchParameter, modulePath, modules, "-jar", "TeaseAI.jar"});
            }


            /*BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }

            input.close();*/
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }

    private static Optional<String> getCommandOfCurrentProcess() {
        ProcessHandle processHandle = ProcessHandle.current();
        return processHandle.info().command();
    }

    public static String getCurrentJavaPath() {
        String launch = getCommandOfCurrentProcess().get();

        if (launch != null) {
            return launch;
        }

        return null;
    }

    public static boolean isWindows() {
        return (OPERATING_SYSTEM.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OPERATING_SYSTEM.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OPERATING_SYSTEM.indexOf("nix") >= 0 || OPERATING_SYSTEM.indexOf("nux") >= 0 || OPERATING_SYSTEM.indexOf("aix") > 0);
    }

    public static boolean isSolaris() {
        return (OPERATING_SYSTEM.indexOf("sunos") >= 0);
    }

    public static String getOS() {
        if (isWindows()) {
            return "win";
        } else if (isMac()) {
            return "osx";
        } else if (isUnix()) {
            return "uni";
        } else if (isSolaris()) {
            return "sol";
        } else {
            return "err";
        }
    }

    public static double getJavaVersion() {
        return Double.parseDouble(System.getProperty("java.specification.version"));
    }
}
