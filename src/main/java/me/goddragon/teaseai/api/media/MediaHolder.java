package me.goddragon.teaseai.api.media;

import java.io.File;

/**
 * Created by GodDragon on 26.03.2018.
 */
public abstract class MediaHolder {

    private final MediaType mediaType;

    public MediaHolder(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public abstract File getRandomMedia();

    public MediaType getMediaType() {
        return mediaType;
    }
}
