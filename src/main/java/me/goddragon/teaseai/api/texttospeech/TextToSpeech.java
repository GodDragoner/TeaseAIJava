package me.goddragon.teaseai.api.texttospeech;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.modules.synthesis.Voice;
import marytts.signalproc.effects.AudioEffect;
import marytts.signalproc.effects.AudioEffects;

import javax.sound.sampled.AudioInputStream;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author GOXR3PLUS
 */
public class TextToSpeech {

    private AudioPlayer tts;
    private MaryInterface marytts;

    /**
     * Constructor
     */
    public TextToSpeech() {
        try {
            String javaVersion = System.getProperty("java.version");

            boolean changeVersion = false;
            if (javaVersion.length() < 3) {
                changeVersion = true;
                System.setProperty("java.version", javaVersion + ".1");
            }

            marytts = new LocalMaryInterface();

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


    //----------------------GENERAL METHODS---------------------------------------------------//

    /**
     * Transform text to speech
     *
     * @param text   The text that will be transformed to speech
     * @param daemon <br>
     *               <b>True</b> The thread that will start the text to speech Player will be a daemon Thread <br>
     *               <b>False</b> The thread that will start the text to speech Player will be a normal non daemon Thread
     * @param join   <br>
     *               <b>True</b> The current Thread calling this method will wait(blocked) until the Thread which is playing the Speech finish <br>
     *               <b>False</b> The current Thread calling this method will continue freely after calling this method
     */
    public void speak(String text, float gainValue, boolean daemon, boolean join) {
        // Stop the previous player
        stopSpeaking();

        try {
            AudioInputStream audio = marytts.generateAudio(text);

            // Player is a thread(threads can only run one time) so it can be
            // used has to be initiated every time
            tts = new AudioPlayer(audio);
            tts.setGain(gainValue);
            tts.setDaemon(daemon);
            tts.start();
            if (join)
                tts.join();

        } catch (Exception e) {
            // an exception found
            e.printStackTrace();
        }
    }

    /**
     * Stop the MaryTTS from Speaking
     */
    public void stopSpeaking() {
        // Stop the previous player
        if (tts != null)
            tts.cancel();
    }

    //----------------------GETTERS---------------------------------------------------//

    /**
     * Available voices in String representation
     *
     * @return The available voices for MaryTTS
     */
    public Collection<Voice> getAvailableVoices() {
        return Voice.getAvailableVoices();
    }

    /**
     * @return the marytts
     */
    public MaryInterface getMarytts() {
        return marytts;
    }

    /**
     * Return a list of available audio effects for MaryTTS
     *
     * @return
     */
    public List<AudioEffect> getAudioEffects() {
        return StreamSupport.stream(AudioEffects.getEffects().spliterator(), false).collect(Collectors.toList());
    }

    //----------------------SETTERS---------------------------------------------------//

    /**
     * Change the default voice of the MaryTTS
     *
     * @param voice
     * @throws IllegalArgumentException
	 *             if voiceName is not among the {@link #getAvailableVoices()}.
     */
    public void setVoice(String voice) throws IllegalArgumentException {
        marytts.setVoice(voice);
    }

}
