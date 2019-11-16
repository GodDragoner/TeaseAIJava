package me.goddragon.teaseai.utils.libraries.ripme.ripper.rippers;

import me.goddragon.teaseai.utils.libraries.ripme.ripper.AbstractHTMLRipper;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Http;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TeenplanetRipper extends AbstractHTMLRipper {

    private static final String DOMAIN = "teenplanet.org",
            HOST = "teenplanet";

    public TeenplanetRipper(URL url) throws IOException {
        super(url);
    }

    @Override
    protected String getDomain() {
        return DOMAIN;
    }

    @Override
    public String getHost() {
        return HOST;
    }

    @Override
    protected Document getFirstPage() throws IOException {
        return Http.url(url).get();
    }

    @Override
    protected List<String> getURLsFromPage(Document page) {
        List<String> imageURLs = new ArrayList<>();
        for (Element thumb : page.select("#galleryImages > a > img")) {
            if (!thumb.hasAttr("src")) {
                continue;
            }
            String imageURL = thumb.attr("src");
            imageURL = imageURL.replace(
                    "/thumbs/",
                    "/");
            imageURLs.add(imageURL);
        }
        System.out.println("Found" + imageURLs.size() + " image urls");
        return imageURLs;
    }

    @Override
    protected void downloadURL(URL url, int index) {
        String prefix = "";
        if (Utils.getConfigBoolean("download.save_order", true)) {
            prefix = String.format("%03d_", index);
        }
        addURLToDownload(url, prefix);
    }

    @Override
    public String getGID(URL url) throws MalformedURLException {
        Pattern p;
        Matcher m;

        p = Pattern.compile("^.*teenplanet.org/galleries/([a-zA-Z0-9\\-]+).html$");
        m = p.matcher(url.toExternalForm());
        if (m.matches()) {
            return m.group(1);
        }

        throw new MalformedURLException(
                "Expected teenplanet.org gallery format: "
                        + "teenplanet.org/galleries/....html"
                        + " Got: " + url);
    }
}