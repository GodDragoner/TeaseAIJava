package me.goddragon.teaseai.utils.libraries.ripme.ripper;

import me.goddragon.teaseai.utils.libraries.ripme.utils.Utils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;

/**
 * Simplified ripper, designed for ripping from sites by parsing JSON.
 */
public abstract class AbstractJSONRipper extends AlbumRipper {

    protected AbstractJSONRipper(URL url) throws IOException {
        super(url);
    }

    protected abstract String getDomain();

    @Override
    public abstract String getHost();

    protected abstract JSONObject getFirstPage() throws IOException;

    protected JSONObject getNextPage(JSONObject doc) throws IOException {
        throw new IOException("getNextPage not implemented");
    }

    protected abstract List<String> getURLsFromJSON(JSONObject json);

    protected abstract void downloadURL(URL url, int index);

    private DownloadThreadPool getThreadPool() {
        return null;
    }

    protected boolean keepSortOrder() {
        return true;
    }

    @Override
    public boolean canRip(URL url) {
        return url.getHost().endsWith(getDomain());
    }

    @Override
    public URL sanitizeURL(URL url) throws MalformedURLException {
        return url;
    }

    @Override
    public void rip() throws IOException {
        int index = 0;
        LOGGER.log(Level.INFO, "Retrieving " + this.url);
        JSONObject json = getFirstPage();

        while (json != null) {
            List<String> imageURLs = getURLsFromJSON(json);

            if (alreadyDownloadedUrls >= Utils.getConfigInteger("history.end_rip_after_already_seen", 1000000000) && !isThisATest()) {
                break;
            }

            // Remove all but 1 image
            if (isThisATest()) {
                while (imageURLs.size() > 1) {
                    imageURLs.remove(1);
                }
            }

            if (imageURLs.isEmpty() && !hasASAPRipping()) {
                throw new IOException("No images found at " + this.url);
            }

            for (String imageURL : imageURLs) {
                if (isStopped()) {
                    break;
                }

                index += 1;
                LOGGER.log(Level.FINE, "Found image url #" + index + ": " + imageURL);
                downloadURL(new URL(imageURL), index);
            }

            if (isStopped() || isThisATest()) {
                break;
            }

            try {
                json = getNextPage(json);
            } catch (IOException e) {
                LOGGER.log(Level.INFO, "Can't get next page: " + e.getMessage());
                break;
            }
        }

        // If they're using a thread pool, wait for it.
        if (getThreadPool() != null) {
            LOGGER.log(Level.FINE, "Waiting for threadpool " + getThreadPool().getClass().getName());
            getThreadPool().waitForThreads();
        }
        waitForThreads();
    }

    protected String getPrefix(int index) {
        String prefix = "";
        if (keepSortOrder() && Utils.getConfigBoolean("download.save_order", true)) {
            prefix = String.format("%03d_", index);
        }
        return prefix;
    }
}
