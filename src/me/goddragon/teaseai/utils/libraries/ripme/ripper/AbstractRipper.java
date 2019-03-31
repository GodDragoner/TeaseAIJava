package me.goddragon.teaseai.utils.libraries.ripme.ripper;

import me.goddragon.teaseai.utils.TeaseLogger;
import me.goddragon.teaseai.utils.libraries.ripme.App;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Utils;
import org.jsoup.HttpStatusException;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.logging.Level;

public abstract class AbstractRipper
                extends Observable
                implements RipperInterface, Runnable {

    protected static final TeaseLogger LOGGER = TeaseLogger.getLogger();
    private final String URLHistoryFile = Utils.getURLHistoryFile();

    public static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36";

    protected URL url;
    protected File workingDir;
    DownloadThreadPool threadPool;

    private boolean completed = true;

    public abstract void rip() throws IOException;
    public abstract String getHost();
    public abstract String getGID(URL url) throws MalformedURLException;
    public boolean hasASAPRipping() { return false; }
    // Everytime addUrlToDownload skips a already downloaded url this increases by 1
    public int alreadyDownloadedUrls = 0;
    private boolean shouldStop = false;
    private static boolean thisIsATest = false;

    public void stop() {
        shouldStop = true;
    }
    public boolean isStopped() {
        return shouldStop;
    }
    protected void stopCheck() throws IOException {
        if (shouldStop) {
            throw new IOException("Ripping interrupted");
        }
    }


    /**
     * Adds a URL to the url history file
     * @param downloadedURL URL to check if downloaded
     */
    private void writeDownloadedURL(String downloadedURL) throws IOException {
        // If "save urls only" is checked don't write to the url history file
        if (Utils.getConfigBoolean("urls_only.save", false)) {
            return;
        }
        downloadedURL = normalizeUrl(downloadedURL);
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            File file = new File(URLHistoryFile);
            if (!new File(Utils.getConfigDir()).exists()) {
                LOGGER.log(Level.SEVERE, "Config dir doesn't exist");
                LOGGER.log(Level.INFO, "Making config dir");
                boolean couldMakeDir = new File(Utils.getConfigDir()).mkdirs();
                if (!couldMakeDir) {
                    LOGGER.log(Level.SEVERE, "Couldn't make config dir");
                    return;
                }
            }
            // if file doesnt exists, then create it
            if (!file.exists()) {
                boolean couldMakeDir = file.createNewFile();
                if (!couldMakeDir) {
                    LOGGER.log(Level.SEVERE, "Couldn't url history file");
                    return;
                }
            }
            if (!file.canWrite()) {
                LOGGER.log(Level.SEVERE, "Can't write to url history file: " + URLHistoryFile);
                return;
            }
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            bw.write(downloadedURL);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * Normalize a URL
     * @param url URL to check if downloaded
     */
    public String normalizeUrl(String url) {
        return url;
    }
    
    /**
     * Checks to see if Ripme has already downloaded a URL
     * @param url URL to check if downloaded
     * @return 
     *      Returns true if previously downloaded.
     *      Returns false if not yet downloaded.
     */
    private boolean hasDownloadedURL(String url) {
        File file = new File(URLHistoryFile);
        url = normalizeUrl(url);

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                final String lineFromFile = scanner.nextLine();
                if (lineFromFile.equals(url)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            return false;
        }

        return false;
    }


    /**
     * Ensures inheriting ripper can rip this URL, raises exception if not.
     * Otherwise initializes working directory and thread pool.
     *
     * @param url
     *      URL to rip.
     * @throws IOException
     *      If anything goes wrong.
     */
    public AbstractRipper(URL url) throws IOException {
        if (!canRip(url)) {
            throw new MalformedURLException("Unable to rip url: " + url);
        }
        this.url = sanitizeURL(url);
    }

    /**
     * Sets ripper's:
     *      Working directory
     *      Logger (for debugging)
     *      FileAppender
     *      Threadpool
     * @throws IOException 
     *      Always be prepared.
     */
    public void setup() throws IOException {
        setWorkingDir(this.url);

        this.threadPool = new DownloadThreadPool();
        
        if (Utils.getConfigBoolean("media_url", false))
        {
            String urlFile = this.workingDir + File.separator + getAlbumTitle(this.url) + ".txt";
            try (FileWriter fw = new FileWriter(urlFile, true)) {
                fw.write(this.url.toExternalForm());
                fw.write("\n");
                if (Utils.getConfigBoolean("use_for_tease", false))
                {
                    fw.write("true");
                    fw.write("\n");
                }
                else
                {
                    fw.write("false");
                    fw.write("\n");
                }
                Utils.setConfigString("url_file", this.workingDir + File.separator + getAlbumTitle(this.url) + ".txt");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error while writing to " + urlFile, e);
            }
        }
    }

    /**
     * Queues image to be downloaded and saved.
     * @param url
     *      URL of the file
     * @param saveAs
     *      Path of the local file to save the content to.
     * @return True on success, false on failure.
     */
    public abstract boolean addURLToDownload(URL url, File saveAs);

    /**
     * Queues image to be downloaded and saved.
     * @param url
     *      URL of the file
     * @param saveAs
     *      Path of the local file to save the content to.
     * @param referrer
     *      The HTTP referrer to use while downloading this file.
     * @param cookies
     *      The cookies to send to the server while downloading this file.
     * @return
     *      True if downloaded successfully
     *      False if failed to download
     */
    protected abstract boolean addURLToDownload(URL url, File saveAs, String referrer, Map<String, String> cookies,
                                                Boolean getFileExtFromMIME);

    /**
     * Queues image to be downloaded and saved.
     * @param url
     *      URL of the file
     * @param prefix
     *      Prefix for the downloaded file
     * @param subdirectory
     *      Path to get to desired directory from working directory
     * @param referrer
     *      The HTTP referrer to use while downloading this file.
     * @param cookies
     *      The cookies to send to the server while downloading this file.
     * @param fileName
     *      The name that file will be written to
     * @return 
     *      True if downloaded successfully
     *      False if failed to download
     */
    protected boolean addURLToDownload(URL url, String prefix, String subdirectory, String referrer, Map<String, String> cookies, String fileName, String extension, Boolean getFileExtFromMIME) {
        // Don't re-add the url if it was downloaded in a previous rip
        if (Utils.getConfigBoolean("remember.url_history", true) && !isThisATest()) {
            if (hasDownloadedURL(url.toExternalForm())) {
                //sendUpdate(STATUS.DOWNLOAD_WARN, "Already downloaded " + url.toExternalForm());
                alreadyDownloadedUrls += 1;
                return false;
            }
        }
        try {
            stopCheck();
        } catch (IOException e) {
            LOGGER.log(Level.FINE, "Ripper has been stopped");
            return false;
        }
        LOGGER.log(Level.FINE, "url: " + url + ", prefix: " + prefix + ", subdirectory" + subdirectory + ", referrer: " + referrer + ", cookies: " + cookies + ", fileName: " + fileName);
        String saveAs = getFileName(url, fileName, extension);
        File saveFileAs;
        try {
            if (!subdirectory.equals("")) {
                subdirectory = Utils.filesystemSafe(subdirectory);
                subdirectory = File.separator + subdirectory;
            }
            prefix = Utils.filesystemSanitized(prefix);
            String topFolderName = workingDir.getCanonicalPath();
            if (App.stringToAppendToFoldername != null) {
                topFolderName = topFolderName + App.stringToAppendToFoldername;
            }
            saveFileAs = new File(
                    topFolderName
                    + subdirectory
                    + File.separator
                    + prefix
                    + saveAs);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "[!] Error creating save file path for URL '" + url + "':", e);
            return false;
        }
        LOGGER.log(Level.FINE, "Downloading " + url + " to " + saveFileAs);
        if (!saveFileAs.getParentFile().exists()) {
            LOGGER.log(Level.INFO, "[+] Creating directory: " + Utils.removeCWD(saveFileAs.getParent()));
            saveFileAs.getParentFile().mkdirs();
        }

        if (Utils.getConfigBoolean("remember.url_history", true) && !isThisATest()) {
            try {
                writeDownloadedURL(url.toExternalForm() + "\n");
            } catch (IOException e) {
                LOGGER.log(Level.FINE, "Unable to write URL history file");
            }
        }

        return addURLToDownload(url, saveFileAs, referrer, cookies, getFileExtFromMIME);
    }

    protected boolean addURLToDownload(URL url, String prefix, String subdirectory, String referrer, Map<String,String> cookies, String fileName, String extension) {
        return addURLToDownload(url, prefix, subdirectory, referrer, cookies, fileName, extension, false);
    }

    protected boolean addURLToDownload(URL url, String prefix, String subdirectory, String referrer, Map<String, String> cookies, String fileName) {
        return addURLToDownload(url, prefix, subdirectory, referrer, cookies, fileName, null);
    }

    /**
     * Queues file to be downloaded and saved. With options.
     * @param url
     *      URL to download.
     * @param prefix
     *      Prefix to prepend to the saved filename.
     * @param subdirectory
     *      Sub-directory of the working directory to save the images to.
     * @return True on success, flase on failure.
     */
    protected boolean addURLToDownload(URL url, String prefix, String subdirectory) {
        return addURLToDownload(url, prefix, subdirectory, null, null, null);
    }

    protected boolean addURLToDownload(URL url, String prefix, String subdirectory, String referrer, Map<String, String> cookies) {
        return addURLToDownload(url, prefix, subdirectory, referrer, cookies, null);
    }

    /**
     * Queues image to be downloaded and saved.
     * Uses filename from URL (and 'prefix') to decide filename.
     * @param url
     *      URL to download
     * @param prefix
     *      Text to append to saved filename.
     * @return True on success, flase on failure.
     */
    protected boolean addURLToDownload(URL url, String prefix) {
        // Use empty subdirectory
        return addURLToDownload(url, prefix, "");
    }

    public static String getFileName(URL url, String fileName, String extension) {
        String saveAs;
        if (fileName != null) {
            saveAs = fileName;
        } else {
            saveAs = url.toExternalForm();
            saveAs = saveAs.substring(saveAs.lastIndexOf('/')+1);
        }
        if (extension == null) {
            // Get the extension of the file
            String[] lastBitOfURL = url.toExternalForm().split("/");

            String[] lastBit = lastBitOfURL[lastBitOfURL.length - 1].split(".");
            if (lastBit.length != 0) {
                extension = lastBit[lastBit.length - 1];
                saveAs = saveAs + "." + extension;
            }
        }

        if (saveAs.indexOf('?') >= 0) { saveAs = saveAs.substring(0, saveAs.indexOf('?')); }
        if (saveAs.indexOf('#') >= 0) { saveAs = saveAs.substring(0, saveAs.indexOf('#')); }
        if (saveAs.indexOf('&') >= 0) { saveAs = saveAs.substring(0, saveAs.indexOf('&')); }
        if (saveAs.indexOf(':') >= 0) { saveAs = saveAs.substring(0, saveAs.indexOf(':')); }
        if (extension != null) {
            saveAs = saveAs + "." + extension;
        }
        return saveAs;
    }


    /**
     * Waits for downloading threads to complete.
     */
    protected void waitForThreads() {
        LOGGER.log(Level.FINE, "Waiting for threads to finish");
        completed = false;
        threadPool.waitForThreads();
        checkIfComplete();
    }



    /**
     * @return Number of files downloaded.
     */
    int getCount() {
        return 1;
    }

    /**
     * Notifies observers and updates state if all files have been ripped.
     */
    void checkIfComplete() {

        if (!completed) {
            completed = true;
            LOGGER.log(Level.INFO, "   Rip completed!");


            if (Utils.getConfigBoolean("urls_only.save", false)) {
                String urlFile = "";
                if (Utils.getConfigBoolean("no_subfolder", false))
                {
                    try
                    {
                        urlFile = this.workingDir + File.separator + getAlbumTitle(this.url) + ".txt";
                    }
                    catch (MalformedURLException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        urlFile = this.workingDir + File.separator + "urls.txt";
                    }
                }
                else {
                    urlFile = this.workingDir + File.separator + "urls.txt";
                }
                try {
                    Desktop.getDesktop().open(new File(urlFile));
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error while opening " + urlFile, e);
                }
            }
        }
    }

    /**
     * Gets URL
     * @return 
     *      Returns URL that wants to be downloaded.
     */
    public URL getURL() {
        return url;
    }

    /**
     * @return
     *      Path to the directory in which all files
     *      ripped via this ripper will be stored.
     */
    public File getWorkingDir() {
        return workingDir;
    }

    @Override
    public abstract void setWorkingDir(URL url) throws IOException;

    /**
     * 
     * @param url 
     *      The URL you want to get the title of.
     * @return
     *      host_URLid
     *      e.g. (for a reddit post)
     *      reddit_post_7mg2ur
     * @throws MalformedURLException 
     *      If any of those damned URLs gets malformed.
     */
    public String getAlbumTitle(URL url) throws MalformedURLException {
        return getHost() + "_" + getGID(url);
    }

    /**
     * Finds, instantiates, and returns a compatible ripper for given URL.
     * @param url
     *      URL to rip.
     * @return
     *      Instantiated ripper ready to rip given URL.
     * @throws Exception
     *      If no compatible rippers can be found.
     */
    public static AbstractRipper getRipper(URL url) throws Exception {
        for (Constructor<?> constructor : getRipperConstructors("me.goddragon.teaseai.utils.libraries.ripme.ripper.rippers")) {
            try {
                AlbumRipper ripper = (AlbumRipper) constructor.newInstance(url); // by design: can throw ClassCastException
                LOGGER.log(Level.FINE, "Found album ripper: " + ripper.getClass().getName());
                return ripper;
            } catch (Exception e) {
                // Incompatible rippers *will* throw exceptions during instantiation.
            }
        }
        for (Constructor<?> constructor : getRipperConstructors("me.goddragon.teaseai.utils.libraries.ripme.ripper.rippers.video")) {
            try {
                VideoRipper ripper = (VideoRipper) constructor.newInstance(url); // by design: can throw ClassCastException
                LOGGER.log(Level.FINE, "Found video ripper: " + ripper.getClass().getName());
                return ripper;
            } catch (Exception e) {
                // Incompatible rippers *will* throw exceptions during instantiation.
            }
        }
        throw new Exception("No compatible ripper found");
    }

    /**
     * @param pkg
     *      The package name.
     * @return
     *      List of constructors for all eligible Rippers.
     * @throws Exception
     */
    public static List<Constructor<?>> getRipperConstructors(String pkg) throws Exception {
        List<Constructor<?>> constructors = new ArrayList<>();
        for (Class<?> clazz : Utils.getClassesForPackage(pkg)) {
            if (AbstractRipper.class.isAssignableFrom(clazz)) {
                constructors.add(clazz.getConstructor(URL.class));
            }
        }
        return constructors;
    }
    
    /**
     * Get the completion percentage.
     * @return 
     *      Percentage complete
     */
    public abstract int getCompletionPercentage();
    /**
     * @return 
     *      Text for status
     */
    public abstract String getStatusText();

    /**
     * Rips the album when the thread is invoked.
     */
    public void run() {
        try {
            rip();
        } catch (HttpStatusException e) {
            LOGGER.log(Level.SEVERE, "Got exception while running ripper:", e);
            waitForThreads();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Got exception while running ripper:", e);
            waitForThreads();
        } finally {
            cleanup();
        }
    }
    /**
     * Tries to delete any empty directories
     */
    private void cleanup() {
        if (this.workingDir.list().length == 0) {
            // No files, delete the dir
            LOGGER.log(Level.INFO, "Deleting empty directory " + this.workingDir);
            boolean deleteResult = this.workingDir.delete();
            if (!deleteResult) {
                LOGGER.log(Level.SEVERE, "Unable to delete empty directory " +  this.workingDir);
            }
        }
    }
    
    /**
     * Pauses thread for a set amount of time.
     * @param milliseconds
     *      Amount of time (in milliseconds) that the thread gets paused for
     * @return 
     *      True if paused successfully
     *      False if failed to pause/got interrupted.
     */
    protected boolean sleep(int milliseconds) {
        try {
            LOGGER.log(Level.FINE, "Sleeping " + milliseconds + "ms");
            Thread.sleep(milliseconds);
            return true;
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Interrupted while waiting to load next page", e);
            return false;
        }
    }

    public void setBytesTotal(int bytes) {
        // Do nothing
    }
    public void setBytesCompleted(int bytes) {
        // Do nothing
    }

    /** Methods for detecting when we're running a test. */
    public void markAsTest() {
        LOGGER.log(Level.FINE, "THIS IS A TEST RIP");
        thisIsATest = true;
    }
    protected static boolean isThisATest() {
        return thisIsATest;
    }

    // If true ripme uses a byte progress bar
    protected boolean useByteProgessBar() { return false;}
    // If true ripme will try to resume a broken download for this ripper
    protected boolean tryResumeDownload() { return false;}
}
