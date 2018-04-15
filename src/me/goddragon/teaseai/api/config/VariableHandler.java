package me.goddragon.teaseai.api.config;

import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class VariableHandler {
    private File personalityVariableFolder;

    private final Personality personality;
    private HashMap<String, PersonalityVariable> variables = new HashMap<>();


    public VariableHandler(Personality personality) {
        this.personality = personality;
        personalityVariableFolder = new File(personality.getFolder().getPath() + File.pathSeparator + "System" + File.pathSeparator + "Variables");
        personalityVariableFolder.mkdirs();

        for (File file : personalityVariableFolder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".var")) {

                try {
                    // Open the file
                    FileInputStream fstream = new FileInputStream(file);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

                    //We only need the first line
                    String strLine = br.readLine();

                    if (strLine != null) {
                        PersonalityVariable personalityVariable = new PersonalityVariable(file.getName().substring(0, file.getName().length() - 4), getObjectFromString(strLine));
                        variables.put(personalityVariable.getConfigName(), personalityVariable);
                    }

                    //Close the input stream
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public Object setVariable(String name, Object value) {
        return setVariable(name, value, false);
    }

    public Object setVariable(String name, Object value, boolean temporary) {
        name = name.toLowerCase();

        //Skip setting the variable because we have the same value already stored
        if(variableExist(name) && (getVariable(name).getValue().equals(value) || getVariable(name).getValue() == value)) {
            return value;
        }

        PersonalityVariable personalityVariable = new PersonalityVariable(name, value);
        variables.put(personalityVariable.getConfigName(), personalityVariable);

        //If this is not meant to be permanent we can just skip the file stuff
        if (temporary) {
            return value;
        }

        File variableFile = getVariableFile(name);

        if (variableFile != null) {
            List<String> lines = Arrays.asList(value.toString());

            try {
                Files.write(Paths.get(variableFile.toURI()), lines, Charset.forName("UTF-8"));
            } catch (IOException e) {
                TeaseLogger.getLogger().log(Level.SEVERE, "Failed to write variable '" + name + "'.", e);
            }
        }

        return value;
    }

    public void deleteVariable(String name) {
        variables.remove(name.toLowerCase());

        //If the variable is not existing we don't need to create it anyway
        File variableFile = getVariableFile(name, false);

        if (variableFile != null) {
            variableFile.delete();
        }
    }

    public Object getVariableValue(String name) {
        if (!variables.containsKey(name.toLowerCase())) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Variable '" + name + "' does not exist.");
            return null;
        }

        return variables.get(name.toLowerCase()).getValue();
    }

    public PersonalityVariable getVariable(String name) {
        if (!variables.containsKey(name.toLowerCase())) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Variable '" + name + "' does not exist.");
            return null;
        }

        return variables.get(name.toLowerCase());
    }

    public boolean isVariable(String name) {
        Object variable = getVariableValue(name);
        return variable instanceof Boolean && variable.equals(Boolean.TRUE);
    }

    public boolean variableExist(String name) {
        return variables.containsKey(name.toLowerCase());
    }


    public File getVariableFile(String name) {
        return getVariableFile(name, true);
    }

    public File getVariableFile(String name, boolean createDefault) {
        File variableFile = new File(personalityVariableFolder.getAbsolutePath() + File.pathSeparator + name + ".var");

        if (!variableFile.exists() && createDefault) {
            try {
                variableFile.createNewFile();
            } catch (IOException e) {
                TeaseLogger.getLogger().log(Level.SEVERE, "Failed to create variable '" + name + "'.", e);
                return null;
            }
        }

        return variableFile;
    }

    public Object getObjectFromString(String string) {
        //Check for boolean first, because anything that is not true will be treated as false otherwise
        if(string.equals("true") || string.equals("false")) {
            return Boolean.valueOf(string);
        }

        Collection< Class<?>> classes = new ArrayList<>();
        classes.add(TeaseDate.class);
        classes.add(Integer.class);
        classes.add(Long.class);
        classes.add(Double.class);
        classes.add(Float.class);

        for(Class clazz : classes)
            try {
                Method method = clazz.getDeclaredMethod("valueOf", String.class);
                if (method != null) {
                    try {
                        Object obj = method.invoke(null, string);
                        return obj;
                    } catch (InvocationTargetException ex) {
                        //Try something different

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

        return string;
    }

    public HashMap<String, PersonalityVariable> getVariables() {
        return variables;
    }
}
