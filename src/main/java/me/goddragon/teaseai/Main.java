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
import java.util.logging.Level;

public class Main {

    public static double JAVA_VERSION = getJavaVersion();
    public static String OPERATING_SYSTEM = System.getProperty("os.name").toLowerCase();

    public static void main(String[] args) {
        TeaseAI.main(args);
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
            Process process = Runtime.getRuntime().exec(new String[]{"java", "--module-path=" + getJavaFXLibFolder().getPath(), "--add-modules=javafx.controls,javafx.fxml,javafx.base,javafx.media,javafx.graphics,javafx.swing,javafx.web", "-jar", "TeaseAI.jar"});

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

    static double getJavaVersion() {
        return Double.parseDouble(System.getProperty("java.specification.version"));
    }
}
