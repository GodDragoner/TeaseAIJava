package me.goddragon.teaseai.api.media;

import me.goddragon.teaseai.api.picture.PictureSet;
import me.goddragon.teaseai.api.picture.PictureTag;
import me.goddragon.teaseai.api.picture.TaggedPicture;
import me.goddragon.teaseai.api.picture.TagsFile;
import me.goddragon.teaseai.utils.RandomUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by GodDragon on 26.03.2018.
 */
public class MediaFolder extends MediaHolder {

    private final File folder;
    private final List<File> mediaFiles = new ArrayList<>();

    private PictureSet pictureSet;

    public MediaFolder(MediaType mediaType, File folder) {
        super(mediaType);
        this.folder = folder;

        loadMediaFiles();
    }

    public void loadMediaFiles() {
        for (File file : folder.listFiles()) {
            if (file.isFile() && getMediaType().hasSupportedExtension(file)) {
                mediaFiles.add(file);
            }
        }

        if (TagsFile.checkFolderForTagsFile(folder)) {
            pictureSet = new PictureSet(folder);
        }
    }

    @Override
    public File getRandomMedia() {
        if (mediaFiles.isEmpty()) {
            return null;
        }

        return mediaFiles.get(RandomUtils.randInt(0, mediaFiles.size() - 1));
    }

    public File getFile() {
        return folder;
    }

    @Override
    public String toString() {
        return getFile().getPath();
    }

    public MediaPictureHolder filterForTags(PictureTag... pictureTags) {
        return filterForTags(Arrays.asList(pictureTags));
    }

    public MediaPictureHolder filterForTags(List<PictureTag> pictureTags) {
        return filterForTags(pictureTags, new ArrayList<>());
    }

    public MediaPictureHolder filterForTags(List<PictureTag> mustHave, List<PictureTag> mustNotHave) {
        //Empty dress state list -> no dress state filter
        List<TaggedPicture> pictures = pictureSet.getPicturesForTagStates(new ArrayList<>(), mustHave);

        return new MediaPictureHolder(MediaType.IMAGE, pictures.stream().filter(taggedPicture -> {
            for (PictureTag tag : mustNotHave) {
                if (taggedPicture.hasTag(tag)) {
                    return false;
                }
            }

            return true;
        }).collect(Collectors.toList()));
    }

    public boolean hasTagsFile() {
        return pictureSet != null;
    }
}
