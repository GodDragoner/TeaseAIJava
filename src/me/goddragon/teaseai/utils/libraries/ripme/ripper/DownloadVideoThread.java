package me.goddragon.teaseai.utils.libraries.ripme.ripper;

import me.goddragon.teaseai.utils.TeaseLogger;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

/**
 * Thread for downloading files.
 * Includes retry logic, observer notifications, and other goodies.
 */
class DownloadVideoThread extends Thread {

    private static final TeaseLogger logger = TeaseLogger.getLogger();

    private URL url;
    private File saveAs;
    private String prettySaveAs;
    private AbstractRipper observer;
    private int retries;

    public DownloadVideoThread(URL url, File saveAs, AbstractRipper observer) {
        super();
        this.url = url;
        this.saveAs = saveAs;
        this.prettySaveAs = Utils.removeCWD(saveAs);
        this.observer = observer;
        this.retries = Utils.getConfigInteger("download.retries", 1);
    }

    /**
     * Attempts to download the file. Retries as needed.
     * Notifies observers upon completion/error/warn.
     */
    public void run() {
        try {
            observer.stopCheck();
        } catch (IOException e) {
            return;
        }
        if (saveAs.exists()) {
            if (Utils.getConfigBoolean("file.overwrite", false)) {
                logger.log(Level.INFO, "[!] Deleting existing file" + prettySaveAs);
                saveAs.delete();
            } else {
                logger.log(Level.INFO, "[!] Skipping " + url + " -- file already exists: " + prettySaveAs);
                return;
            }
        }

        int bytesTotal, bytesDownloaded = 0;
        try {
            bytesTotal = getTotalBytes(this.url);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to get file size at " + this.url, e);
            return;
        }
        observer.setBytesTotal(bytesTotal);
        logger.log(Level.FINE, "Size of file at " + this.url + " = " + bytesTotal + "b");

        int tries = 0; // Number of attempts to download
        do {
            InputStream bis = null; OutputStream fos = null;
            byte[] data = new byte[1024 * 256];
            int bytesRead;
            try {
                logger.log(Level.INFO, "    Downloading file: " + url + (tries > 0 ? " Retry #" + tries : ""));

                // Setup HTTP request
                HttpURLConnection huc;
                if (this.url.toString().startsWith("https")) {
                    huc = (HttpsURLConnection) this.url.openConnection();
                }
                else {
                    huc = (HttpURLConnection) this.url.openConnection();
                }
                huc.setInstanceFollowRedirects(true);
                huc.setConnectTimeout(0); // Never timeout
                huc.setRequestProperty("accept",  "*/*");
                huc.setRequestProperty("Referer", this.url.toExternalForm()); // Sic
                huc.setRequestProperty("User-agent", AbstractRipper.USER_AGENT);
                tries += 1;
                logger.log(Level.FINE, "Request properties: " + huc.getRequestProperties().toString());
                huc.connect();
                // Check status code
                bis = new BufferedInputStream(huc.getInputStream());
                fos = new FileOutputStream(saveAs);
                while ( (bytesRead = bis.read(data)) != -1) {
                    try {
                        observer.stopCheck();
                    } catch (IOException e) {

                        return;
                    }
                    fos.write(data, 0, bytesRead);
                    bytesDownloaded += bytesRead;
                    observer.setBytesCompleted(bytesDownloaded);
                }
                bis.close();
                fos.close();
                break; // Download successful: break out of infinite loop
            } catch (IOException e) {
                logger.log(Level.SEVERE, "[!] Exception while downloading file: " + url + " - " + e.getMessage(), e);
            } finally {
                // Close any open streams
                try {
                    if (bis != null) { bis.close(); }
                } catch (IOException e) { }
                try {
                    if (fos != null) { fos.close(); }
                } catch (IOException e) { }
            }
            if (tries > this.retries) {
                logger.log(Level.SEVERE, "[!] Exceeded maximum retries (" + this.retries + ") for URL " + url);
                return;
            }
        } while (true);
        logger.log(Level.INFO, "[+] Saved " + url + " as " + this.prettySaveAs);
    }

    /**
     * @param url
     *      Target URL
     * @return 
     *      Returns connection length
     */
    private int getTotalBytes(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("HEAD");
        conn.setRequestProperty("accept",  "*/*");
        conn.setRequestProperty("Referer", this.url.toExternalForm()); // Sic
        conn.setRequestProperty("User-agent", AbstractRipper.USER_AGENT);
        return conn.getContentLength();
    }

}