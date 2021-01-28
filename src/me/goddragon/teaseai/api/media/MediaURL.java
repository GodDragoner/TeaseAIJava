package me.goddragon.teaseai.api.media;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.Label;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.utils.RandomUtils;
import me.goddragon.teaseai.utils.TeaseLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by GodDragon on 26.03.2018.
 */
public class MediaURL extends MediaHolder implements Observable {

    public static final String URL_FILE_PATH = "Images" + File.separator + "System" + File.separator + "URL Files";
    public static final String IMAGE_DOWNLOAD_PATH = "Images" + File.separator + "System" + File.separator + "Downloaded Images";

    private String url;
    private boolean useForTease = false;
    private final File file;
    private final List<String> mediaURLs = new ArrayList<>();


    public MediaURL(MediaType mediaType, String url) {
        this(mediaType, url, null);
    }

    public MediaURL(MediaType mediaType, String url, String fileName) {
        this(mediaType, url, fileName, null);
    }

    public MediaURL(MediaType mediaType, String url, String fileName, Label progressLabel) {
        super(mediaType);

        String urlFileName;

        this.url = url;
        if (this.url != null && !this.url.startsWith("http")) {
            this.url = "https://" + this.url;
        }

        if (fileName != null) {
            urlFileName = fileName;
        } else {
            urlFileName = getFileName();
        }

        //Create dirs
        new File(URL_FILE_PATH).mkdirs();
        new File(IMAGE_DOWNLOAD_PATH).mkdirs();

        this.file = new File(URL_FILE_PATH + File.separator + urlFileName);
        if (!file.exists()) {
            if (url == null) {
                TeaseLogger.getLogger().log(Level.SEVERE, "URL file '" + file.getPath() + "' does not exist.");
                return;
            }

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (url.toLowerCase().contains("tumblr.com")) {
                loadImagesFromTumblrURL(progressLabel);
                saveToFile();
            }
        } else {
            fromFile();
        }
    }

    public MediaURL(MediaType mediaType, File file) {
        super(mediaType);

        //Create dirs
        new File(IMAGE_DOWNLOAD_PATH).mkdirs();

        this.file = file;
        fromFile();
    }

    public void reloadFromFile() {
        mediaURLs.clear();
        fromFile();
    }

    private void fromFile() {
        try {
            //Open the file
            FileInputStream fstream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            int line = 0;

            String strLine;

            //Read line by line
            while ((strLine = br.readLine()) != null) {
                if (line == 0) {
                    this.url = strLine;
                } else if (line == 1) {
                    useForTease = Boolean.valueOf(strLine);
                } else {
                    //Add the url to the list
                    mediaURLs.add(strLine);
                }

                line++;
            }

            //Close the input stream
            br.close();
        } catch (IOException e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Failed to download image from url '" + url + "'.", e);
            e.printStackTrace();
        }
    }

    public String getFileName() {
        //If the url ends with a / remove it
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        //Replace illegal characters
        String urlFileName = url.replace("http://", "");
        urlFileName = urlFileName.replace("https://", "");
        urlFileName = urlFileName.replace("/", "");
        urlFileName += ".txt";

        return urlFileName;
    }

    public static void loadImagesFromTumblrURL() {

    }

