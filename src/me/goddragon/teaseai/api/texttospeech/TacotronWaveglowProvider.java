package me.goddragon.teaseai.api.texttospeech;

import me.goddragon.teaseai.api.media.MediaHandler;
import me.goddragon.teaseai.utils.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class TacotronWaveglowProvider extends TTSProvider {

    @Override
    public void preFetchAudio(String text) throws Exception {
        if(text.trim().isEmpty()) {
            return;
        }

        URL preFetch = new URL("http://localhost:8080/tts/generate?message=" + StringUtils.urlEncodeValue(text));
        HttpURLConnection con = (HttpURLConnection) preFetch.openConnection();
        con.setRequestMethod("GET");

        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        } else {
            System.out.println("GET request not worked");
        }

        super.preFetchAudio(text);
    }

    @Override
    public void playFetchedAudio(String text, float gainValue, boolean daemon, boolean join) {
        if(text.trim().isEmpty()) {
            return;
        }

        super.playFetchedAudio(text, gainValue, daemon, join);

        try {
            URL url = new URL("http://localhost:8080/tts/get/sound.wav");
            MediaHandler.getHandler().playAudioWithURI(url.toExternalForm(), join);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        isFetched = false;
    }

    @Override
    public void stopAudio() {
        MediaHandler.getHandler().stopAllAudio();
    }
}
