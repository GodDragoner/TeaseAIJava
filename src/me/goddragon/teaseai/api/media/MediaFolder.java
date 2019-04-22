package me.goddragon.teaseai.api.media;

import me.goddragon.teaseai.utils.RandomUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GodDragon on 26.03.2018.
 */
public class MediaFolder extends MediaHolder {

    private final File folder;
    private final List<File> mediaFiles = new ArrayList<>();

    public MediaFolder(MediaType mediaType, File folder) {
        super(mediaType);
        this.folder = folder;

        loadMediaFiles();
    }

    public void loadMediaFiles() {
        for (File file : folder.listFiles()) {
            if (file.isFile() && getMediaType().hasSupportedExtenstion(file)) {
                mediaFiles.add(file);
            }
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
}
