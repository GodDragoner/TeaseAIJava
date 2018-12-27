package me.goddragon.teaseai.utils.libraries.ripme.ripper.rippers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.goddragon.teaseai.utils.TeaseLogger;
import me.goddragon.teaseai.utils.libraries.ripme.ripper.AbstractHTMLRipper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import me.goddragon.teaseai.utils.libraries.ripme.ripper.AlbumRipper;
import me.goddragon.teaseai.utils.libraries.ripme.ripper.DownloadThreadPool;
import me.goddragon.teaseai.utils.libraries.ripme.ui.MainWindow;
import me.goddragon.teaseai.utils.libraries.ripme.ui.RipStatusMessage.STATUS;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Http;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Utils;

public class PornhubRipper extends AbstractHTMLRipper {
    // All sleep times are in milliseconds
    private static final int IMAGE_SLEEP_TIME = 1000;

    private static final String DOMAIN = "pornhub.com", HOST = "Pornhub";
    
    private List<Callable<String>> workers;

    // Thread pool for finding direct image links from "image" pages (html)
    private DownloadThreadPool pornhubThreadPool = new DownloadThreadPool("pornhub");

    public PornhubRipper(URL url) throws IOException {
        super(url);
        workers = new ArrayList<Callable<String>>();
    }

    @Override
    public String getHost() {
        return HOST;
    }

    @Override
    protected String getDomain() {
        return DOMAIN;
    }

    @Override
    protected Document getFirstPage() throws IOException {
        return Http.url(url).referrer(url).get();
    }

    @Override
    public Document getNextPage(Document page) throws IOException {
        Elements nextPageLink = page.select("li.page_next > a");
        if (nextPageLink.isEmpty()){
            throw new IOException("No more pages");
        } else {
            URL nextURL = new URL(this.url, nextPageLink.first().attr("href"));
            return Http.url(nextURL).get();
        }
    }

    @Override
    protected List<String> getURLsFromPage(Document page) {
        List<String> pageURLs = new ArrayList<>();
        // Find thumbnails
        Elements thumbs = page.select(".photoBlockBox li");
        // Iterate over thumbnail images on page
        for (Element thumb : thumbs) {
            String imagePage = thumb.select(".photoAlbumListBlock > a")
                    .first().attr("href");
            String fullURL = "https://pornhub.com" + imagePage;
            pageURLs.add(fullURL);
        }
        return pageURLs;
    }
    
