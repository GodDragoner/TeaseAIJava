package me.goddragon.teaseai.api.chat.vocabulary;

import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GodDragon on 23.03.2018.
 */
public class VocabularyHandler {

    private static VocabularyHandler handler = new VocabularyHandler();

    private HashMap<String, Vocabulary> vocabularies = new HashMap<>();

    public void loadDefaultVocabulary() {
        vocabularies.put("subname", new Vocabulary((Object) ChatHandler.getHandler().getSubParticipant().getName()));
        vocabularies.put("domname", new Vocabulary((Object) ChatHandler.getHandler().getMainDomParticipant().getName()));
        vocabularies.put("domfriend1name", new Vocabulary((Object) ChatHandler.getHandler().getParticipantById(2).getName()));
        vocabularies.put("domfriend2name", new Vocabulary((Object) ChatHandler.getHandler().getParticipantById(3).getName()));
        vocabularies.put("domfriend3name", new Vocabulary((Object) ChatHandler.getHandler().getParticipantById(4).getName()));
    }

    public void loadVocabulariesFromPersonality(Personality personality) {
        vocabularies.clear();
        loadDefaultVocabulary();

        File folder = personality.getFolder();
        File vocabFolder = new File(folder.getAbsolutePath() + "\\Vocabularies");
        vocabFolder.mkdir();

        for (File file : vocabFolder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".txt")) {
                try {
                    // Open the file
                    FileInputStream fstream = new FileInputStream(file);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

                    String strLine;

                    int line = 0;
                    Vocabulary vocabulary = null;
                    //Read File Line By Line
                    while ((strLine = br.readLine()) != null) {
                        if (line == 0) {
                            vocabulary = new Vocabulary(strLine);
                        } else {
                            //Clear the default synonym because we have some alternatives
                            if(line == 1) {
                                vocabulary.getSynonyms().clear();
                            }

                            double chance = 1;
                            String synonym = strLine;
                            //Custom chance are separated by ;
                            if (strLine.contains(";")) {
                                String[] split = strLine.split(";");
                                synonym = split[0];
                                try {
                                    chance = Double.parseDouble(split[1]);
                                } catch(NumberFormatException ex) {
                                    TeaseLogger.getLogger().log(Level.SEVERE, "Malformed vocabulary '" + file.getName() + "'. Found ; but didn't find valid chance in string " + strLine);
                                }
                            }

                            vocabulary.getSynonyms().put(synonym, chance);
                        }

                        line++;
                    }

                    //Close the input stream
                    br.close();

                    if(vocabulary != null) {
                        //Remove .txt extension
                        registerVocabulary(file.getName().substring(0, file.getName().length() - 4), vocabulary);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        TeaseLogger.getLogger().log(Level.INFO, "Loaded " + vocabularies.size() + " vocabularies.");
    }

    public void registerVocabulary(String name, Object... answers) {
        vocabularies.put(name.trim().toLowerCase(), new Vocabulary(answers));
    }

    public void registerVocabulary(String name, Vocabulary vocabulary) {
        vocabularies.put(name.trim().toLowerCase(), vocabulary);
    }

    public Vocabulary getVocabulary(String name) {
        return vocabularies.getOrDefault(name.trim().toLowerCase(), new Vocabulary(name));
    }

    public String replaceAllVocabularies(String message) {
        String result = message;
        Pattern p = Pattern.compile("[%][a-zA-Z0-9_]*[%]");
        Matcher m = p.matcher(message);
        boolean foundMatch = false;
        while (m.find()) {
            foundMatch = true;
            String vocab = m.group().substring(1, m.group().length() - 1);
            result = result.replace(m.group(), getVocabulary(vocab).toString());
        }

        int loops = 0;
        String newResult = result;

        //Recursively replace all vocabularies that were entered using vocabularies
        //Do it at least once because only after that we can be sure that there are no vocabs to replace left
        while (!result.equals(newResult) && loops < 10 || loops == 0 && foundMatch) {
            result = newResult;
            newResult = replaceAllVocabularies(result);
            loops++;
        }

        if (loops == 10) {
            //TODO: Log max amount of calling here
            System.out.println("Max loop exceeded");
        }

        return newResult;
    }

    public static VocabularyHandler getHandler() {
        return handler;
    }

    public static void setHandler(VocabularyHandler handler) {
        VocabularyHandler.handler = handler;
    }

    public HashMap<String, Vocabulary> getVocabularies() {
        return vocabularies;
    }

    public void setVocabularies(HashMap<String, Vocabulary> vocabularies) {
        this.vocabularies = vocabularies;
    }
}
