package me.goddragon.teaseai.utils;

import me.goddragon.teaseai.TeaseAI;

import javax.net.ssl.*;
import java.io.*;
import java.nio.file.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GodDragon on 05.04.2018.
 */
public class FileUtils {

    public static List<File> getMatchingFiles(String pathString) {
        pathString = pathString.replace("/", File.separator).replace("\\", File.separator);


        List<File> files = new ArrayList<>();

        if (pathString.lastIndexOf(File.separator) < 0) {
            files.add(new File(pathString));
            return files;
        }

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

        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }

        in.close();
        out.close();
    }

    /**
     * This function recursively copy all the sub folder and files from sourceFolder to destinationFolder
     */
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
                if (file.startsWith(".") && ignoreHidden) {
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

    public static boolean containsFile(File folder, String fileName) {
        String files[] = folder.list();

        //Iterate over all files
        for (String file : files) {
            if (!new File(folder, file).isDirectory() && file.endsWith(fileName)) {
                return true;
            }
        }

        return false;
    }

    public static File findFile(File parentFolder, String fileName) {
        String files[] = parentFolder.list();

        //Iterate over all files
        for (String file : files) {
            if (!new File(parentFolder, file).isDirectory()) {
                if (file.endsWith(fileName)) {
                    return new File(parentFolder, file);
                }
            } else {
                //Try to find the file one directory deeper
                File foundFile = findFile(new File(parentFolder, file), fileName);

                if (foundFile != null) {
                    return foundFile;
                }
            }
        }

        return null;
    }

    public static void deleteFileOrFolder(final Path path) throws IOException {
        /*Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
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
        });*/

        delete(path.toFile());
    }

    public static void delete(File file) throws IOException {
        if (file.isDirectory()) {
            //Directory is empty, then delete it
            if (file.list().length == 0) {
                file.delete();

            } else {
                //List all the directory contents
                String files[] = file.list();

                for (String temp : files) {
                    //Construct the file structure
                    File fileDelete = new File(file, temp);

                    //Recursive delete
                    delete(fileDelete);
                }

                //Check the directory again, if empty then delete it
                if (file.list().length == 0) {
                    file.delete();
                }
            }

        } else {
            //If file, then delete it
            file.delete();
        }
    }

    public static String stripExtension(String str) {
        //Handle null case specially.
        if (str == null) {
            return null;
        }

        //Get position of last '.'.
        int pos = str.lastIndexOf(".");

        //If there wasn't any '.' just return the string as is.
        if (pos == -1) {
            return str;
        }

        //Otherwise return the string, up to the dot.
        return str.substring(0, pos);
    }


    public static String getExtension(File file) {
        return getExtension(file.getName());
    }

    public static String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1).toLowerCase();
        }

        return extension;
    }


    public static boolean isSupportedPictureExtension(String extension) {
        return extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("gif") || extension.equalsIgnoreCase("png");
    }

    public static String getNormalizedFileName(File file) {
        return stripExtension(file.getName()) + "." + getExtension(file).toLowerCase();
    }

    /**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param resourceName ie.: "/SmartLibrary.dll"
     * @return The path to the exported resource
     * @throws Exception
     */
    public static String exportResource(String resourceName) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try {
            //note that each / is a directory down in the "jar tree" been the jar the root of the tree
            stream = TeaseAI.class.getResourceAsStream(resourceName);
            if (stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            jarFolder = getTAJPath().replace('\\', '/');
            resStreamOut = new FileOutputStream(jarFolder + resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }

        return jarFolder + resourceName;
    }

    public static void disableSslVerification() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                    // TODO Auto-generated method stub

                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                    // TODO Auto-generated method stub

                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public static boolean folderContains(File directory, String fileName) {
        if(!directory.isDirectory()) {
            return false;
        }

        for(File file : directory.listFiles()) {
            if(file.getName().equalsIgnoreCase(fileName)) {
                return true;
            }
        }

        return false;
    }


    public static File getLibFolder() {
        return new File(getTAJPath() + File.separator + "lib");
    }

    public static String getTAJPath() {
        return Paths.get(System.getProperty("user.dir")).toString();
    }
}
