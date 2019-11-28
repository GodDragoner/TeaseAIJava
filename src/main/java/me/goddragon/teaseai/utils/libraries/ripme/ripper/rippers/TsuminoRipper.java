package me.goddragon.teaseai.utils.libraries.ripme.ripper.rippers;

import me.goddragon.teaseai.utils.libraries.ripme.ripper.AbstractHTMLRipper;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Http;
import me.goddragon.teaseai.utils.libraries.ripme.utils.RipUtils;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TsuminoRipper extends AbstractHTMLRipper {
    private Map<String, String> cookies = new HashMap<>();

    public TsuminoRipper(URL url) throws IOException {
        super(url);
    }

    public List<String> getTags(Document doc) {
        List<String> tags = new ArrayList<>();
        LOGGER.log(Level.INFO, "Getting tags");
        for (Element tag : doc.select("div#Tag > a")) {
            LOGGER.log(Level.INFO, "Found tag " + tag.text());
            tags.add(tag.text().toLowerCase());
        }
        return tags;
    }

    private JSONArray getPageUrls() {
        String postURL = "http://www.tsumino.com/Read/Load";
        try {
            // This sessionId will expire and need to be replaced
            cookies.put("ASP.NET_SessionId", "c4rbzccf0dvy3e0cloolmlkq");
            Document doc = Jsoup.connect(postURL).data("q", getAlbumID()).userAgent(USER_AGENT).cookies(cookies).referrer("http://www.tsumino.com/Read/View/" + getAlbumID()).get();
            String jsonInfo = doc.html().replaceAll("<html>", "").replaceAll("<head></head>", "").replaceAll("<body>", "").replaceAll("</body>", "")
                    .replaceAll("</html>", "").replaceAll("\n", "");
            JSONObject json = new JSONObject(jsonInfo);
            return json.getJSONArray("reader_page_urls");
        } catch (IOException e) {
            LOGGER.log(Level.INFO, e.getMessage());
            return null;
        }
    }

    @Override
    public String getHost() {
        return "tsumino";
    }

    @Override
    public String getDomain() {
        return "tsumino.com";
    }

    @Override
    public String getGID(URL url) throws MalformedURLException {
        Pattern p = Pattern.compile("https?://www.tsumino.com/Book/Info/([0-9]+)/([a-zA-Z0-9_-]*)/?");
        Matcher m = p.matcher(url.toExternalForm());
        if (m.matches()) {
            return m.group(1) + "_" + m.group(2);
        }
        p = Pattern.compile("https?://www.tsumino.com/Book/Info/([0-9]+)/?");
        m = p.matcher(url.toExternalForm());
        if (m.matches()) {
            return m.group(1);
        }
        throw new MalformedURLException("Expected tsumino URL format: " +
                "tsumino.com/Book/Info/ID/TITLE - got " + url + " instead");
    }

    private String getAlbumID() {
        Pattern p = Pattern.compile("https?://www.tsumino.com/Book/Info/([0-9]+)/\\S*");
        Matcher m = p.matcher(url.toExternalForm());
        if (m.matches()) {
            return m.group(1);
        }
        return null;
    }

    @Override
    public Document getFirstPage() throws IOException {
        Connection.Response resp = Http.url(url).response();
        cookies.putAll(resp.cookies());
        Document doc = resp.parse();
        String blacklistedTag = RipUtils.checkTags(Utils.getConfigStringArray("tsumino.blacklist.tags"), getTags(doc));
        if (blacklistedTag != null) {
            return null;
        }
        return doc;
    }

    @Override
    public List<String> getURLsFromPage(Document doc) {
        JSONArray imageIds = getPageUrls();
        List<String> result = new ArrayList<>();
        for (int i = 0; i < imageIds.length(); i++) {
            result.add("http://www.tsumino.com/Image/Object?name=" + URLEncoder.encode(imageIds.getString(i)));
        }

        return result;
    }

    @Override
    public void downloadURL(URL url, int index) {
        sleep(1000);
        /*
        There is no way to tell if an image returned from tsumino.com is a png to jpg. The content-type header is always
        "image/jpeg" even when the image is a png. The file ext is not included in the url.
         */
        addURLToDownload(url, getPrefix(index), "", null, null, null, null, true);
    }
}
