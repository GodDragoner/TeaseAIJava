package me.goddragon.teaseai.utils.libraries.ripme.ripper.rippers;

import me.goddragon.teaseai.utils.libraries.ripme.ripper.AbstractHTMLRipper;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Http;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FemjoyhunterRipper extends AbstractHTMLRipper {

    public FemjoyhunterRipper(URL url) throws IOException {
        super(url);
    }

    @Override
    public String getHost() {
        return "femjoyhunter";
    }

    @Override
    public String getDomain() {
        return "femjoyhunter.com";
    }

    @Override
    public String getGID(URL url) throws MalformedURLException {
        Pattern p = Pattern.compile("https?://www.femjoyhunter.com/([a-zA-Z0-9_-]+)/?");
        Matcher m = p.matcher(url.toExternalForm());
        if (m.matches()) {
            return m.group(1);
        }
        throw new MalformedURLException("Expected femjoyhunter URL format: " +
                "femjoyhunter.com/ID - got " + url + " instead");
    }

    @Override
    public Document getFirstPage() throws IOException {
        // "url" is an instance field of the superclass
        return Http.url(url).get();
    }

    @Override
    public List<String> getURLsFromPage(Document doc) {
        List<String> result = new ArrayList<>();
        for (Element el : doc.select("ul.gallery-b > li > a")) {
            result.add(el.attr("href"));
        }
        return result;
    }

    @Override
    public void downloadURL(URL url, int index) {

        addURLToDownload(url, getPrefix(index), "", "https://a2h6m3w6.ssl.hwcdn.net/", null);
    }
}