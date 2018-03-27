package me.goddragon.teaseai.api.scripts;

import me.goddragon.teaseai.api.chat.response.ResponseHandler;
import me.goddragon.teaseai.api.chat.vocabulary.VocabularyHandler;
import me.goddragon.teaseai.api.scripts.nashorn.*;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.utils.TeaseLogger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class ScriptHandler {

    private static ScriptHandler handler = new ScriptHandler();
    private final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

    private Personality currentPersonality;

    public void load() {
        registerFunction(new SendMessageFunction());
        registerFunction(new SendInputFunction());
        registerFunction(new PlayAudioFunction());
        registerFunction(new PlayVideoFunction());
        registerFunction(new ShowImageFunction());
        registerFunction(new RegisterVocabFunction());
        registerFunction(new RegisterResponseFunction());
        registerFunction(new AddResponseRegexFunction());
        registerFunction(new AddResponseIndicatorFunction());
        registerFunction(new DeleteVarFunction());
        registerFunction(new SetTempVarFunction());
        registerFunction(new SetVarFunction());
        registerFunction(new GetVarFunction());
        registerFunction(new ShowCategoryImageFunction());
        registerFunction(new PlayCategoryVideoFunction());

        engine.put("run", (Consumer<String>) this::evalScript);
    }

    public void registerFunction(CustomFunction function) {
        for(String functionName : function.getFunctionNames()) {
            engine.put(functionName, function);
        }
    }

    public void startPersonality(Personality personality) {
        this.currentPersonality = personality;
        VocabularyHandler.getHandler().loadVocabulariesFromPersonality(personality);
        ResponseHandler.getHandler().loadVocabulariesFromPersonality(personality);

        File mainScript = new File(personality.getFolder().getAbsolutePath() + "\\main.js");
        try {
            runScript(mainScript);
        } catch (FileNotFoundException e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Personality '" + currentPersonality.getName() + "' is missing the main.js script");
        }
    }

    public void evalScript(String scriptName) {
        if(!scriptName.toLowerCase().endsWith(".js")) {
            scriptName += ".js";
        }

        File script = new File(currentPersonality.getFolder().getAbsolutePath() + "\\" + scriptName);
        try {
            runScript(script);
        } catch (FileNotFoundException e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Script " + scriptName + " does not exist.");
        }
    }

    public void runScript(File script) throws FileNotFoundException {
        try {
            engine.eval(new FileReader(script));
        } catch (ScriptException e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Error while handling file '" + e.getFileName() + "' in line " + e.getLineNumber() + "\n" +
                    "Error: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    public ScriptEngine getEngine() {
        return engine;
    }

    public static ScriptHandler getHandler() {
        return handler;
    }
}
