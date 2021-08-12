package me.goddragon.teaseai.api.media;

import me.goddragon.teaseai.api.picture.TaggedPicture;
import me.goddragon.teaseai.utils.RandomUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaPictureHolder extends MediaHolder{

    private final List<File> mediaFiles = new ArrayList<>();

    public MediaPictureHolder(MediaType mediaType, List<TaggedPicture> taggedPictures) {
        super(mediaType);

        for(TaggedPicture taggedPicture : taggedPictures) {
            mediaFiles.add(taggedPicture.getFile());
        }
    }

    @Override
    public File getRandomMedia() {
        if (mediaFiles.isEmpty()) {
            return null;
        }

        return mediaFiles.get(RandomUtils.randInt(0, mediaFiles.size() - 1));
    }
}
