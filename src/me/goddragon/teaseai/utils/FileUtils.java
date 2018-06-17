package me.goddragon.teaseai.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;

/**
 * Created by GodDragon on 05.04.2018.
 */
public class FileUtils {

    public static List<File> getMatchingFiles(String pathString) {
        pathString = pathString.replace("/", File.separator).replace("\\", File.separator);

        List<File> files = new ArrayList<>();

        String dirPath = pathString.substring(0, pathString.lastIndexOf(File.separator));
        String fileWildcard = pathString.substring(pathString.lastIndexOf(File.separator) + 1);

        Path dir = FileSystems.getDefault().getPath(dirPath);
        try {
            if (!dir.toFile().exists()) {
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

        if (files.isEmpty()) {
            return null;
        }

        return files.get(RandomUtils.randInt(0, files.size() - 1));
    }


    public static void copyInputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }

        in.close();
        out.close();
    }

    /**
     * This function recursively copy all the sub folder and files from sourceFolder to destinationFolder
     * */
    public static void copyFolder(File sourceFolder, File destinationFolder, boolean ignoreHidden) throws IOException {
        //Check if sourceFolder is a directory or file
        //If sourceFolder is file; then copy the file directly to new location
        if (sourceFolder.isDirectory()) {
            //Verify if destinationFolder is already present; If not then create it
            if (!destinationFolder.exists()) {
                destinationFolder.mkdir();
            }

            //Get all files from source directory
            String files[] = sourceFolder.list();

            //Iterate over all files and copy them to destinationFolder one by one
            for (String file : files) {
                //Skip hidden files
                if(file.startsWith(".") && ignoreHidden) {
                    continue;
                }

                File srcFile = new File(sourceFolder, file);
                File destFile = new File(destinationFolder, file);

                //Recursive function call
                copyFolder(srcFile, destFile, ignoreHidden);
            }
        } else {
            //Create required folders that are missing
            destinationFolder.getParentFile().mkdirs();

            //Copy the file content from one place to another
            Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void deleteFileOrFolder(final Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
            @Override public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                Files.delete(file);

                return CONTINUE;
            }

            @Override public FileVisitResult visitFileFailed(final Path file, final IOException e) {
                return handleException(e);
            }

            private FileVisitResult handleException(final IOException e) {
                //Replace with more robust error handling
                e.printStackTrace();

                return TERMINATE;
            }

            @Override public FileVisitResult postVisitDirectory(final Path dir, final IOException e) throws IOException {
                if(e != null) {
                    return handleException(e);
                }

                Files.delete(dir);
                return CONTINUE;
            }
        });
    }
}
