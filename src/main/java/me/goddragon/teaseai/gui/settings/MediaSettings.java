package me.goddragon.teaseai.gui.settings;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.media.MediaFetishType;
import me.goddragon.teaseai.api.media.MediaHolderType;
import me.goddragon.teaseai.api.media.MediaType;

/**
 * Created by GodDragon on 28.03.2018.
 */
public class MediaSettings {

    private final SettingsController settingsController;

    public MediaSettings(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    public void initiate() {
        new URLGenreSettings(settingsController).initiate();
        new GenreMediaSettings(settingsController).initiate();
        new URLMediaSettings(settingsController).initiate();
        MediaTagging.create(settingsController);
    }

    public void saveMediaPaths(MediaType mediaType, MediaFetishType mediaFetishType) {
        if (mediaFetishType != null) {
            TeaseAI.application.getMediaCollection().saveMediaFetishType(mediaFetishType, mediaType, MediaHolderType.FOLDER);
        }
    }

    public MediaFetishType getSelectedURLMediaFetish() {
        if (settingsController.urlFetishTypeList.getSelectionModel().getSelectedItems().size() == 1) {
            MediaFetishType mediaFetishType = (MediaFetishType) settingsController.urlFetishTypeList.getSelectionModel().getSelectedItem();
            return mediaFetishType;
        }

        return null;
    }
}
