package me.goddragon.teaseai.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GodDragon on 05.04.2018.
 */
public class FileUtils {

    public static List<File> getMatchingFiles(String pathString) {
        pathString = pathString.replace("/", FileSystems.getDefault().getSeparator());

        List<File> files = new ArrayList<>();

        File file = new File(pathString);
        String dirPath = file.getParent();
        String fileWildcard = file.getName();

        Path dir = FileSystems.getDefault().getPath(dirPath);
        try {
            if(!dir.toFile().exists()) {
                return new ArrayList<>();
            }

            DirectoryStream<Path> stream = Files.newDirectoryStream(dir, fileWildcard);

            for (Path path : stream) {
                files.add(path.toFile());
            }

            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return files;
    }

    public static File getRandomMatchingFile(String pathString) {
        List<File> files = getMatchingFiles(pathString);

        if(files.isEmpty()) {
            return null;
        }

        return files.get(RandomUtils.randInt(0, files.size() - 1));
    }
}
