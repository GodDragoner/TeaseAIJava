package me.goddragon.teaseai.api.scripts.nashorn;

import java.io.File;
import java.util.logging.Level;

import me.goddragon.teaseai.api.media.MediaHandler;
import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class PlayVideoFunction extends CustomFunctionExtended {
    public PlayVideoFunction() {
        super("playVideo", "showVideo", "displayVideo");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    protected void onCall(String pathOrUrl) {
        onCall(pathOrUrl, false);
    }

    protected void onCall(String pathOrUrl, Boolean waitUntilFinishedPlaying){
        if (isHttpUrl(pathOrUrl)) {
            MediaHandler.getHandler().playVideo(pathOrUrl, waitUntilFinishedPlaying);
        } else {
            final File file = FileUtils.getRandomMatchingFile(pathOrUrl);
            if (file != null) {
                MediaHandler.getHandler().playVideo(file, waitUntilFinishedPlaying);
            } else {
                TeaseLogger.getLogger().log(Level.SEVERE,
                        "Matching video file for path " + pathOrUrl + " does not exist.");
            }
        }
    }

    private boolean isHttpUrl(String path) {
        final String lowerCasePath = path.toLowerCase();
        return lowerCasePath.startsWith("http://") || lowerCasePath.startsWith("https://");
    }
}
