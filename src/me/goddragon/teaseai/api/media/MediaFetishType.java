package me.goddragon.teaseai.api.media;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.config.ConfigValue;
import me.goddragon.teaseai.utils.StringUtils;

import java.io.File;
import java.util.*;

/**
 * Created by GodDragon on 26.03.2018.
 */
public enum MediaFetishType {

    HARDCORE, SOFTCORE, LESBIAN, BLOWJOB, FEMDOM, LEZDOM, HENTAI, GAY, MALEDOM, CAPTIONS, GENERAL, BOOBS, BUTTS;

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

        if (value.isEmpty() || !value.contains(";")) {
            return Arrays.asList(value);
        }

        String[] split = value.split(";");


        return Arrays.asList(split);
    }

    public List<MediaHolder> getMediaFolders(MediaType mediaType) {
        List<File> files = getFolders(mediaType);

        List<MediaHolder> folders = new ArrayList<>();
        for (File file : files) {
            if (!file.exists()) {
                continue;
            }

            folders.add(new MediaFolder(mediaType, file));
        }

        return folders;
    }

    public List<File> getFolders(MediaType mediaType) {
        String value = configValues.get(mediaType).get(MediaHolderType.FOLDER).getValue();

        if (value.isEmpty() || !value.contains(";")) {
            return Arrays.asList(new File(value));
        }

        String[] split = value.split(";");
        List<File> files = new ArrayList<>();

        for (String folderPath : split) {
            files.add(new File(folderPath));
        }

        return files;
    }

    public Map<MediaType, Map<MediaHolderType, ConfigValue>> getConfigValues() {
        return configValues;
    }

    @Override
    public String toString() {
        return StringUtils.capitalize(super.toString().toLowerCase());
    }
}
