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

public class PorncomixDotOneRipper extends AbstractHTMLRipper {

    public PorncomixDotOneRipper(URL url) throws IOException {
        super(url);
    }

    @Override
    public String getHost() {
        return "porncomix";
    }

    @Override
    public String getDomain() {
        return "porncomix.one";
    }

    @Override
    public String getGID(URL url) throws MalformedURLException {
        Pattern p = Pattern.compile("https?://www.porncomix.one/gallery/([a-zA-Z0-9_\\-]*)/?$");
        Matcher m = p.matcher(url.toExternalForm());
        if (m.matches()) {
            return m.group(1);
        }
        throw new MalformedURLException("Expected proncomix URL format: " +
                "porncomix.one/gallery/comic - got " + url + " instead");
    }

    @Override
    public Document getFirstPage() throws IOException {
        // "url" is an instance field of the superclass
        return Http.url(url).get();
    }

    @Override
    public List<String> getURLsFromPage(Document doc) {
        List<String> result = new ArrayList<>();
        // We have 2 loops here to cover all the different album types
        for (Element el : doc.select(".dgwt-jg-gallery > a")) {
            result.add(el.attr("href"));
        }
        for (Element el : doc.select(".unite-gallery > img")) {
            result.add(el.attr("data-image"));

        }
        return result;
    }

    @Override
    public void downloadURL(URL url, int index) {
        addURLToDownload(url, getPrefix(index));
    }
}