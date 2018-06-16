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
    //private File supportedVariablesFile;

    private final Personality personality;
    private HashMap<String, PersonalityVariable> variables = new HashMap<>();

    private HashMap<String, PersonalityVariable> nonSetSupportedVariables = new HashMap<>();

    public VariableHandler(Personality personality) {
        this.personality = personality;
    }

    public void loadVariables() {
        variables.clear();
        nonSetSupportedVariables.clear();

        personalityVariableFolder = new File(personality.getFolder().getPath() + File.separator + "System" + File.separator + "Variables");
        personalityVariableFolder.mkdirs();

        /*supportedVariablesFile = new File(personality.getFolder().getPath() + File.separator + "variableInformation.txt");
        if(!supportedVariablesFile.exists()) {
            try {
                supportedVariablesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

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

                        //If we already know about this variable try to restore custom information
                        if(variableExist(personalityVariable.getConfigName())) {
                            PersonalityVariable existingVariable = getVariable(personalityVariable.getConfigName());
                            //Check whether this variable has any relevant information
                            if(existingVariable.isSupportedByPersonality()) {
                                personalityVariable.setDescription(existingVariable.getDescription());
                                personalityVariable.setCustomName(existingVariable.getCustomName());
                            }
                        }

                        variables.put(personalityVariable.getConfigName(), personalityVariable);
                    }

                    //Close the input stream
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /*try {
            // Open the file
            FileInputStream fstream = new FileInputStream(supportedVariablesFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String strLine;

            //Read file line by line
            while((strLine = br.readLine()) != null) {

            }

            //Close the input stream
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public Object setVariable(String name, Object value) {
        return setVariable(name, value, false);
    }

    public Object setVariable(String name, Object value, boolean temporary) {
        name = name.toLowerCase();

        PersonalityVariable personalityVariable;
        if(variableExist(name)) {
            //Just update the existing value
            personalityVariable = getVariable(name);

            //Skip setting the variable because we have the same value already stored
            if ((getVariable(name).getValue().equals(value) || getVariable(name).getValue() == value)) {
                return value;
            }

            personalityVariable.setValue(value);
        } else {
            personalityVariable = new PersonalityVariable(name, value);

            if(nonSetSupportedVariables.containsKey(name)) {
                PersonalityVariable supportedVariable = nonSetSupportedVariables.get(name);
                personalityVariable.setSupportedByPersonality(true);
                personalityVariable.setCustomName(supportedVariable.getCustomName());
                personalityVariable.setDescription(supportedVariable.getDescription());
                nonSetSupportedVariables.remove(name);
            }

            variables.put(personalityVariable.getConfigName(), personalityVariable);
        }


        //If this is not meant to be permanent we can just skip the file stuff
        if (temporary) {
            personalityVariable.setTemporary(true);
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
        name = name.toLowerCase();

        if(variableExist(name)) {
            PersonalityVariable personalityVariable = getVariable(name);

            //Move the supported variable to the list of not yet set variables
            if(personalityVariable.isSupportedByPersonality()) {
                nonSetSupportedVariables.put(name, personalityVariable);
            }
        }

        variables.remove(name);

        //If the variable is not existing we don't need to create it anyway
        File variableFile = getVariableFile(name, false);

        if (variableFile != null) {
            variableFile.delete();
        }
    }

    public Object getVariableValue(String name) {
        name = name.toLowerCase();

        if (!variables.containsKey(name)) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Variable '" + name + "' does not exist.");
            return null;
        }

        return variables.get(name).getValue();
    }

    public PersonalityVariable getVariable(String name) {
        name = name.toLowerCase();

        if (!variables.containsKey(name)) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Variable '" + name + "' does not exist.");
            return null;
        }

        return variables.get(name);
    }

    public boolean isVariable(String name) {
        name = name.toLowerCase();

        Object variable = getVariableValue(name);
        return variable instanceof Boolean && variable.equals(Boolean.TRUE);
    }

    public boolean variableExist(String name) {
        return variables.containsKey(name.toLowerCase());
    }

    public Collection<PersonalityVariable> getTemporaryVariables() {
        Collection<PersonalityVariable> tempVariables = new HashSet<>();
        for(PersonalityVariable variable : variables.values()) {
            if(variable.isTemporary()) {
                tempVariables.add(variable);
            }
        }

        return tempVariables;
    }

    public void clearTemporaryVariables() {
        //Remove all temporary variables
        for(PersonalityVariable tempVariable : getTemporaryVariables()) {
            variables.remove(tempVariable.getConfigName());
        }
    }

    public File getVariableFile(String name) {
        return getVariableFile(name, true);
    }

    public File getVariableFile(String name, boolean createDefault) {
        File variableFile = new File(personalityVariableFolder.getAbsolutePath() + File.separator + name + ".var");

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

    public void setVariableSupport(String variableName, String customName, String description) {
        variableName = variableName.toLowerCase();

        if(variableExist(variableName)) {
            PersonalityVariable personalityVariable = getVariable(variableName);
            personalityVariable.setDescription(description);
            personalityVariable.setCustomName(customName);
            personalityVariable.setSupportedByPersonality(true);
        } else {
            PersonalityVariable personalityVariable = new PersonalityVariable(variableName, null);
            personalityVariable.setDescription(description);
            personalityVariable.setCustomName(customName);
            personalityVariable.setSupportedByPersonality(true);

            nonSetSupportedVariables.put(variableName, personalityVariable);
        }
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
