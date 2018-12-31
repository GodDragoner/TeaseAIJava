package me.goddragon.teaseai.api.scripts.personality;

import javafx.scene.control.ChoiceBox;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Level;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class PersonalityManager {
    public static String PERSONALITY_FOLDER_NAME = "Personalities";

    private static PersonalityManager manager = new PersonalityManager();

    private BiConsumer<Integer, Integer> progressUpdate ;

    private final Collection<Personality> personalities = new ArrayList<>();
    
    private static Personality currentlyLoadingPersonality;

    public void loadPersonalities() {
        personalities.clear();

        List<File> folders = getPersonalityFolders();
        for(int x = 0; x < folders.size(); x++) {
            progressUpdate.accept(x + 1, folders.size());

            Personality personality = new Personality(folders.get(x).getName());
            personality.checkForUpdate();
            addPersonality(personality);
            TeaseLogger.getLogger().log(Level.INFO, "Personality '" + personality.getName() + "' version " + personality.getVersion() + " loaded.");
        }
    }

    public void addPersonalitiesToGUI() {
        ChoiceBox choiceBox = TeaseAI.application.getController().getPersonalityChoiceBox();
        choiceBox.getItems().clear();

        for(Personality personality : personalities) {
            choiceBox.getItems().add(personality);
        }

        String latestSelectedPersonality = TeaseAI.application.LAST_SELECTED_PERSONALITY.getValue();

        Personality personality = getPersonality(latestSelectedPersonality);

        if(personality != null) {
            choiceBox.getSelectionModel().select(personality);
        } else {
            choiceBox.getSelectionModel().selectFirst();
        }
    }

    public void addPersonality(Personality personality) {
        personalities.add(personality);
    }

    public Personality getPersonality(String name) {
        for(Personality personality : personalities) {
            if(personality.getName().getValue().equals(name)) {
                return personality;
            }
        }

        TeaseLogger.getLogger().log(Level.SEVERE, "Personality with name '" + name + "' does not exist.");
        return null;
    }

    public Collection<Personality> getPersonalities() {
        return personalities;
    }

    public List<File> getPersonalityFolders() {
        List<File> folders = new ArrayList<>();

        File personalityFolder = new File(PERSONALITY_FOLDER_NAME);
        personalityFolder.mkdirs();

        for(File file : personalityFolder.listFiles()) {
            //Ignore all non directories
            if (file.isDirectory()) {
                File propertiesFile = new File(file.getAbsolutePath() + File.separator + Personality.PROPERTIES_NAME);

                if(propertiesFile.exists()) {
                    folders.add(file);
                } else {
                    TeaseLogger.getLogger().log(Level.WARNING, "Personality '" + file.getName() + "' is missing a properties file. Skipping loading.");
                }
            }
        }

        return folders;
    }

    public void setProgressUpdate(BiConsumer<Integer, Integer> progressUpdate) {
        this.progressUpdate = progressUpdate ;
    }

    @Deprecated
    public Personality getActivePersonality() {
        return TeaseAI.application.getSession().getActivePersonality();
    }

    @Deprecated
    public void setActivePersonality(Personality activePersonality) {
        TeaseAI.application.getSession().setActivePersonality(activePersonality);
    }

    public static PersonalityManager getManager() {
        return manager;
    }

    public static void setManager(PersonalityManager manager) {
        PersonalityManager.manager = manager;
    }
    
    public void setLoadingPersonality(Personality personality)
    {
        currentlyLoadingPersonality = personality;
    }
    
    public Personality getLoadingPersonality()
    {
        return currentlyLoadingPersonality;
    }
}
