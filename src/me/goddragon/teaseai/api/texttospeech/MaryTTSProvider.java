package me.goddragon.teaseai.api.texttospeech;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;

import javax.sound.sampled.AudioInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaryTTSProvider extends TTSProvider implements TTSVoicable {

    private MaryInterface marytts;
    private AudioInputStream audioInputStream;
    private AudioPlayer audioPlayer;

    public MaryTTSProvider() {
        try {
            String javaVersion = System.getProperty("java.version");

            boolean changeVersion = false;
            if (javaVersion.length() < 3) {
                changeVersion = true;
                System.setProperty("java.version", javaVersion + ".1");
            }

            this.marytts = new LocalMaryInterface();

            //Reset to original java version
            if (changeVersion) {
                System.setProperty("java.version", javaVersion);
            }

        } catch (MaryConfigurationException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playFetchedAudio(String text, float gainValue, boolean daemon, boolean join) {
        if(text.trim().isEmpty()) {
            return;
        }

        super.playFetchedAudio(text, gainValue, daemon, join);

        try {
            // Player is a thread(threads can only run one time) so it can be used has to be initiated every time
            audioPlayer = new AudioPlayer(audioInputStream);
            audioPlayer.setGain(gainValue);
            audioPlayer.setDaemon(daemon);
            audioPlayer.start();

            if (join) {
                audioPlayer.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        isFetched = false;
    }

    @Override
    public void stopAudio() {
        // Stop the previous player
        if (audioPlayer != null) {
            audioPlayer.cancel();
        }
    }

    @Override
    public void preFetchAudio(String text) throws Exception {
        if(text.trim().isEmpty()) {
            return;
        }

        this.audioInputStream = marytts.generateAudio(text);
        super.preFetchAudio(text);
    }

    @Override
    public void setVoice(String s) {
        marytts.setVoice(s);
    }
}
