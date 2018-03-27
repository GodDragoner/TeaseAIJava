package me.goddragon.teaseai.api.media;

import com.sun.xml.internal.ws.util.StringUtils;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.config.ConfigValue;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GodDragon on 26.03.2018.
 */
public enum MediaFetishType {

    HARDCORE, SOFTCORE, LESBIAN, BLOWJOB, FEMDOM, HENTAI, GAY, MALEDOM, CAPTIONS, GENERAL, BOOBS, BUTTS;

    private Map<MediaType, Map<MediaHolderType, ConfigValue>> configValues = new HashMap<>();

    MediaFetishType() {
        Map<MediaHolderType, ConfigValue> imageMap = new HashMap<>();
        Map<MediaHolderType, ConfigValue> videoMap = new HashMap<>();

        imageMap.put(MediaHolderType.FOLDER, new ConfigValue(this.toString().toLowerCase() + "ImagePaths", "null", TeaseAI.application.getConfigHandler()));
        imageMap.put(MediaHolderType.URL, new ConfigValue(this.toString().toLowerCase() + "ImageURLs", "null", TeaseAI.application.getConfigHandler()));

        videoMap.put(MediaHolderType.FOLDER, new ConfigValue(this.toString().toLowerCase() + "VideoPaths", "null", TeaseAI.application.getConfigHandler()));
        videoMap.put(MediaHolderType.URL, new ConfigValue(this.toString().toLowerCase() + "VideoURLs", "null", TeaseAI.application.getConfigHandler()));

        configValues.put(MediaType.IMAGE, imageMap);
        configValues.put(MediaType.VIDEO, videoMap);
    }

    public List<String> getURLFileNames(MediaType mediaType) {
        String value = configValues.get(mediaType).get(MediaHolderType.URL).getValue();

        if(value.isEmpty() || !value.contains(";")) {
            return Arrays.asList(value);
        }

        String[] split = value.split(";");


        return Arrays.asList(split);
    }

    public MediaFolder getMediaFolder(MediaType mediaType) {
        File file = getFolder(mediaType);

        if(!file.exists()) {
            return null;
        }

        return new MediaFolder(mediaType, getFolder(mediaType));
    }

    public File getFolder(MediaType mediaType) {
        ConfigValue configValue = configValues.get(mediaType).get(MediaHolderType.FOLDER);

        return new File(configValue.getValue());
    }

    public Map<MediaType, Map<MediaHolderType, ConfigValue>> getConfigValues() {
        return configValues;
    }

    @Override
    public String toString() {
        return StringUtils.capitalize(super.toString().toLowerCase());
    }
}
