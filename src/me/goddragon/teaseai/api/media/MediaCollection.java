package me.goddragon.teaseai.api.media;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.config.ConfigValue;
import me.goddragon.teaseai.utils.RandomUtils;

import java.io.File;
import java.util.*;

/**
 * Created by GodDragon on 26.03.2018.
 */
public class MediaCollection {

    private final Map<MediaFetishType, Map<MediaType, List<MediaHolder>>> folders = new HashMap<>();

    private final Map<MediaType, List<MediaHolder>> mediaHolders = new HashMap<>();
    private final Collection<String> registeredURLs = new HashSet<>();

    public MediaCollection() {
        for (MediaFetishType mediaFetishType : MediaFetishType.values()) {
            addMediaHolders(mediaFetishType, mediaFetishType.getMediaFolders(MediaType.IMAGE));
            addMediaHolders(mediaFetishType, mediaFetishType.getMediaFolders(MediaType.VIDEO));

            //Handle url files for images here
            List<String> tumblrURLFileNames = mediaFetishType.getURLFileNames(MediaType.IMAGE);

            for (String fileName : tumblrURLFileNames) {
                if (!fileName.equals("null") && fileName.contains(".txt")) {
                    MediaURL mediaURL = new MediaURL(MediaType.IMAGE, null, fileName);

                    //Skip it because there was an error during initialization
                    if (mediaURL.getUrl() == null) {
                        continue;
                    }

                    addMediaHolder(mediaFetishType, mediaURL);
                }
            }
        }

        File urlFileFolder = new File(MediaURL.URL_FILE_PATH);
        if (urlFileFolder.exists()) {
            for (File file : urlFileFolder.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    //We actually pass the file name instead of the url here however that is fine because the url is loaded from the file's first line anyway
                    MediaURL mediaURL = new MediaURL(MediaType.IMAGE, null, file.getName());
                    //Now check whether we have already registered that url file
                    if (!registeredURLs.contains(mediaURL.getUrl())) {
                        addMediaHolder(null, mediaURL);
                    }
                }
            }
        }
    }

    public void addMediaHolders(MediaFetishType mediaFetishType, List<MediaHolder> mediaHolders) {
        for (MediaHolder mediaHolder : mediaHolders) {
            if (mediaHolder != null) {
                addMediaHolder(mediaFetishType, mediaHolder);
            }
        }
    }

    public void addMediaHolder(MediaFetishType mediaFetishType, MediaHolder mediaHolder) {
        if (mediaFetishType != null) {
            if (!folders.containsKey(mediaFetishType)) {
                folders.put(mediaFetishType, new HashMap<>());
            }

            if (!folders.get(mediaFetishType).containsKey(mediaHolder.getMediaType())) {
                folders.get(mediaFetishType).put(mediaHolder.getMediaType(), new ArrayList<>());
            }

            if (!folders.get(mediaFetishType).get(mediaHolder.getMediaType()).contains(mediaHolder)) {
                folders.get(mediaFetishType).get(mediaHolder.getMediaType()).add(mediaHolder);
            }
        }

        //Map all media holders here
        if (!mediaHolders.containsKey(mediaHolder.getMediaType())) {
            mediaHolders.put(mediaHolder.getMediaType(), new ArrayList<>());
        }

        if (!mediaHolders.get(mediaHolder.getMediaType()).contains(mediaHolder)) {
            mediaHolders.get(mediaHolder.getMediaType()).add(mediaHolder);
        }

        //Add all urls so we can check whether an url is already registered
        if (mediaHolder instanceof MediaURL) {
            if (!registeredURLs.contains(mediaHolder)) {
                registeredURLs.add(((MediaURL) mediaHolder).getUrl());
            }
        }
    }

    public void removeMediaHolder(MediaHolder mediaHolder) {
        mediaHolders.get(mediaHolder.getMediaType()).remove(mediaHolder);

        for (MediaFetishType mediaFetishType : MediaFetishType.values()) {
            removeMediaHolder(mediaHolder, mediaFetishType);
        }
    }

    public void removeMediaHolder(MediaHolder mediaHolder, MediaFetishType mediaFetishType) {
        if (folders.containsKey(mediaFetishType) && folders.get(mediaFetishType).containsKey(mediaHolder.getMediaType())) {
            folders.get(mediaFetishType).get(mediaHolder.getMediaType()).remove(mediaHolder);
        }
    }

    public File getRandomTeaseFile(MediaType mediaType) {
        List<MediaHolder> mediaFolders = mediaHolders.get(mediaType);
        List<MediaHolder> urls = new ArrayList<>();

        for (MediaHolder mediaHolder : mediaFolders) {
            if (mediaHolder instanceof MediaURL && ((MediaURL) mediaHolder).isUseForTease()) {
                urls.add(mediaHolder);
            }
        }

        //Add all tease folders as well
        if(folders.containsKey(MediaFetishType.TEASE) && folders.get(MediaFetishType.TEASE).containsKey(mediaType)) {
            mediaFolders = folders.get(MediaFetishType.TEASE).get(mediaType);
        } else {
            mediaFolders = new ArrayList<>();
        }

        if(mediaFolders != null) {
            urls.addAll(mediaFolders);
        }

        urls.addAll(getCustomTagged(MediaFetishType.TEASE, mediaType));

        if (!urls.isEmpty()) {
            MediaHolder mediaURL = urls.get(RandomUtils.randInt(0, urls.size() - 1));
            return mediaURL.getRandomMedia();
        } else {
            return null;
        }
    }

    public boolean hasFetishType(MediaFetishType fetishType) {
        return folders.containsKey(fetishType);
    }

    public boolean hasFetishMediaType(MediaFetishType fetishType, MediaType mediaType) {
        return folders.containsKey(fetishType) && folders.get(fetishType).containsKey(mediaType);
    }

    public File getRandomFile(MediaFetishType fetishType, MediaType mediaType) {
        List<MediaHolder> mediaFolders;

        if(hasFetishMediaType(fetishType, mediaType)) {
            mediaFolders = folders.get(fetishType).get(mediaType);
        } else {
            mediaFolders = new ArrayList<>();
        }

        mediaFolders.addAll(getCustomTagged(fetishType, mediaType));

        if (mediaFolders.isEmpty()) {
            return null;
        }

        for (int tries = 0; tries < 10; tries++) {
            MediaHolder mediaHolder = mediaFolders.get(RandomUtils.randInt(0, mediaFolders.size() - 1));
            File file = mediaHolder.getRandomMedia();

            //We found a file
            if (file != null) {
                return file;
            }
            //No file found, the folder seems to be empty and we can remove it
            else {
                //Maybe the users adds stuff later. Don't remove it
                //mediaFolders.remove(mediaHolder);
            }
        }

        return null;
    }

    public List<MediaPictureHolder> getCustomTagged(MediaFetishType fetishType, MediaType mediaType) {
        List<MediaPictureHolder> mediaFolders = new ArrayList<>();

        if(hasFetishMediaType(MediaFetishType.CUSTOM_TAGGED, mediaType)) {
            for(MediaHolder mediaHolder : getMediaHolders(MediaFetishType.CUSTOM_TAGGED, mediaType)) {
                if(mediaHolder instanceof MediaFolder) {
                    MediaPictureHolder pictureHolder = ((MediaFolder) mediaHolder).filterForTags(fetishType.toPictureTag());

                    //Empty check, if it returns null it's empty
                    if(pictureHolder.getRandomMedia() != null) {
                        mediaFolders.add(pictureHolder);
                    }
                }
            }
        }

        return mediaFolders;
    }

    public boolean isPathAssigned(String path, MediaFetishType fetishType, MediaType mediaType) {
        for (MediaHolder mediaHolder : getMediaHolders(fetishType, mediaType)) {
            if (mediaHolder instanceof MediaFolder) {
                if (((MediaFolder) mediaHolder).getFile().getPath().equalsIgnoreCase(path)) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<MediaHolder> getMediaHolders(MediaFetishType fetishType, MediaType mediaType) {
        return folders.getOrDefault(fetishType, new HashMap<>()).getOrDefault(mediaType, new ArrayList<>());
    }

    public MediaURL getByURL(String url) {
        for (MediaHolder mediaHolder : mediaHolders.get(MediaType.IMAGE)) {
            if (mediaHolder instanceof MediaURL && ((MediaURL) mediaHolder).getUrl().equals(url)) {
                return (MediaURL) mediaHolder;
            }
        }

        return null;
    }

    public void saveMediaFetishType(MediaFetishType mediaFetishType, MediaType mediaType, MediaHolderType holderType) {
        ConfigValue configValue = mediaFetishType.getConfigValues().get(mediaType).get(holderType);
        List<MediaHolder> folders = this.folders.get(mediaFetishType).get(mediaType);
        //If the folder list is empty we can already set it to the default value
        String newValue = folders.isEmpty() ? configValue.getDefaultValue().toString() : "";
        switch (holderType) {
            case FOLDER:
                for (MediaHolder mediaHolder : folders) {
                    if (mediaHolder instanceof MediaFolder) {
                        newValue += ((MediaFolder) mediaHolder).getFile().getPath() + ";";
                    }
                }
                break;
            case URL:
                for (MediaHolder mediaHolder : folders) {
                    if (mediaHolder instanceof MediaURL) {
                        newValue += ((MediaURL) mediaHolder).getFile().getName() + ";";
                    }
                }
                break;
        }

        configValue.setValue(newValue);
        TeaseAI.application.getConfigHandler().saveConfig();
    }

    public Map<MediaFetishType, Map<MediaType, List<MediaHolder>>> getFolders() {
        return folders;
    }

    public Map<MediaType, List<MediaHolder>> getMediaHolders() {
        return mediaHolders;
    }

    public Collection<String> getRegisteredURLs() {
        return registeredURLs;
    }
}
