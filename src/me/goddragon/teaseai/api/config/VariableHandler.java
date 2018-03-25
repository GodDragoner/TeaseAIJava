package me.goddragon.teaseai.api.config;

import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class VariableHandler {
    private static VariableHandler handler = new VariableHandler();

    //TODO: Change this to the personality system
    private String currentPersonalityName = "Default";
    private File personalityVariableFolder = new File("Personalities\\" + currentPersonalityName + "\\System\\Variables");

    private HashMap<String, Object> variables = new HashMap<>();

    public VariableHandler() {
        personalityVariableFolder.mkdirs();
    }

    public void setVariable(String name, Object value) {
        setVariable(name, value, false);
    }

    public void setVariable(String name, Object value, boolean temporary) {
        name = name.toLowerCase();
        variables.put(name, value);

        //If this is not meant to be permanent we can just skip the file stuff
        if(temporary) {
            return;
        }

        File variableFile = getVariableFile(name);

        if(variableFile != null) {
            List<String> lines = Arrays.asList(value.toString());

            try {
                Files.write(Paths.get(variableFile.toURI()), lines, Charset.forName("UTF-8"));
            } catch (IOException e) {
                TeaseLogger.getLogger().log(Level.SEVERE, "Failed to write variable '" + name + "'.", e);
            }
        }
    }

    public void deleteVariable(String name) {
        variables.remove(name);

        //If the variable is not existing we don't need to create it anyway
        File variableFile = getVariableFile(name, false);

        if(variableFile != null) {
            variableFile.delete();
        }
    }

    public Object getVariable(String name) {
        if(!variables.containsKey(name)) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Variable '" + name + "' does not exist.");
            return null;
        }

        return variables.get(name);
    }

    public boolean isVariable(String name) {
        Object variable = getVariable(name);
        return variable instanceof Boolean && variable.equals(Boolean.TRUE);
    }

    public File getVariableFile(String name) {
        return getVariableFile(name, true);
    }

    public File getVariableFile(String name, boolean createDefault) {
        File variableFile = new File(personalityVariableFolder.getAbsolutePath() + "\\" + name + ".var");

        if(!variableFile.exists() && createDefault) {
            try {
                variableFile.createNewFile();
            } catch (IOException e) {
                TeaseLogger.getLogger().log(Level.SEVERE, "Failed to create variable '" + name + "'.", e);
                return null;
            }
        }

        return variableFile;
    }

    public static VariableHandler getHandler() {
        return handler;
    }

    public static void setHandler(VariableHandler handler) {
        VariableHandler.handler = handler;
    }
}
