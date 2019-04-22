package me.goddragon.teaseai.utils.libraries.ripme.ripper;

import me.goddragon.teaseai.utils.TeaseLogger;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Utils;
import org.jsoup.HttpStatusException;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Thread for downloading files.
 * Includes retry logic, observer notifications, and other goodies.
 */
class DownloadFileThread extends Thread {


    private static final TeaseLogger logger = TeaseLogger.getLogger();

    private String referrer = "";
    private Map<String, String> cookies = new HashMap<>();

    private URL url;
    private File saveAs;
    private String prettySaveAs;
    private AbstractRipper observer;
    private int retries;
    private Boolean getFileExtFromMIME;

    private final int TIMEOUT;

    public DownloadFileThread(URL url, File saveAs, AbstractRipper observer, Boolean getFileExtFromMIME) {
        super();
        this.url = url;
        this.saveAs = saveAs;
        this.prettySaveAs = Utils.removeCWD(saveAs);
        this.observer = observer;
        this.retries = Utils.getConfigInteger("download.retries", 1);
        this.TIMEOUT = Utils.getConfigInteger("download.timeout", 60000);
        this.getFileExtFromMIME = getFileExtFromMIME;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }


    /**
     * Attempts to download the file. Retries as needed.
     * Notifies observers upon completion/error/warn.
     */
    public void run() {
        // First thing we make sure the file name doesn't have any illegal chars in it
        saveAs = new File(saveAs.getParentFile().getAbsolutePath() + File.separator + Utils.sanitizeSaveAs(saveAs.getName()));
        long fileSize = 0;
        int bytesTotal = 0;
        int bytesDownloaded = 0;
        if (saveAs.exists() && observer.tryResumeDownload()) {
            fileSize = saveAs.length();
        }
        try {
            observer.stopCheck();
        } catch (IOException e) {
            return;
        }
        if (saveAs.exists() && !observer.tryResumeDownload() && !getFileExtFromMIME ||
                Utils.fuzzyExists(new File(saveAs.getParent()), saveAs.getName()) && getFileExtFromMIME && !observer.tryResumeDownload()) {
            if (Utils.getConfigBoolean("file.overwrite", false)) {
                saveAs.delete();
            } else {
                return;
            }
        }
        URL urlToDownload = this.url;
        boolean redirected = false;
        int tries = 0; // Number of attempts to download
        do {
            tries += 1;
            InputStream bis = null;
            OutputStream fos = null;
            try {
                logger.log(Level.INFO, "    Downloading file: " + urlToDownload + (tries > 0 ? " Retry #" + tries : ""));

                // Setup HTTP request
                HttpURLConnection huc;
                if (this.url.toString().startsWith("https")) {
                    huc = (HttpsURLConnection) urlToDownload.openConnection();
                } else {
                    huc = (HttpURLConnection) urlToDownload.openConnection();
                }
                huc.setInstanceFollowRedirects(true);
                // It is important to set both ConnectTimeout and ReadTimeout. If you don't then ripme will wait forever
                // for the server to send data after connecting.
                huc.setConnectTimeout(TIMEOUT);
                huc.setReadTimeout(TIMEOUT);
                huc.setRequestProperty("accept", "*/*");
                if (!referrer.equals("")) {
                    huc.setRequestProperty("Referer", referrer); // Sic
                }
                huc.setRequestProperty("User-agent", AbstractRipper.USER_AGENT);
                String cookie = "";
                for (String key : cookies.keySet()) {
                    if (!cookie.equals("")) {
                        cookie += "; ";
                    }
                    cookie += key + "=" + cookies.get(key);
                }
                huc.setRequestProperty("Cookie", cookie);
                if (observer.tryResumeDownload()) {
                    if (fileSize != 0) {
                        huc.setRequestProperty("Range", "bytes=" + fileSize + "-");
                    }
                }
                huc.connect();

                int statusCode = huc.getResponseCode();
                logger.log(Level.FINE, "Status code: " + statusCode);
                if (statusCode != 206 && observer.tryResumeDownload() && saveAs.exists()) {
                    // TODO find a better way to handle servers that don't support resuming downloads then just erroring out
                    throw new IOException("server.doesnt.support.resuming.downloads");
                }
                if (statusCode / 100 == 3) { // 3xx Redirect
                    if (!redirected) {
                        // Don't increment retries on the first redirect
                        tries--;
                        redirected = true;
                    }
                    String location = huc.getHeaderField("Location");
                    urlToDownload = new URL(location);
                    // Throw exception so download can be retried
                    throw new IOException("Redirect status code " + statusCode + " - redirect to " + location);
                }
                if (statusCode / 100 == 4) { // 4xx errors

                    return; // Not retriable, drop out.
                }
                if (statusCode / 100 == 5) { // 5xx errors
                    // Throw exception so download can be retried
                    throw new IOException("retriable.status.code" + " " + statusCode);
                }
                if (huc.getContentLength() == 503 && urlToDownload.getHost().endsWith("imgur.com")) {
                    // Imgur image with 503 bytes is "404"
                    logger.log(Level.SEVERE, "[!] Imgur image is 404 (503 bytes long): " + url);
                    return;
                }

                // If the ripper is using the bytes progress bar set bytesTotal to huc.getContentLength()
                if (observer.useByteProgessBar()) {
                    bytesTotal = huc.getContentLength();
                    observer.setBytesTotal(bytesTotal);
                    logger.log(Level.FINE, "Size of file at " + this.url + " = " + bytesTotal + "b");
                }

                // Save file
                bis = new BufferedInputStream(huc.getInputStream());

                // Check if we should get the file ext from the MIME type
                if (getFileExtFromMIME) {
                    String fileExt = URLConnection.guessContentTypeFromStream(bis);
                    if (fileExt != null) {
                        fileExt = fileExt.replaceAll("image/", "");
                        saveAs = new File(saveAs.toString() + "." + fileExt);
                    } else {
                        logger.log(Level.SEVERE, "Was unable to get content type from stream");
                        // Try to get the file type from the magic number
                        byte[] magicBytes = new byte[8];
                        bis.read(magicBytes, 0, 5);
                        bis.reset();
                        fileExt = Utils.getEXTFromMagic(magicBytes);
                        if (fileExt != null) {
                            saveAs = new File(saveAs.toString() + "." + fileExt);
                        } else {
                        }
                    }
                }
                // If we're resuming a download we append data to the existing file
                if (statusCode == 206) {
                    fos = new FileOutputStream(saveAs, true);
                } else {
                    try {
                        fos = new FileOutputStream(saveAs);
                    } catch (FileNotFoundException e) {
                        // We do this because some filesystems have a max name length
                        if (e.getMessage().contains("File name too long")) {
                            logger.log(Level.SEVERE, "The filename " + saveAs.getName() + " is to long to be saved on this file system.");
                            logger.log(Level.INFO, "Shortening filename");
                            String[] saveAsSplit = saveAs.getName().split("\\.");
                            // Get the file extension so when we shorten the file name we don't cut off the file extension
                            String fileExt = saveAsSplit[saveAsSplit.length - 1];
                            // The max limit for filenames on Linux with Ext3/4 is 255 bytes
                            logger.log(Level.INFO, saveAs.getName().substring(0, 254 - fileExt.length()) + fileExt);
                            String filename = saveAs.getName().substring(0, 254 - fileExt.length()) + "." + fileExt;
                            // We can't just use the new file name as the saveAs because the file name doesn't include the
                            // users save path, so we get the user save path from the old saveAs
                            saveAs = new File(saveAs.getParentFile().getAbsolutePath() + File.separator + filename);
                            fos = new FileOutputStream(saveAs);
                        } else if (saveAs.getAbsolutePath().length() > 259 && Utils.isWindows()) {
                            // This if is for when the file path has gone above 260 chars which windows does not allow
                            fos = new FileOutputStream(Utils.shortenSaveAsWindows(saveAs.getParentFile().getPath(), saveAs.getName()));
                        }
                    }
                }
                byte[] data = new byte[1024 * 256];
                int bytesRead;
                boolean shouldSkipFileDownload = huc.getContentLength() / 10000000 >= 10 && AbstractRipper.isThisATest();
                // If this is a test rip we skip large downloads
                if (shouldSkipFileDownload) {
                    logger.log(Level.FINE, "Not downloading whole file because it is over 10mb and this is a test");
                } else {
                    while ((bytesRead = bis.read(data)) != -1) {
                        try {
                            observer.stopCheck();
                        } catch (IOException e) {
                            return;
                        }
                        fos.write(data, 0, bytesRead);
                        if (observer.useByteProgessBar()) {
                            bytesDownloaded += bytesRead;
                            observer.setBytesCompleted(bytesDownloaded);
                        }
                    }
                }
                bis.close();
                fos.close();
                break; // Download successful: break out of infinite loop
            } catch (SocketTimeoutException timeoutEx) {
                // Handle the timeout
                logger.log(Level.SEVERE, "[!] " + url.toExternalForm() + " timedout!");
                // Download failed, break out of loop
                break;
            } catch (HttpStatusException hse) {
                logger.log(Level.SEVERE, "[!] HTTP status " + hse.getStatusCode() + " while downloading from " + urlToDownload);
                if (hse.getStatusCode() == 404 && Utils.getConfigBoolean("errors.skip404", false)) {
                    return;
                }
            } catch (IOException e) {
                logger.log(Level.FINE, "IOException", e);
            } finally {
                // Close any open streams
                try {
                    if (bis != null) {
                        bis.close();
                    }
                } catch (IOException e) {
                }
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                }
            }
            if (tries > this.retries) {
                return;
            }
        } while (true);
        logger.log(Level.INFO, "[+] Saved " + url + " as " + this.prettySaveAs);
    }

}
