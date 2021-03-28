package me.goddragon.teaseai.api.media;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.utils.TeaseLogger;

public class AudioHandler {
    public void playAudioWithURI(String uriText, boolean waitUntilPlaybackFinished) {
        URI uri;
        try {
            uri = new URI(uriText);
        } catch (URISyntaxException ex) {
            TeaseLogger.getLogger().log(
                    Level.SEVERE, "Cannot parse '" + uriText + "' as a URI: " + ex.getMessage());
            return;
        }

        Media media;
        try {
            media = new Media(uriText);
        } catch (MediaException ex) {
            TeaseLogger.getLogger().log(
                    Level.SEVERE, "Audio format of '" + uri + "' unknown: " + ex.getMessage());
            return;
        }

        final SelfDisposingMediaPlayer mediaPlayer =
                new SelfDisposingMediaPlayer(media, null, this::asyncOnPlaybackEnded);
        playingAudioClips.computeIfAbsent(uri, dummy -> new ArrayList<SelfDisposingMediaPlayer>())
                .add(mediaPlayer);
        isWaitingForPlaybackToFinish = waitUntilPlaybackFinished;
        mediaPlayer.start();

        if (waitUntilPlaybackFinished) {
            while (mediaPlayer.isPlaying()) {
                TeaseAI.application.waitPossibleScripThread(0);
                TeaseAI.application.checkForNewResponses();
            }

            isWaitingForPlaybackToFinish = false;
        }

        purgeFinishedPlayers();
    }

    public void stopAudio(File file) {
        playingAudioClips.computeIfPresent(file.toURI(), (uri, listOfMediaPlayers) -> {
            listOfMediaPlayers.forEach(SelfDisposingMediaPlayer::stop);
            return null;
        });
    }

    public void stopAllAudio() {
        playingAudioClips.forEach(
                (uri, listOfMediaPlayers) -> listOfMediaPlayers.forEach(SelfDisposingMediaPlayer::stop));

        playingAudioClips.clear();
    }

    private void purgeFinishedPlayers() {
        playingAudioClips.forEach((uri, listOfMediaPlayers)
                                          -> listOfMediaPlayers.removeIf(
                                                  mediaPlayer -> !mediaPlayer.isPlaying()));

        playingAudioClips.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    private void asyncOnPlaybackEnded() {
        if (isWaitingForPlaybackToFinish) {
            final Thread scriptThread = TeaseAI.getApplication().getScriptThread();
            synchronized (scriptThread) {
                scriptThread.notifyAll();
            }
        }
    }

    private boolean isWaitingForPlaybackToFinish;
    private Map<URI, List<SelfDisposingMediaPlayer>> playingAudioClips = new HashMap<>();
}
