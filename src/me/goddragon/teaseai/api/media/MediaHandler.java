package me.goddragon.teaseai.api.media;

import java.io.*;
import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;

import me.goddragon.teaseai.utils.TeaseLogger;


/**
 * Created by GodDragon on 22.03.2018.
 */
public class MediaHandler {
    private static MediaHandler handler = new MediaHandler();

    private final ImageHandler imageHandler;
    private final VideoHandler videoPlayer;
    private final AudioHandler audioPlayer;
    private final AtomicBoolean imagesLocked = new AtomicBoolean(false);

    public MediaHandler() {
        imageHandler = new ImageHandler();
        videoPlayer = new VideoHandler(
                this::asyncOnVideoPlaybackStarted, this::asyncOnVideoPlaybackEnded);
        audioPlayer = new AudioHandler();
    }

    private void asyncOnVideoPlaybackStarted() {
        imagesLocked.set(true);
    }

    private void asyncOnVideoPlaybackEnded() {
        imagesLocked.set(false);
    }

    public void playVideo(File file) {
        playVideo(file, false);
    }

    public void playVideo(File file, boolean wait) {
        if (!file.exists()) {
            TeaseLogger.getLogger().log(
                    Level.SEVERE, "Video " + file.getPath() + " does not exist.");
        } else {
            try {
                playVideo(file.toURI().toURL().toExternalForm(), wait);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public void playVideo(String uri, boolean waitUntilPlaybackFinished) {
        Media media = null;
        try {
            media = new Media(uri);
        } catch (MediaException ex) {
            TeaseLogger.getLogger().log(
                    Level.SEVERE, "Video format of '" + uri + "' unknown: " + ex.getMessage());
        }

        if (media != null) {
            videoPlayer.play(media, waitUntilPlaybackFinished);
        }
    }

    public void stopVideo() {
        videoPlayer.stop();
    }

    public boolean isPlayingVideo() {
        return videoPlayer.isPlaying();
    }

    public void playAudio(String path) {
        playAudio(path, false);
    }

    public void playAudio(String path, boolean waitUntilPlaybackFinished) {
        playAudio(new File(path), waitUntilPlaybackFinished);
    }

    public void playAudio(File file) {
        playAudio(file, false);
    }

    public void playAudio(File file, boolean waitUntilPlaybackFinished) {
        if (file == null || !file.exists()) {
            TeaseLogger.getLogger().log(Level.SEVERE,
                    "Audio " + (file == null ? "null" : file.getPath()) + " does not exist.");
        } else {
            try {
                playAudioWithURI(file.toURI().toURL().toExternalForm(), waitUntilPlaybackFinished);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public void playAudioWithURI(String uri, boolean waitUntilPlaybackFinished) {
        audioPlayer.playAudioWithURI(uri, waitUntilPlaybackFinished);
    }

    public void stopAudio(String path) {
        stopAudio(new File(path));
    }

    public void stopAudio(File file) {
        audioPlayer.stopAudio(file);
    }

    public void stopAllAudio() {
        audioPlayer.stopAllAudio();
    }

    public void showPicture(File file) {
        showPicture(file, 0);
    }

    public void showPicture(File file, int durationSeconds) {
        videoPlayer.stop();
        imageHandler.showPicture(file, durationSeconds);
    }

    public File tryGetImageFromURL(String url) {
        return imageHandler.tryGetImageFromURL(url);
    }

    public boolean isImagesLocked() {
        return imagesLocked.get();
    }

    public void setImagesLocked(boolean imagesLocked) {
        this.imagesLocked.set(imagesLocked);
    }

    public String getCurrentImageURL() {
        return imageHandler.getCurrentImageURL();
    }

    public static MediaHandler getHandler() {
        return handler;
    }

    public static void setHandler(MediaHandler handler) {
        MediaHandler.handler = handler;
    }
}
