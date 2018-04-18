package me.goddragon.teaseai.api.chat.vocabulary;

import me.goddragon.teaseai.api.scripts.ScriptHandler;
import me.goddragon.teaseai.utils.StringUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Created by GodDragon on 11.04.2018.
 */
public class RunableVocabulary extends Vocabulary {

    private final String name;
    private final File script;

    public RunableVocabulary(String name, File script, Object... synonyms) {
        super(synonyms);
        this.name = name;
        this.script = script;
    }

    public RunableVocabulary(String vocabName, String name, File script) {
        super(vocabName);
        this.name = name;
        this.script = script;
    }

    public RunableVocabulary(HashMap<Object, Double> synonyms, String name, File script) {
        super(synonyms);
        this.name = name;
        this.script = script;
    }

    @Override
    public String toString() {
        ScriptEngine engine = ScriptHandler.getHandler().getEngine();
        String responseName = name;

        try {
            Invocable invocable = (Invocable) engine;

            return invocable.invokeFunction(StringUtils.decapitalize(name) + "Vocabulary").toString();
        } catch (ScriptException e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Error while handling file '" + e.getFileName() + "' in line " + e.getLineNumber() + "\n" +
                    "Error: " + e.getMessage(), false);
        } catch (NoSuchMethodException e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Vocabulary '" + responseName + " is missing the function to trigger it. Create the function '" + StringUtils.decapitalize(name) + "Vocabulary() for this to work." , false);
        } catch(NullPointerException ex) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Vocabulary '" + responseName + "' returned null!" , false);
        }

        return '!' + name + '!';
    }
}
