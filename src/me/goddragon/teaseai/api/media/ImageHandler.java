package me.goddragon.teaseai.api.media;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaView;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.TeaseLogger;
import me.goddragon.teaseai.utils.media.AnimatedGif;
import me.goddragon.teaseai.utils.media.Animation;
import me.goddragon.teaseai.utils.media.ImageUtils;

public class ImageHandler {
    public void showPicture(File file, int durationSeconds) {
        if (file == null) {
            TeaseAI.application.runOnUIThread(this::removePicture);
            return;
        }

        if (!file.exists()) {
            TeaseLogger.getLogger().log(
                    Level.SEVERE, "Picture " + file.getPath() + " does not exist.");
            return;
        }

        currentImageURL = file.getAbsolutePath();
        switchToImageView(file);
        if (durationSeconds > 0) {
            TeaseAI.application.sleepPossibleScripThread(durationSeconds * 1000L);
        }
    }

    public String getCurrentImageURL() {
        return currentImageURL;
    }

    public File tryGetImageFromURL(String url) {
        currentImageURL = url;

        final File downloadPath = getDownloadImagePathFromUrl(url);
        if (downloadPath.exists()) {
            return downloadPath;
        } else {
            if (tryDownloadImageFromURL(url, downloadPath)) {
                return downloadPath;
            }
        }

        return null;
    }

    private void switchToImageView(File file) {
        final AtomicBoolean readyFlag = new AtomicBoolean();

        TeaseAI.application.runOnUIThread(() -> {
            MediaView mediaView = TeaseAI.application.getController().getMediaView();

            if (mediaView.getMediaPlayer() != null) {
                mediaView.getMediaPlayer().stop();
            }

            ImageView imageView = TeaseAI.application.getController().getImageView();

            // Handle visibilities
            mediaView.setOpacity(0);
            imageView.setOpacity(1);

            // Stop any current image animation that might be running before displaying a new
            // picture
            stopCurrentAnimation();

            if (FileUtils.getExtension(file).equalsIgnoreCase("gif")) {
                currentAnimation = new AnimatedGif(file.toURI().toString());
                currentAnimation.setCycleCount(Integer.MAX_VALUE);
                currentAnimation.play(imageView);
            } else {
                ImageUtils.setImageInView(file, imageView);
            }
            
            synchronized (readyFlag) {
                readyFlag.set(true);
                readyFlag.notifyAll();
            }
        });

        while (!readyFlag.get()) {
            try {
                synchronized (readyFlag) {
                    readyFlag.wait();
                }
            } catch (InterruptedException ex) {
                TeaseLogger.getLogger().log(Level.WARNING,
                        "Thread interrupted while initialising image user interface");
                Thread.currentThread().interrupt();
            }
        }
    }

    private File getDownloadImagePathFromUrl(String url) {
        final String[] split = url.split("/");
        final String path = split[split.length - 1];
        final String downloadPath = MediaURL.IMAGE_DOWNLOAD_PATH + File.separator + path;
        return new File(downloadPath);
    }

    private boolean tryDownloadImageFromURL(String url, File downloadPath) {
        boolean wasSuccessful = false;

        TeaseLogger.getLogger().log(Level.FINER, String.format("Fetching url '%s'", url));

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("Referer", url);
            connection.addRequestProperty("Accept", "*/*");
            connection.addRequestProperty("User-Agent",
                    "Mozilla/5.0 (X11; Linux x86_64; rv:12.0) Gecko/20100101 Firefox/12.0");
            connection.connect();

            final int responseCode = connection.getResponseCode();
            final String responseMessage = connection.getResponseMessage();
            TeaseLogger.getLogger().log(Level.FINER,
                    String.format("Response code received %d '%s'", responseCode, responseMessage));

            if (responseCode == HttpURLConnection.HTTP_OK) {
                TeaseLogger.getLogger().log(Level.FINER,
                        String.format("Fetched %,d bytes of type '%s'",
                                connection.getContentLength(), connection.getContentType()));

                saveDownloadedImage(connection.getInputStream(), downloadPath);
                wasSuccessful = true;
            } else {
                TeaseLogger.getLogger().log(
                        Level.WARNING, "Unsupported response code, ignoring conent");
            }
        } catch (IOException ex) {
            TeaseLogger.getLogger().log(
                    Level.WARNING, "Unable to find image on url " + url + ": " + ex.getMessage());
        } catch (ClassCastException ex) {
            TeaseLogger.getLogger().log(
                    Level.SEVERE, "Url " + url + " does not appear to be an http connection");
        }

        return wasSuccessful;
    }

    private void saveDownloadedImage(InputStream inputStream, File downloadPath)
            throws IOException {
        final byte[] buffer = new byte[1024];
        try (InputStream in = new BufferedInputStream(inputStream)) {
            try (OutputStream out = new FileOutputStream(downloadPath)) {
                int bytesRead;
                while (-1 != (bytesRead = in.read(buffer))) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    private void removePicture() {
        ImageView imageView = TeaseAI.application.getController().getImageView();
        stopCurrentAnimation();
        imageView.setImage(null);
    }

    private void stopCurrentAnimation() {
        if (currentAnimation != null) {
            currentAnimation.stop();
            currentAnimation = null;
        }
    }

    private Animation currentAnimation = null;
    private String currentImageURL;
}
