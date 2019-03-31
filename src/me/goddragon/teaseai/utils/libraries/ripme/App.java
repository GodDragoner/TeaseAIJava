package me.goddragon.teaseai.utils.libraries.ripme;

import me.goddragon.teaseai.utils.TeaseLogger;
import me.goddragon.teaseai.utils.libraries.ripme.ripper.AbstractRipper;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * Entry point to application.
 * This is where all the fun happens, with the main method.
 * Decides to display UI or to run silently via command-line.
 *
 * As the "controller" to all other classes, it parses command line parameters and loads the history.
 */
public class App {

    public static final TeaseLogger logger = TeaseLogger.getLogger();
    public static String stringToAppendToFoldername = null;


    public static File mediaUrlRip(String url, String directory, boolean useForTease) throws Exception {
        Utils.setConfigBoolean("urls_only.save", true);
        Utils.setConfigBoolean("errors.skip404", true);
        Utils.setConfigBoolean("download.save_order", false);
        Utils.setConfigString("rips.directory", directory);
        Utils.setConfigBoolean("album_titles.save", false);
        Utils.setConfigBoolean("no_subfolder", true);
        Utils.setConfigBoolean("media_url", true);
        Utils.setConfigBoolean("use_for_tease", useForTease);
        Utils.setConfigInteger("history.end_rip_after_already_seen", 2000);
        ripURL(url, true);

        return new File(Utils.getConfigString("url_file", null));
    }
    
    /**
     * Creates an abstract ripper and instructs it to rip.
     * @param url URL to be ripped
     * @throws Exception Nothing too specific here, just a catch-all.
     *
     */
    private static void rip(URL url) throws Exception {
        AbstractRipper ripper = AbstractRipper.getRipper(url);
        ripper.setup();
        ripper.rip();
    }
    /**
     * Attempt to rip targetURL.
     * @param targetURL URL to rip
     * @param saveConfig Whether or not you want to save the config (?)
     * @throws Exception 
     */
    private static void ripURL(String targetURL, boolean saveConfig) throws Exception {
        try {
            URL url = new URL(targetURL);
            rip(url);
            List<String> history = Utils.getConfigList("download.history");
            if (!history.contains(url.toExternalForm())) {//if you haven't already downloaded the file before
                history.add(url.toExternalForm());//add it to history so you won't have to redownload
                Utils.setConfigList("download.history", Arrays.asList(history.toArray()));
                if (saveConfig) {
                    Utils.saveConfig();
                }
            }
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "[!] Given URL is not valid. Expected URL format is http://domain.com/...");
            if (Utils.getConfigBoolean("media_url", false))
            {
                throw e;
            }
            // System.exit(-1);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "[!] Error while ripping URL " + targetURL, e);
            if (Utils.getConfigBoolean("media_url", false))
            {
                throw e;
            }
            // System.exit(-1);
        }
    }


    
}
