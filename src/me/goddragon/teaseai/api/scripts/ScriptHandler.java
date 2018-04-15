package me.goddragon.teaseai.api.scripts;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.response.ResponseHandler;
import me.goddragon.teaseai.api.chat.vocabulary.VocabularyHandler;
import me.goddragon.teaseai.api.scripts.nashorn.*;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.utils.FileUtils;
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

    private File currentFile;

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
        registerFunction(new ShowTeaseImageFunction());
        registerFunction(new UnlockImagesFunction());
        registerFunction(new LockImagesFunction());
        registerFunction(new SetDateFunction());
        registerFunction(new SetTempDateFunction());
        registerFunction(new StartStrokingFunction());
        registerFunction(new StopStrokingFunction());
        registerFunction(new IsStrokingFunction());
        registerFunction(new AddStrokingBPMFunction());
        registerFunction(new StopAudioFunction());
        registerFunction(new IsOnEdgeFunction());
        registerFunction(new StartEdgeFunction());
        registerFunction(new IsEdgingFunction());
        registerFunction(new EndEdgeFunction());
        registerFunction(new IsPlayingVideoFunction());
        registerFunction(new StopVideoFunction());
        registerFunction(new SetActiveSenderFunction());
        registerFunction(new SendCustomMessageFunction());
        registerFunction(new IsVariableFunction());
        registerFunction(new CreateInputFunction());
        registerFunction(new EndSessionFunction());
        registerFunction(new ReplaceVocabulariesFunction());
        registerFunction(new SleepFunction());
        registerFunction(new WaitFunction());

        engine.put("run", (Consumer<String>) this::evalScript);
    }

    public void registerFunction(CustomFunction function) {
        for(String functionName : function.getFunctionNames()) {
            engine.put(functionName, function);
        }
    }

    public void startPersonality(Personality personality) {
        this.currentPersonality = personality;

        currentPersonality.getVariableHandler().setVariable("personalityVersion", currentPersonality.getVersion().getValue(), true);

        VocabularyHandler.getHandler().loadVocabulariesFromPersonality(personality);
        ResponseHandler.getHandler().loadResponsesFromPersonality(personality);

        File mainScript = new File(personality.getFolder().getAbsolutePath() + File.separator + "main.js");
        try {
            runScript(mainScript);
        } catch (FileNotFoundException e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Personality '" + currentPersonality.getName() + "' is missing the main.js script");
        }

        TeaseAI.application.getSession().end();
    }

    public void evalScript(String scriptName) {
        if(!scriptName.toLowerCase().endsWith(".js")) {
            scriptName += ".js";
        }

        File script = FileUtils.getRandomMatchingFile(currentPersonality.getFolder().getAbsolutePath() + File.separator + scriptName);

        if(script == null || !script.exists()) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Script " + scriptName + " does not exist.");
            return;
        }

        try {
            runScript(script);
        } catch (FileNotFoundException e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Script " + scriptName + " does not exist.");
        }
    }

    public void runScript(File script) throws FileNotFoundException {
        try {
            this.currentFile = script;
            engine.eval(new FileReader(script));
        } catch (ScriptException e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Latest loaded file was '" + currentFile.getPath() + "' and error was found in line " + e.getLineNumber() + "\n" +
                    "Error: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public ScriptEngine getEngine() {
        return engine;
    }

    public static ScriptHandler getHandler() {
        return handler;
    }
}
