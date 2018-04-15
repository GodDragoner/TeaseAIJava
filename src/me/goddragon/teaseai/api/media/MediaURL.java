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
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by GodDragon on 26.03.2018.
 */
public class MediaURL extends MediaHolder implements Observable {

    public static final String URL_FILE_PATH = "Images" + File.pathSeparator + "System" + File.pathSeparator + "URL Files";
    public static final String IMAGE_DOWNLOAD_PATH = "Images" + File.pathSeparator + "System" + File.pathSeparator + "Tumblr";

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
        if(this.url != null && !this.url.startsWith("http")) {
            this.url = "https://" + this.url;
        }

        if(fileName != null) {
            urlFileName = fileName;
        } else {

            urlFileName = getFileName();
        }

        //Create dirs
        new File(URL_FILE_PATH).mkdirs();
        new File(IMAGE_DOWNLOAD_PATH).mkdirs();

        this.file = new File(URL_FILE_PATH + File.pathSeparator + urlFileName);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(url.toLowerCase().contains("tumblr.com")) {
                loadImagesFromTumblrURL(progressLabel);
                saveToFile();
            }
        } else {
            try {
                //Open the file
                FileInputStream fstream = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

                int line = 0;

                String strLine;

                //Read line by line
                while ((strLine = br.readLine()) != null)   {
                    if(line == 0) {
                        this.url = strLine;
                    } else if(line == 1) {
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
    }

    public String getFileName() {
        //If the url ends with a / remove it
        if(url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        //Replace illegal characters
        String urlFileName = url.replace("http://", "");
        urlFileName = urlFileName.replace("https://", "");
        urlFileName = urlFileName.replace("/", "");
        urlFileName += ".txt";

        return urlFileName;
    }


    public void loadImagesFromTumblrURL(Label progressLabel) {
        mediaURLs.clear();

        int currentIndex = 0;
        int num = 50;
        int imagesFound = num;

        while(imagesFound >= num) {
            imagesFound = 0;
            String apiUrl = url + "/api/read?type=photo&num=" + num + "&start=" + currentIndex;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new URL(apiUrl).openStream());
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
            } catch (ParserConfigurationException | SAXException | IOException e) {
                TeaseLogger.getLogger().log(Level.SEVERE, "Failed to find image-url in tumblr blog '" + url + "'.", e);
            }

            currentIndex += num;

            if(progressLabel != null) {
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

            for(String mediaURL : mediaURLs) {
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
        DOMImplementationLS lsImpl = (DOMImplementationLS)node.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
        LSSerializer lsSerializer = lsImpl.createLSSerializer();
        NodeList childNodes = node.getChildNodes();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < childNodes.getLength(); i++) {
            sb.append(lsSerializer.writeToString(childNodes.item(i)));
        }
        return sb.toString();
    }

    @Override
    public File getRandomMedia() {
        return getRandomMedia(0);
    }

    public File getRandomMedia(int loops) {
        if(!mediaURLs.isEmpty()) {
            for(int tries = 0; tries < 10; tries++) {
                String url = mediaURLs.get(RandomUtils.randInt(0, mediaURLs.size() - 1));

                String[] split = url.split("/");
                String path = split[split.length - 1];

                path = IMAGE_DOWNLOAD_PATH + File.pathSeparator + path;
                File file = new File(path);

                if(file.exists()) {
                    return file;
                }

                try {
                    InputStream in = new BufferedInputStream(new URL(url).openStream());
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];

                    int n;
                    while (-1!=(n=in.read(buf))) {
                        out.write(buf, 0, n);
                    }
                    out.close();
                    in.close();

                    byte[] response = out.toByteArray();

                    FileOutputStream fos = new FileOutputStream( path);
                    fos.write(response);
                    fos.close();
                } catch (IOException e) {
                    //Try different media if picture is down
                    if(e instanceof ConnectException && loops < 10) {
                        return getRandomMedia(loops);
                    }

                    e.printStackTrace();
                }

                if(file.exists()) {
                    return file;
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
