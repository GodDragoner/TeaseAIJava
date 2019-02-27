package me.goddragon.teaseai.utils.libraries.ripme.ripper;

import me.goddragon.teaseai.utils.libraries.ripme.utils.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;


public abstract class VideoRipper extends AbstractRipper {

    private int bytesTotal = 1;
    private int bytesCompleted = 1;

    protected VideoRipper(URL url) throws IOException {
        super(url);
    }

    public abstract void rip() throws IOException;

    public abstract String getHost();

    public abstract String getGID(URL url) throws MalformedURLException;

    @Override
    public void setBytesTotal(int bytes) {
        this.bytesTotal = bytes;
    }

    @Override
    public void setBytesCompleted(int bytes) {
        this.bytesCompleted = bytes;
    }

    @Override
    public String getAlbumTitle(URL url) {
        return "videos";
    }

    @Override
    public boolean addURLToDownload(URL url, File saveAs) {
        if (Utils.getConfigBoolean("urls_only.save", false)) {
            // Output URL to file
            String urlFile = this.workingDir + File.separator + "urls.txt";

            try (FileWriter fw = new FileWriter(urlFile, true)) {
                fw.write(url.toExternalForm());
                fw.write("\n");

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error while writing to " + urlFile, e);
                return false;
            }
        } else {
            if (isThisATest()) {
                // Tests shouldn't download the whole video
                // Just change this.url to the download URL so the test knows we found it.
                LOGGER.log(Level.FINE, "Test rip, found URL: " + url);
                this.url = url;
                return true;
            }
            threadPool.addThread(new DownloadVideoThread(url, saveAs, this));
        }
        return true;
    }

    @Override
    public boolean addURLToDownload(URL url, File saveAs, String referrer, Map<String, String> cookies, Boolean getFileExtFromMIME) {
        return addURLToDownload(url, saveAs);
    }

    /**
     * Creates & sets working directory based on URL.
     *
     * @param url Target URL
     */
    @Override
    public void setWorkingDir(URL url) throws IOException {
        String path = Utils.getWorkingDirectory().getCanonicalPath();

        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }

        path += "videos" + File.separator;
        workingDir = new File(path);

        if (!workingDir.exists()) {
            LOGGER.log(Level.INFO, "[+] Creating directory: " + Utils.removeCWD(workingDir));
            workingDir.mkdirs();
        }

        LOGGER.log(Level.FINE, "Set working directory to: " + workingDir);
    }

    /**
     * @return Returns % of video done downloading.
     */
    @Override
    public int getCompletionPercentage() {
        return (int) (100 * (bytesCompleted / (float) bytesTotal));
    }

    /**
     * Gets the status and changes it to a human-readable form.
     *
     * @return Status of current download.
     */
    @Override
    public String getStatusText() {
        return String.valueOf(getCompletionPercentage()) +
                "%  - " +
                Utils.bytesToHumanReadable(bytesCompleted) +
                " / " +
                Utils.bytesToHumanReadable(bytesTotal);
    }

    /**
     * Sanitizes URL.
     * Usually just returns itself.
     */
    @Override
    public URL sanitizeURL(URL url) throws MalformedURLException {
        return url;
    }


}