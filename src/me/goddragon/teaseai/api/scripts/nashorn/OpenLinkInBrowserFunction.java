package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.utils.TeaseLogger;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by Go-mei-sa on 01.06.2018.
 */
public class OpenLinkInBrowserFunction extends CustomFunction {

    public OpenLinkInBrowserFunction() {
        super("openLinkInBrowser");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        if (args.length == 1) {
            URL u;
            try {
                u = new URL(args[0].toString());
            } catch(MalformedURLException murle) {
                TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with malformed url:" + args[0].toString());
                return null;
            }

            URI uri;
            try {
                uri = u.toURI();
            } catch(URISyntaxException urise) {
                TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid uri:" + args[0].toString());
                return null;
            }
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        desktop.browse(uri);
                    } catch (IOException ioe){
                        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " could not be opened with arg:" + args[0].toString());
                        return null;
                    }
                } else {
                    TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " for " + args[0].toString() + " impossible, desktop browse not supported");
                }
            } else {
                TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " for " + args[0].toString() + " impossible, desktop not supported");
            }
            return null;
        }

        TeaseLogger.getLogger().log(Level.SEVERE, getFunctionName() + " called with invalid args:" + Arrays.asList(args).toString());
        return null;
    }
}