package me.goddragon.teaseai.utils.libraries.ripme.ripper.rippers.video;

import me.goddragon.teaseai.utils.libraries.ripme.ripper.VideoRipper;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Http;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Utils;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VidearnRipper extends VideoRipper {

    private static final String HOST = "videarn";

    public VidearnRipper(URL url) throws IOException {
        super(url);
    }

    @Override
    public String getHost() {
        return HOST;
    }

    @Override
    public boolean canRip(URL url) {
        Pattern p = Pattern.compile("^https?://[wm.]*videarn\\.com/[a-zA-Z0-9\\-]+/([0-9]+).*$");
        Matcher m = p.matcher(url.toExternalForm());
        return m.matches();
    }

    @Override
    public URL sanitizeURL(URL url) throws MalformedURLException {
        return url;
    }

    @Override
    public String getGID(URL url) throws MalformedURLException {
        Pattern p = Pattern.compile("^https?://[wm.]*videarn\\.com/[a-zA-Z0-9\\-]+/([0-9]+).*$");
        Matcher m = p.matcher(url.toExternalForm());
        if (m.matches()) {
            return m.group(1);
        }

        throw new MalformedURLException(
                "Expected videarn format:"
                        + "videarn.com/.../####-..."
                        + " Got: " + url);
    }

    @Override
    public void rip() throws IOException {
        LOGGER.log(Level.INFO, "Retrieving " + this.url);
        Document doc = Http.url(url).get();
        List<String> mp4s = Utils.between(doc.html(), "file:\"", "\"");
        if (mp4s.isEmpty()) {
            throw new IOException("Could not find files at " + url);
        }
        String vidUrl = mp4s.get(0);
        addURLToDownload(new URL(vidUrl), HOST + "_" + getGID(this.url));
        waitForThreads();
    }
}