package me.goddragon.teaseai.utils.libraries.ripme.ripper.rippers.video;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.goddragon.teaseai.utils.libraries.ripme.ripper.VideoRipper;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Http;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Utils;

public class MotherlessVideoRipper extends VideoRipper {

    private static final String HOST = "motherless";

    public MotherlessVideoRipper(URL url) throws IOException {
        super(url);
    }

    @Override
    public String getHost() {
        return HOST;
    }

    @Override
    public boolean canRip(URL url) {
        Pattern p = Pattern.compile("^https?://[wm.]*motherless\\.com/[A-Z0-9]+.*$");
        Matcher m = p.matcher(url.toExternalForm());
        return m.matches();
    }

    @Override
    public URL sanitizeURL(URL url) throws MalformedURLException {
        return url;
    }

    @Override
    public String getGID(URL url) throws MalformedURLException {
        Pattern p = Pattern.compile("^https?://[wm.]*motherless\\.com/([A-Z0-9]+).*$");
        Matcher m = p.matcher(url.toExternalForm());
        if (m.matches()) {
            return m.group(1);
        }

        throw new MalformedURLException(
                "Expected motherless format:"
                        + "motherless.com/####"
                        + " Got: " + url);
    }

    @Override
    public void rip() throws IOException {
        LOGGER.log(Level.INFO, "    Retrieving " + this.url);
        String html = Http.url(this.url).get().toString();
        if (html.contains("__fileurl = '")) {
            LOGGER.log(Level.SEVERE, "WTF");
        }
        List<String> vidUrls = Utils.between(html, "__fileurl = '", "';");
        if (vidUrls.isEmpty()) {
            throw new IOException("Could not find video URL at " + url);
        }
        String vidUrl = vidUrls.get(0);
        addURLToDownload(new URL(vidUrl), HOST + "_" + getGID(this.url));
        waitForThreads();
    }
}