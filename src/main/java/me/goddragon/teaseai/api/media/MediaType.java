package me.goddragon.teaseai.api.media;

import me.goddragon.teaseai.utils.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by GodDragon on 26.03.2018.
 */
public enum MediaType {

    VIDEO("mp4"), IMAGE("jpg", "png", "jpeg", "gif");

    private final Collection<String> supportedExtensions = new HashSet<>();

    MediaType(String... supportedExtensions) {
        this.supportedExtensions.addAll(Arrays.asList(supportedExtensions));
    }

    public boolean hasSupportedExtension(File file) {
        String fileExtension = FileUtils.getExtension(file);
        for (String supportedExtension : supportedExtensions) {
            if (fileExtension.equalsIgnoreCase(supportedExtension)) {
                return true;
            }
        }

        return false;
    }

    public Collection<String> getSupportedExtensions() {
        return supportedExtensions;
    }
}
