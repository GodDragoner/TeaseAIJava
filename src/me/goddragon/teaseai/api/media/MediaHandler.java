package me.goddragon.teaseai.api.media;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by GodDragon on 22.03.2018.
 */
public class MediaHandler {

    private static MediaHandler handler = new MediaHandler();

    private HashMap<URI, MediaPlayer> playingAudioClips = new HashMap<>();

    private MediaPlayer currentVideoPlayer = null;
    private boolean imagesLocked = false;

    public MediaPlayer playVideo(File file) {
        return playVideo(file, false);
    }

    public MediaPlayer playVideo(File file, boolean wait) {
        if(!file.exists()) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Video " + file.getPath() + " does not exist.");
            return null;
        }

        try {
            currentVideoPlayer = new MediaPlayer(new Media(file.toURI().toURL().toExternalForm()));
            currentVideoPlayer.setAutoPlay(true);
            this.imagesLocked = true;

            TeaseAI.application.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    MediaView mediaView = TeaseAI.application.getController().getMediaView();
                    StackPane mediaViewBox = TeaseAI.application.getController().getMediaViewBox();

                    //Handle visibilities
                    mediaView.setOpacity(1);
                    TeaseAI.application.getController().getImageView().setOpacity(0);

                    mediaView.setPreserveRatio(true);
                    mediaView.fitWidthProperty().bind(mediaViewBox.widthProperty());
                    mediaView.fitHeightProperty().bind(mediaViewBox.heightProperty());
                    mediaView.setMediaPlayer(currentVideoPlayer);
                }
            });

            //Check if we want to wait for the media to finish
            if(wait) {
                waitForPlayer(currentVideoPlayer);
                currentVideoPlayer = null;
            } else {
                //Unlock the images again (of course they can be unlocked by the user during the video)
                currentVideoPlayer.setOnEndOfMedia(new Runnable() {
                    @Override
                    public void run() {
                        imagesLocked = false;
                        currentVideoPlayer = null;
                    }
                });
            }

            return currentVideoPlayer;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void stopVideo() {
        currentVideoPlayer.stop();
        currentVideoPlayer = null;
    }

    public void showPicture(File file) {
        showPicture(file, 0);
    }

    public void showPicture(File file, int durationSeconds) {
        if(!file.exists()) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Picture " + file.getPath() + " does not exist.");
            return;
        }

        TeaseAI.application.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                MediaView mediaView = TeaseAI.application.getController().getMediaView();

                if (mediaView.getMediaPlayer() != null) {
                    mediaView.getMediaPlayer().stop();
                }

                ImageView imageView = TeaseAI.application.getController().getImageView();

                //Handle visibilities
                mediaView.setOpacity(0);
                imageView.setOpacity(1);

                imageView.setImage(new Image(file.toURI().toString()));
            }
        });

        if(durationSeconds > 0) {
            TeaseAI.application.sleepThread(Thread.currentThread(), durationSeconds*1000);
        }
    }

    private MediaPlayer getAudioPlayer(File file) {
        Media hit = new Media(file.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(hit);
        return mediaPlayer;
    }

    /*public MediaPlayer playSoundFromFolder(String path) {
        return playSoundFromFolder(path, false);
    }

    public MediaPlayer playSoundFromFolder(String path, boolean wait) {
        return playAudio(new File("Sounds\\" + path), wait);
    }*/

    public MediaPlayer playAudio(String path) {
        return playAudio(path, false);
    }

    public MediaPlayer playAudio(String path, boolean wait) {
        return playAudio(new File(path), wait);
    }

    public MediaPlayer playAudio(File file) {
        return playAudio(file, false);
    }

    public MediaPlayer playAudio(File file, boolean wait) {
        if(!file.exists()) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Audio " + file.getPath() + " does not exist.");
            return null;
        }

        MediaPlayer mediaPlayer = getAudioPlayer(file);
        playingAudioClips.put(file.toURI(), mediaPlayer);
        mediaPlayer.play();

        if(wait) {
            waitForPlayer(mediaPlayer);
        }

        return mediaPlayer;
    }

    public void stopAudio(String path) {
        stopAudio(new File(path));
    }

    public void stopAudio(File file) {
        if (!playingAudioClips.containsKey(file.toURI())) {
            return;
        }

        playingAudioClips.get(file.toURI()).stop();
        playingAudioClips.remove(file.toURI());
    }

    public void stopAllAudio() {
        for (Map.Entry<URI, MediaPlayer> clips : playingAudioClips.entrySet()) {
            clips.getValue().stop();
            playingAudioClips.get(clips.getKey()).stop();
        }
    }

    public void waitForPlayer(MediaPlayer mediaPlayer) {
        final boolean[] hasFinishedPlaying = {false};
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                imagesLocked = false;

                synchronized (TeaseAI.application.getScriptThread()) {
                    TeaseAI.application.getScriptThread().notify();
                }

                hasFinishedPlaying[0] = true;
            }
        });

        while (!hasFinishedPlaying[0]) {
            TeaseAI.application.waitScriptThread(0);

            //Check whether there are new responses to handle
            TeaseAI.application.checkForNewResponses();
        }
    }

    public boolean isPlayingVideo() {
        return currentVideoPlayer != null;
    }

    public MediaPlayer getCurrentVideoPlayer() {
        return currentVideoPlayer;
    }

    public boolean isImagesLocked() {
        return imagesLocked;
    }

    public void setImagesLocked(boolean imagesLocked) {
        this.imagesLocked = imagesLocked;
    }

    public static MediaHandler getHandler() {
        return handler;
    }

    public static void setHandler(MediaHandler handler) {
        MediaHandler.handler = handler;
    }
}