    @Override
    public void rip() throws IOException {
        List<Callable<String>> workers = new ArrayList<Callable<String>>();
        
        int index = 0;
        int textindex = 0;
        LOGGER.info("Retrieving " + this.url);
        sendUpdate(STATUS.LOADING_RESOURCE, this.url.toExternalForm());
        Document doc = getFirstPage();

        if (hasQueueSupport() && pageContainsAlbums(this.url)) {
            List<String> urls = getAlbumsToQueue(doc);
            for (String url : urls) {
                MainWindow.addUrlToQueue(url);
            }

            // We set doc to null here so the while loop below this doesn't fire
            doc = null;
        }

        while (doc != null) {
            if (alreadyDownloadedUrls >= Utils.getConfigInteger("history.end_rip_after_already_seen", 1000000000) && !isThisATest()) {
                sendUpdate(STATUS.DOWNLOAD_COMPLETE_HISTORY, "Already seen the last " + alreadyDownloadedUrls + " images ending rip");
                break;
            }
            List<String> imageURLs = getURLsFromPage(doc);
            // If hasASAPRipping() returns true then the ripper will handle downloading the files
            // if not it's done in the following block of code
            if (!hasASAPRipping()) {
                // Remove all but 1 image
                if (isThisATest()) {
                    while (imageURLs.size() > 1) {
                        imageURLs.remove(1);
                    }
                }

                if (imageURLs.isEmpty()) {
                    throw new IOException("No images found at " + doc.location());
                }

                for (String imageURL : imageURLs) {
                    index += 1;
                    LOGGER.debug("Found image url #" + index + ": " + imageURL);
                    workers.add(downloadWorker(new URL(imageURL), index));
                    if (isStopped()) {
                        break;
                    }
                }
            }
            if (hasDescriptionSupport() && Utils.getConfigBoolean("descriptions.save", false)) {
                LOGGER.debug("Fetching description(s) from " + doc.location());
                List<String> textURLs = getDescriptionsFromPage(doc);
                if (!textURLs.isEmpty()) {
                    LOGGER.debug("Found description link(s) from " + doc.location());
                    for (String textURL : textURLs) {
                        if (isStopped()) {
                            break;
                        }
                        textindex += 1;
                        LOGGER.debug("Getting description from " + textURL);
                        String[] tempDesc = getDescription(textURL,doc);
                        if (tempDesc != null) {
                            if (Utils.getConfigBoolean("file.overwrite", false) || !(new File(
                                    workingDir.getCanonicalPath()
                                            + ""
                                            + File.separator
                                            + getPrefix(index)
                                            + (tempDesc.length > 1 ? tempDesc[1] : fileNameFromURL(new URL(textURL)))
                                            + ".txt").exists())) {
                                LOGGER.debug("Got description from " + textURL);
                                saveText(new URL(textURL), "", tempDesc[0], textindex, (tempDesc.length > 1 ? tempDesc[1] : fileNameFromURL(new URL(textURL))));
                                sleep(descSleepTime());
                            } else {
                                LOGGER.debug("Description from " + textURL + " already exists.");
                            }
                        }

                    }
                }
            }

            if (isStopped() || isThisATest()) {
                break;
            }

            try {
                sendUpdate(STATUS.LOADING_RESOURCE, "next page");
                doc = getNextPage(doc);
            } catch (IOException e) {
                LOGGER.info("Can't get next page: " + e.getMessage());
                break;
            }
        }
        TeaseLogger.getLogger().log(Level.INFO, "debug 12345 " + workers.size());
        ExecutorService executor = Executors.newWorkStealingPool();
        try
        {
            for (int i = 0; i < workers.size(); i++)
            {
                executor.submit(workers.get(i));
                Thread.sleep(1000);
            }
            //executor.invokeAll(workers);
            //TeaseLogger.getLogger().log(Level.INFO, "debug 2da " + executor.);
            executor.shutdown();
            executor.awaitTermination(10000000, TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*Override
    protected void downloadURL(URL url, int index) {
                /*try
                {
                    Document doc = Http.url(url)
                            .referrer(url)
                            .get();
      
      // Find image
      Elements images = doc.select("#photoImageSection img");
      Element image = images.first();
      String imgsrc = image.attr("src");
      LOGGER.info("Found URL " + imgsrc + " via " + images.get(0));
      
      // Provide prefix and let the AbstractRipper "guess" the filename
      String prefix = "";
      if (Utils.getConfigBoolean("download.save_order", true)) {
       prefix = String.format("%03d_", index);
      }
      
      URL imgurl = new URL(url, imgsrc);
      addURLToDownload(imgurl, prefix);
                }
                catch (MalformedURLException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        */
        //PornhubImageThread t = new PornhubImageThread(url, index, this.workingDir);
        //pornhubThreadPool.addThread(t);
        /*try {
            Thread.sleep(IMAGE_SLEEP_TIME);
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted while waiting to load next image", e);
        }
    }*/
    
    private Callable<String> downloadWorker(URL url, int index)
    {
        return new PornhubImageThread(url, index, this.workingDir);
    }

    public URL sanitizeURL(URL url) throws MalformedURLException {
        // always start on the first page of an album
        // (strip the options after the '?')
        String u = url.toExternalForm();
        if (u.contains("?")) {
            u = u.substring(0, u.indexOf("?"));
            return new URL(u);
        } else {
            return url;
        }
    }

    @Override
    public String getGID(URL url) throws MalformedURLException {
        Pattern p;
        Matcher m;

        p = Pattern.compile("^.*pornhub\\.com/album/([0-9]+).*$");
        m = p.matcher(url.toExternalForm());
        if (m.matches()) {
            return m.group(1);
        }

        throw new MalformedURLException(
                "Expected pornhub.com album format: "
                        + "http://www.pornhub.com/album/####"
                        + " Got: " + url);
    }

    @Override
    public DownloadThreadPool getThreadPool(){
        return pornhubThreadPool;
    }

    public boolean canRip(URL url) {
        return url.getHost().endsWith(DOMAIN) && url.getPath().startsWith("/album");
    }

    /**
     * Helper class to find and download images found on "image" pages
     *
     * Handles case when site has IP-banned the user.
     */
    private class PornhubImageThread implements Callable<String> {
        private URL url;
        private int index;

        PornhubImageThread(URL url, int index, File workingDir) {
            super();
            this.url = url;
            this.index = index;
        }

        private void fetchImage() {
            try {
                TeaseLogger.getLogger().log(Level.INFO, "Index " + this.index);
                Document doc = Http.url(this.url)
                                   .referrer(this.url)
                                   .get();
                while (doc.html().substring(0, 400).contains("text/javascript"))
                {
                    doc = Http.url(this.url)
                            .referrer(this.url)
                            .get();
                }
                //TeaseLogger.getLogger().log(Level.INFO, "doc " + index + " " + doc);
                // Find image
                Elements images = doc.select("#photoImageSection img");
                Element image = images.first();
                if (image == null)
                {
                    TeaseLogger.getLogger().log(Level.INFO, "image null doc " + doc.html().substring(0, 400));
                }
                else
                {
                    TeaseLogger.getLogger().log(Level.INFO, "image NOTnull doc " + doc.html().substring(0, 400));
                }
                String imgsrc = image.attr("src");
                LOGGER.info("Found URL " + imgsrc + " via " + images.get(0));

                // Provide prefix and let the AbstractRipper "guess" the filename
                String prefix = "";
                if (Utils.getConfigBoolean("download.save_order", true)) {
                    prefix = String.format("%03d_", index);
                }

                URL imgurl = new URL(url, imgsrc);
                TeaseLogger.getLogger().log(Level.INFO, "Parsed url " + index + " " + imgurl.toExternalForm());
                addURLToDownload(imgurl, prefix);

            } catch (Exception e) {
                e.printStackTrace();
                TeaseLogger.getLogger().log(Level.SEVERE, "[!] Exception while loading/parsing " + this.url + " " + e);
            }
        }

        @Override
        public String call() throws Exception
        {
            fetchImage();
            return index + "";
        }
    }

    @Override
    protected void downloadURL(URL url, int index)
    {
        // TODO Auto-generated method stub
        workers.add(downloadWorker(url, index));
    }
}