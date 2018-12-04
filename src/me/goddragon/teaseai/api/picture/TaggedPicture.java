package me.goddragon.teaseai.api.picture;

import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by GodDragon on 26.03.2018.
 */
public class TaggedPicture {

    private File file;
    private DressState dressState;
    private HashSet<PictureTag> tags = new HashSet<>();
    private TagsFile imageTagFile;

    public TaggedPicture(String fileName, String[] tags, File folder) {
        this.file = new File(folder.getPath() + File.separator + fileName);

        if (!file.exists()) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Tagged picture " + fileName + " in folder " + folder.getAbsolutePath() + " does not exist!");
            return;
        }

        //Not relevant here. Make a dedicated gui that can allow you to list all duplicated files
        /*if (PictureHandler.getHandler().checkDuplicate(file)) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Duplicate file " + file.getPath());
            return;
        }*/

        String extension = FileUtils.getExtension(file).toLowerCase();

        if (!extension.equals("png") && !extension.equals("jpg") && !extension.equals("gif")) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid file extension " + extension + ". Expected format png jpg or gif!");
            return;
        }

        for (int x = 0; x < tags.length; x++) {
            String tag = tags[x];

            //Too short to be a real tag
            if (tag.length() < 2) {
                continue;
            }

            DressState dressState = DressState.getByTag(tag);
            if (dressState != null) {
                this.dressState = dressState;
                continue;
            }

            PictureTag pictureTag = PictureTag.getByTag(folder, tag);
            if (pictureTag != null) {
                this.tags.add(pictureTag);
            }
        }

        if (dressState == null) {
            this.dressState = DressState.FULLY_DRESSED;
        }

        getTagsFolder();
    }

    public TaggedPicture(File file) {
        this(file, false);

    }

    public TaggedPicture(File file, boolean readOnly) {
        this.file = file;

        if (!file.exists()) {
            TeaseLogger.getLogger().log(Level.SEVERE, "File does not exist!");
            return;
        }

        //Not relevant here. Make a dedicated gui that can allow you to list all duplicated files
        /*if (PictureHandler.getHandler().checkDuplicate(file)) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Duplicate file " + file.getPath());
            return;
        }*/

        String extension = FileUtils.getExtension(file).toLowerCase();

        if (!extension.equals("png") && !extension.equals("jpg") && !extension.equals("gif")) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Invalid file extension " + extension + ". Expected format png jpg or gif!");
            return;
        }

        getTagsFolder();

        this.tags = imageTagFile.getTags(this.file);

        this.dressState = imageTagFile.getDressState(this.file);

        if (dressState == null && !readOnly) {
            this.dressState = DressState.FULLY_DRESSED;
        }

        if (tags == null) {
            tags = new HashSet<>();
        }
    }

    public boolean isDuplicate() {
        return PictureHandler.getHandler().checkDuplicate(file);
    }

    public boolean move(String newPath) {
        File folder = this.file.getParentFile();
        HashSet<PictureTag> localTags = getTags();
        deleteTags();
        File newFile = new File(newPath);
        TeaseLogger.getLogger().log(Level.INFO, "Moving file to " + newPath);

        try {
            Files.move(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Failed to move file to path " + newPath);
            e.printStackTrace();
        }
        
        /*if (!file.renameTo(newFile)) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Failed to move file to path " + newPath);
            return false;
        }*/

        newFile = new File(newPath);
        if (!newFile.exists()) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Failed to move file to path " + newPath);
            return false;
        }

        this.file = newFile;
        getTagsFolder();
        setTags(localTags);
        File newFolder = newFile.getParentFile();

        List<File> uniqueFolders = Arrays.asList(PictureHandler.getHandler().getFolders());

        if (folder.equals(newFolder)) {
            TeaseLogger.getLogger().log(Level.SEVERE, "File's destination location is the same as its source! " + newFolder.getPath());
            return false;
        } else if (uniqueFolders.contains(folder) && !uniqueFolders.contains(newFolder)) {
            PictureHandler.getHandler().removeUniquePicture(this.file);
        } else if (!uniqueFolders.contains(folder) && uniqueFolders.contains(newFolder)) {
            PictureHandler.getHandler().addUniquePicture(this.file);
        }

        TeaseLogger.getLogger().log(Level.INFO, "File location after move " + this.file.getPath());
        TeaseLogger.getLogger().log(Level.INFO, "File tags after move " + this.tags.toString());
        return true;
    }

    public void delete() {
        deleteTags();
        File folder = this.file.getParentFile();

        if (Arrays.asList(PictureHandler.getHandler().getFolders()).contains(folder)) {
            PictureHandler.getHandler().removeUniquePicture(this.file);
        }

        this.file.delete();
    }

    public boolean hasTag(PictureTag pictureTag) {
        return tags.contains(pictureTag);
    }

    public boolean hasDressState(DressState dressState) {
        return this.dressState == dressState;
    }

    public File getFile() {
        return file;
    }

    public DressState getDressState() {
        return dressState;
    }

    public void setDressState(DressState dressState) {
        if (this.dressState == dressState) {
            return;
        }

        this.dressState = dressState;

        imageTagFile.setDressState(dressState, this.file);

        if (dressState == null && (tags == null || tags.size() == 0)) {
            imageTagFile.deleteTags(file);
        }
    }

    public HashSet<PictureTag> getTags() {
        return tags;
    }

    public void addTags(PictureTag... tags) {
        HashSet<PictureTag> addTags = new HashSet<>(Arrays.asList(tags));
        addTags(addTags);
    }

    public void addTags(HashSet<PictureTag> addTags) {
        if (imageTagFile == null) {
            TeaseLogger.getLogger().log(Level.SEVERE, "No image tag file defined for this file");
        } else {
            imageTagFile.addTags(addTags, file);
            tags = imageTagFile.getTags(file);
        }
    }

    public void setTags(PictureTag... tags) {
        setTags(new HashSet<>(Arrays.asList(tags)));
    }

    public void setTags(HashSet<PictureTag> setTags) {
        //Issue in here somewhere
        if (imageTagFile == null) {
            TeaseLogger.getLogger().log(Level.SEVERE, "No image tag file defined for this file");
        } else {
            if (setTags.equals(tags)) {
                //TeaseLogger.getLogger().log(Level.INFO, "Tags to set already equals current tags." + setTags.toString() + " " + tags.toString());
                return;
            }

            //If tagslist is empty, delete tags
            if ((tags == null || setTags.size() == 0) && dressState == null) {
                imageTagFile.deleteTags(file);
            } else {
                imageTagFile.setTags(setTags, file);
            }

            tags = setTags;
        }
    }

    public boolean hasTags(PictureTag... tags) {
        return hasTags(Arrays.asList(tags));
    }

    public boolean hasTags(Collection<PictureTag> tags) {
        if (this.tags.containsAll(tags)) {
            return true;
        }

        return false;
    }

    public void deleteTags() {
        imageTagFile.deleteTags(this.file);
        if (tags == null) {
            tags = new HashSet<>();
        }
        tags.clear();
    }

    private void getTagsFolder() {
        File parentFolder = file.getParentFile();

        if (!parentFolder.exists()) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Parent file does not exist. Is the file located in the root of your hard drive?");
        }

        imageTagFile = TagsFile.getTagsFile(parentFolder);

        if (imageTagFile == null) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Tag file null " + this.file);
        }
    }
}