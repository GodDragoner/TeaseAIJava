package me.goddragon.teaseai.api.config;

import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.*;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class ConfigHandler {
    private String configName;
    private URL url;
    private final Collection<ConfigValue> configValues = new HashSet<>();

    private final Properties properties = new Properties();

    public ConfigHandler(String configName) {
        this.configName = configName;
    }

    public ConfigHandler(URL url) {
        this.url = url;
    }

    public void loadConfig() {
        InputStream input = null;
        try {
            //Do we want to fetch it from a file or url?
            if(configName != null && configName.length() > 0) {
                File config = new File(configName);
                input = new FileInputStream(config);

                if (!config.exists()) {
                    try {
                        config.createNewFile();
                    } catch (IOException e) {
                        TeaseLogger.getLogger().log(Level.SEVERE, "Failed to load config.", e);
                    }
                }
            }
            //No config name means we want to fetch an url
            else {
                input = url.openStream();
            }

            //Load a properties file
            properties.load(input);

            for(ConfigValue configValue : configValues) {
                //Fetch the value from the config/create default
                configValue.getValue();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        saveConfig();
    }

    public void saveConfig() {
        if(configName == null || configName.length() == 0) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Can't save a config to an url. URL: '" + url.toString() + "'.");
            return;
        }

        try {
            properties.store(new FileOutputStream(configName), null);
        } catch (IOException e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Failed to save config.", e);
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public Collection<ConfigValue> getConfigValues() {
        return configValues;
    }
}
