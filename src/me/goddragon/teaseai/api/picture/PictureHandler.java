package me.goddragon.teaseai.api.picture;

import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;
import java.util.logging.Level;

public class PictureHandler {

    public static final PictureHandler handler = new PictureHandler();
    private HashMap<String, File> uniquePictures;
    private File[] folders;

    public PictureHandler() {
        loadUniquePictures();
    }

    public static PictureHandler getHandler() {
        return handler;
    }

    public void setPictureFolders(File... folders) {
        this.folders = folders;
        if (uniquePictures != null) {
            uniquePictures = null;
        }
    }

    public void setDefaultFolders() {
        File normal = new File(FileUtils.getTAJPath() + File.separator + "Images" + File.separator + "Normal");
        File liked = new File(FileUtils.getTAJPath() + File.separator + "Images" + File.separator + "Liked");
        File loved = new File(FileUtils.getTAJPath() + File.separator + "Images" + File.separator + "Loved");
        ArrayList<File> defFiles = new ArrayList<>();

        if (normal.exists()) {
            defFiles.add(normal);
        }

        if (liked.exists()) {
            defFiles.add(liked);
        }

        if (loved.exists()) {
            defFiles.add(loved);
        }

        folders = new File[defFiles.size()];
        folders = defFiles.toArray(folders);
    }

    public File[] getFolders() {
        if (folders == null) {
            setDefaultFolders();
        }

        return folders.clone();
    }

    public static File getOrCreateFile(String path) {
        File thisFile = new File(path);
        new File(thisFile.getParent()).mkdirs();

        try {
            thisFile.createNewFile();
        } catch (IOException e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "File creation error: " + e.getMessage());
        }

        return thisFile;
    }

    public static File moveFile(File file, String newPath) {
        TaggedPicture thisPicture = new TaggedPicture(file);
        thisPicture.move(newPath);
        return thisPicture.getFile();
    }

    public void addUniquePicture(File unique) {
        String fileMd5 = calculateMD5(unique);

        synchronized (uniquePictures) {
            if (uniquePictures.containsKey(fileMd5)) {
                TeaseLogger.getLogger().log(Level.SEVERE, "Tried to add unique image that already exists!");
                return;
            }

            uniquePictures.put(fileMd5, unique);
        }
    }

    public void removeUniquePicture(File unique) {
        String fileMd5 = calculateMD5(unique);

        synchronized (uniquePictures) {
            if (!uniquePictures.containsKey(fileMd5)) {
                TeaseLogger.getLogger().log(Level.SEVERE, "Tried to remove unique image that doesnt exist!");
                return;
            }

            uniquePictures.remove(fileMd5);
        }
    }

    public List<TaggedPicture> getTaggedPicturesExact(PictureTag... imageTags) {
        return getTaggedPicturesExact(null, Arrays.asList(imageTags));
    }

    public List<TaggedPicture> getTaggedPicturesExact(DressState dressState, Collection<PictureTag> imageTags) {
        return getTaggedPicturesExact(null, imageTags, dressState);
    }

    public ArrayList<TaggedPicture> getTaggedPicturesExact(File folder, DressState dressState, PictureTag... imageTags) {
        return getTaggedPicturesExact(folder, Arrays.asList(imageTags), dressState);
    }

    public ArrayList<TaggedPicture> getTaggedPicturesExact(File folder, String... imageTags) {
        Collection<PictureTag> pictureTags = new HashSet<>();
        DressState dressState = null;

        for (String tag : imageTags) {
            //Legacy solution to allow people to use same terminology as in TAI
            tag = tag.toLowerCase().replaceAll("tag", "");

            try {
                dressState = DressState.valueOf(tag.toUpperCase());
            } catch (IllegalArgumentException ex) {
                try {
                    pictureTags.add(PictureTag.valueOf(tag.toUpperCase()));
                } catch (IllegalArgumentException ex2) {
                    TeaseLogger.getLogger().log(Level.SEVERE, "Trying to find images with tag '" + tag + "' which is neither a dress state nor a valid tag");
                }
            }
        }

        return getTaggedPicturesExact(folder, pictureTags, dressState);
    }

    public ArrayList<TaggedPicture> getTaggedPicturesExact(File folder, Collection<PictureTag> imageTags, DressState dressState) {
        ArrayList<TaggedPicture> picturesWithTags = new ArrayList<>();

        Collection<File> files;

        if (folder != null && folder.list() != null && folder.list().length != 0) {
            File[] fileArray = folder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".gif");
                }
            });

            //Nothing found
            if (fileArray == null) {
                files = new ArrayList<>();
            } else {
                files = Arrays.asList(fileArray);
            }
        } else {
            files = new ArrayList<>();
            //files = uniquePictures.values();
        }

        //synchronized (uniquePictures) {
        for (File thisFile : files) {
            TaggedPicture thisImage = new TaggedPicture(thisFile);
            if (thisImage.hasTags(imageTags)) {
                if (dressState == null || thisImage.getDressState().equals(dressState)) {
                    picturesWithTags.add(thisImage);
                }
            }
        }
        //}

        if (picturesWithTags.size() == 0) {
            return null;
        } else {
            return picturesWithTags;
        }
    }

    public boolean checkDuplicate(File isDupe) {
        String fileMd5 = calculateMD5(isDupe);

        if (uniquePictures == null) {
            loadUniquePictures();
        }

        synchronized (uniquePictures) {
            if (uniquePictures.containsKey(fileMd5)) {
                if (!uniquePictures.get(fileMd5).equals(isDupe)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void loadUniquePictures() {
        this.uniquePictures = new HashMap<>();

        if (folders == null) {
            setDefaultFolders();
        }

        synchronized (folders) {
            for (File folder : folders) {
                File[] files = folder.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".gif");
                    }
                });

                for (File thisFile : files) {
                    String thisMd5 = calculateMD5(thisFile);
                    synchronized (uniquePictures) {
                        if (!uniquePictures.containsKey(thisMd5)) {
                            uniquePictures.put(thisMd5, thisFile);
                        } else {
                            TeaseLogger.getLogger().log(Level.WARNING, "Duplicate files: " + thisFile.getPath() + " and " + uniquePictures.get(thisMd5));
                        }
                    }
                }
            }
        }
    }


    /**
     * calculateMD5 Internal method that will calculate the md5checksum for a file.
     * Do not call this directly unless you know what you are doing!
     **/
    public static String calculateMD5(File file) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "MD5 error: " + e.getMessage());
            return null;
        }

        FileInputStream is;
        try {
            is = new FileInputStream(file);
        } catch (Exception e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "MD5 error: " + e.getMessage());
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }

            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (Exception e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "MD5 error: " + e.getMessage());
            return null;
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                TeaseLogger.getLogger().log(Level.SEVERE, "MD5 error: " + e.getMessage());
                return null;
            }
        }
    }
}