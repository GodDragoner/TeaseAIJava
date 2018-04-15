package me.goddragon.teaseai.api.picture;

import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;

/**
 * Created by GodDragon on 26.03.2018.
 */
public class TaggedPicture {

    private final File file;
    private DressState dressState;
    private final Collection<PictureTag> tags = new HashSet<>();

    public TaggedPicture(String fileName, String[] tags, File folder) {
        this.file = new File(folder, fileName);

        if(!file.exists()) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Tagged picture '" + fileName + "' in folder " + folder.getAbsolutePath() + " does not exist!");
        }

        for(int x = 0; x < tags.length; x++) {
            String tag = tags[x];

            //Too short to be a real tag
            if(tag.length() < 2) {
                continue;
            }

            DressState dressState = DressState.getByTag(tag);
            if(dressState != null) {
                this.dressState = dressState;
                continue;
            }

            PictureTag pictureTag = PictureTag.getByTag(folder, tag);
            if(pictureTag != null) {
                this.tags.add(pictureTag);
            }
        }
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
        this.dressState = dressState;
    }

    public Collection<PictureTag> getTags() {
        return tags;
    }
}
