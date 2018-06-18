package me.goddragon.teaseai.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by GodDragon on 17.06.2018.
 */
public class ZipUtils {

    public static void unzipFile(String inputFile, String outputFolder) {
        unzipFile(new File(inputFile), new File(outputFolder));
    }

    public static void unzipFile(File inputFile, File outputFolder) {
        try {
            ZipFile zipFile = new ZipFile(inputFile);

            Enumeration zipEntries = zipFile.entries();

            if (!outputFolder.exists()) {
                outputFolder.mkdir();
            }

            String outputDir = outputFolder.getAbsolutePath() + File.separator;
            while (zipEntries.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) zipEntries.nextElement();

                if (zipEntry.isDirectory()) {
                    //System.out.println("Extracting directory: " + outputDir + zipEntry.getName());

                    new File(outputDir + zipEntry.getName()).mkdirs();
                    continue;
                }

                //System.out.println("Extracting file: " + outputDir + zipEntry.getName());

                FileUtils.copyInputStream(zipFile.getInputStream(zipEntry), new BufferedOutputStream(new FileOutputStream(outputDir + zipEntry.getName())));
            }

            zipFile.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void zipFolder(File sourceFolder, File outputFile) {
        byte[] buffer = new byte[1024];

        try {
            FileOutputStream fos = new FileOutputStream(outputFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            for(String file : generateFileList(new ArrayList<>(), sourceFolder, sourceFolder)) {
                ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);

                FileInputStream in = new FileInputStream(sourceFolder.getAbsoluteFile() + File.separator + file);

                int len;
                while((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                in.close();
            }

            zos.closeEntry();

            //Remember to close it
            zos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static List<String> generateFileList(List<String> fileList, File sourceFolder, File node) {
        //Add files only
        if (node.isFile()) {
            fileList.add(generateZipEntry(sourceFolder, node.getPath()));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename: subNote) {
                generateFileList(fileList, sourceFolder, new File(node, filename));
            }
        }

        return fileList;
    }

    private static String generateZipEntry(File sourceFolder, String file) {
        return file.substring(sourceFolder.getPath().length() + 1, file.length());
    }
}