    public void loadImagesFromTumblrURL(Label progressLabel) {
        mediaURLs.clear();

        // Create a new client
        /*JumblrClient client = new JumblrClient("6XgzfM8rK0ddEkEIyuhanGzLDUX6q3zxfaZErn9hCw9ngHKl2M", "CA9J5P5kU7uCDRSgN6l4TjZduIFZOh4mTgvm2wE9z1wKyY0hOg");
        //client.setToken("oauth_token", "oauth_token_secret");


        Blog blog = client.blogInfo(url.replace("http://", "").replace("https://", "").replace("/", ""));

        int posts = blog.getPostCount();
        int postsPerRequest = 20;

        Map<String, Object> parameters = new HashMap<>();

        int offset = 0;
        while(true) {
            offset = Math.min(offset + postsPerRequest, posts);
            parameters.put("offset", offset);
            for (Post post : blog.posts(parameters)) {
                if (post instanceof PhotoPost) {
                    for (Photo photo : ((PhotoPost) post).getPhotos()) {
                        /*Field f = photo.getClass().getDeclaredField("source");
                        f.setAccessible(true);
                        String source = (String) f.get(photo);

                        System.out.println(source);
                        if(source != null && source.length() > 0) {

                        }*/

                        /*PhotoSize biggestSize = null;
                        int widthHeight = 0;
                        for (PhotoSize size : photo.getSizes()) {
                            if(size.getHeight() + size.getWidth() > widthHeight) {
                                widthHeight = size.getHeight() + size.getWidth();
                                biggestSize = size;
                            }
                        }

                        mediaURLs.add(biggestSize.getUrl());
                    }
                }
            }

            if(offset == posts) {
                break;
            }
        }*/

        int currentIndex = 0;
        int num = 50;
        int imagesFound = num;

        while (imagesFound >= num) {
            imagesFound = 0;
            String apiUrl = url + "/api/read?type=photo&filter=text&num=" + num + "&start=" + currentIndex;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();

                //Create an URL connection object
                URLConnection urlConnection = new URL(apiUrl).openConnection();

                //Different user Agent so we can bypass the GPDR thing
                urlConnection.setRequestProperty("User-Agent", "curl/7.54.0");

                Document doc = db.parse(urlConnection.getInputStream());

                NodeList nodeList = doc.getElementsByTagName("photo-url");
                for (int x = 0; x < nodeList.getLength(); x++) {
                    Node node = nodeList.item(x);
                    if (node.hasAttributes() && node.getAttributes().getNamedItem("max-width") != null) {
                        if (node.getAttributes().getNamedItem("max-width").getTextContent().equals("1280")) {
                            String imageUrl = innerXml(node);
                            if (!mediaURLs.contains(imageUrl)) {
                                mediaURLs.add(imageUrl);
                                imagesFound++;
                            }
                        }
                    }
                }
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException | IOException e) {
                e.printStackTrace();
                TeaseLogger.getLogger().log(Level.SEVERE, "Failed to find image-url in tumblr blog '" + url + "'.", e);
            }

            currentIndex += num;

            if (progressLabel != null) {
                int finalCurrentIndex = currentIndex;
                TeaseAI.application.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        progressLabel.setText(finalCurrentIndex + " files found.");
                    }
                });
            }
        }
    }

    public void deleteFile() {
        file.delete();
    }

    public void saveToFile() {
        try {
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            writer.println(url);
            writer.println(useForTease);

            for (String mediaURL : mediaURLs) {
                writer.println(mediaURL);
            }

            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private String innerXml(Node node) {
        NodeList childNodes = node.getChildNodes();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < childNodes.getLength(); i++) {
            sb.append(childNodes.item(i).getTextContent());
        }

        return sb.toString();
    }

    @Override
    public File getRandomMedia() {
        if (!mediaURLs.isEmpty()) {
            for (int tries = 0; tries < 10; tries++) {
                String mediaUrl = mediaURLs.get(RandomUtils.randInt(0, mediaURLs.size() - 1));

                final File imageFile = MediaHandler.getHandler().tryGetImageFromURL(mediaUrl);
                if (imageFile != null) {
                    return imageFile;
                }
            }
        }

        return null;
    }

    public String getUrl() {
        return url;
    }

    public boolean isUseForTease() {
        return useForTease;
    }

    public void setUseForTease(boolean useForTease) {
        this.useForTease = useForTease;
    }

    public File getFile() {
        return file;
    }

    public List<String> getMediaURLs() {
        return mediaURLs;
    }

    @Override
    public String toString() {
        return url;
    }

    @Override
    public void addListener(InvalidationListener listener) {

    }

    @Override
    public void removeListener(InvalidationListener listener) {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MediaURL player = (MediaURL) o;

        if (url != null ? !url.equals(player.getUrl()) : player.getUrl() != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}
