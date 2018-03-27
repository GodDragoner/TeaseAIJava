package me.goddragon.teaseai.api.media;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by GodDragon on 26.03.2018.
 */
public enum MediaType {

    VIDEO("mp4"), IMAGE("jpg", "png");

    private final Collection<String> supportedExtensions = new HashSet<>();

    MediaType(String... supportedExtensions) {
        this.supportedExtensions.addAll(Arrays.asList(supportedExtensions));
    }

    public boolean hasSupportedExtenstion(File file) {
        for(String supprtedExtension : supportedExtensions) {
            if(file.getName().endsWith("." + supprtedExtension)) {
                return true;
            }
        }

        return false;
    }

    public Collection<String> getSupportedExtensions() {
        return supportedExtensions;
    }
}
