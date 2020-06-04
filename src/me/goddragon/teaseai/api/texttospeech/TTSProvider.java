package me.goddragon.teaseai.api.texttospeech;

public abstract class TTSProvider {

    protected boolean isFetched = false;

    public TTSProvider() {
    }

    public void preFetchAudio(String text) throws Exception {
        this.isFetched = true;
    }

    public void playFetchedAudio(String text, float gainValue, boolean daemon, boolean join) {
        if(!this.isFetched) {
            try {
                preFetchAudio(text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void stopAudio();
}
