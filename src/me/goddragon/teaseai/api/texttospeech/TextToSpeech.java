package me.goddragon.teaseai.api.texttospeech;

import marytts.modules.synthesis.Voice;
import marytts.signalproc.effects.AudioEffect;
import marytts.signalproc.effects.AudioEffects;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author GOXR3PLUS
 */
public class TextToSpeech {

    private TTSProvider provider;

    /**
     * Constructor
     */
    public TextToSpeech() {
        provider = new MaryTTSProvider();
    }


    //----------------------GENERAL METHODS---------------------------------------------------//

    /**
     * Transform text to speech
     *
     * @param daemon <br>
     *               <b>True</b> The thread that will start the text to speech Player will be a daemon Thread <br>
     *               <b>False</b> The thread that will start the text to speech Player will be a normal non daemon Thread
     * @param join   <br>
     *               <b>True</b> The current Thread calling this method will wait(blocked) until the Thread which is playing the Speech finish <br>
     *               <b>False</b> The current Thread calling this method will continue freely after calling this method
     */
    public void speakFetched(String text, float gainValue, boolean daemon, boolean join) {
        // Stop the previous player
        stopSpeaking();

        provider.playFetchedAudio(text, gainValue, daemon, join);
    }

    public void preFetch(String text) {
        try {
            provider.preFetchAudio(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop the MaryTTS from Speaking
     */
    public void stopSpeaking() {
        provider.stopAudio();
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
     * Return a list of available audio effects for MaryTTS
     *
     * @return
     */
    public List<AudioEffect> getAudioEffects() {
        return StreamSupport.stream(AudioEffects.getEffects().spliterator(), false).collect(Collectors.toList());
    }

    public TTSProvider getProvider() {
        return provider;
    }

    public void setProvider(TTSProvider provider) {
        this.provider = provider;
    }
}
