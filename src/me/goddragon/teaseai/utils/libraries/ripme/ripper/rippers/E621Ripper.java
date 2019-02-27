package me.goddragon.teaseai.utils.libraries.ripme.ripper.rippers;

import me.goddragon.teaseai.utils.TeaseLogger;
import me.goddragon.teaseai.utils.libraries.ripme.ripper.AbstractHTMLRipper;
import me.goddragon.teaseai.utils.libraries.ripme.ripper.DownloadThreadPool;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Http;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Utils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class E621Ripper extends AbstractHTMLRipper{
	private static final TeaseLogger logger = TeaseLogger.getLogger();

	private static Pattern gidPattern=null;
	private static Pattern gidPattern2=null;
	private static Pattern gidPatternPool=null;

	private DownloadThreadPool e621ThreadPool=new DownloadThreadPool("e621");

	public E621Ripper(URL url) throws IOException {
		super(url);
	}

	@Override
	public DownloadThreadPool getThreadPool() {
		return e621ThreadPool;
	}

	@Override
	public String getDomain() {
		return "e621.net";
	}

	@Override
	public String getHost() {
		return "e621";
	}

	@Override
	public Document getFirstPage() throws IOException {
		if(url.getPath().startsWith("/pool/show/"))
			return Http.url("https://e621.net/pool/show/"+getTerm(url)).get();
		else
			return Http.url("https://e621.net/post/index/1/"+getTerm(url)).get();
	}

	private String getFullSizedImage(String url) {
	    try {
                Document page = Http.url("https://e621.net" + url).get();
                Elements video = page.select("video > source");
                Elements flash = page.select("embed");
                Elements image = page.select("a#highres");
                if (video.size() > 0) {
                    return video.attr("src");
                } else if (flash.size() > 0) {
                    return flash.attr("src");
                } else if (image.size() > 0) {
                    return image.attr("href");
                } else {
                    throw new IOException();
                }
        } catch (IOException e) {
	        logger.log(Level.SEVERE, "Unable to get full sized image from " + url);
	        return null;
        }
    }

	@Override
	public List<String> getURLsFromPage(Document page) {
		Elements elements = page.select("div > span.thumb > a");
		List<String> res = new ArrayList<>();

		for(Element e:elements) {
		    if (!e.attr("href").isEmpty()) {
                String fullSizedImage = getFullSizedImage(e.attr("href"));
                if (fullSizedImage != null && !fullSizedImage.equals("")) {
                    res.add(getFullSizedImage(e.attr("href")));
                }
            }
		}

		return res;
	}

	@Override
	public Document getNextPage(Document page) throws IOException {
        if (page.select("a.next_page") != null) {
            return Http.url("https://e621.net" + page.select("a.next_page").attr("href")).get();
        } else {
            throw new IOException("No more pages");
        }
    }

	@Override
	public void downloadURL(final URL url, int index) {
        addURLToDownload(url, getPrefix(index));
	}

	private String getTerm(URL url) throws MalformedURLException{
		if(gidPattern==null)
			gidPattern=Pattern.compile("^https?://(www\\.)?e621\\.net/post/index/[^/]+/([a-zA-Z0-9$_.+!*'():,%\\-]+)(/.*)?(#.*)?$");
		if(gidPatternPool==null)
			gidPatternPool=Pattern.compile("^https?://(www\\.)?e621\\.net/pool/show/([a-zA-Z0-9$_.+!*'(),%:\\-]+)(\\?.*)?(/.*)?(#.*)?$");

		Matcher m = gidPattern.matcher(url.toExternalForm());
		if(m.matches()) {
            LOGGER.log(Level.INFO, m.group(2));
            return m.group(2);
        }

		m = gidPatternPool.matcher(url.toExternalForm());
		if(m.matches()) {
            return m.group(2);
        }

		throw new MalformedURLException("Expected e621.net URL format: e621.net/post/index/1/searchterm - got "+url+" instead");
	}

	@Override
	public String getGID(URL url) throws MalformedURLException {

			String prefix="";
			if (url.getPath().startsWith("/pool/show/")) {
                prefix = "pool_";
            }

			return Utils.filesystemSafe(prefix+getTerm(url));

	}

	@Override
	public URL sanitizeURL(URL url) throws MalformedURLException {
		if(gidPattern2==null)
			gidPattern2=Pattern.compile("^https?://(www\\.)?e621\\.net/post/search\\?tags=([a-zA-Z0-9$_.+!*'():,%-]+)(/.*)?(#.*)?$");

		Matcher m = gidPattern2.matcher(url.toExternalForm());
		if(m.matches())
			return new URL("https://e621.net/post/index/1/"+m.group(2).replace("+","%20"));

		return url;
	}

}
