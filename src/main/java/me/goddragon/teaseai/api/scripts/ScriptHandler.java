package me.goddragon.teaseai.api.scripts;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.response.ResponseHandler;
import me.goddragon.teaseai.api.chat.vocabulary.VocabularyHandler;
import me.goddragon.teaseai.api.scripts.nashorn.*;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;
import me.goddragon.teaseai.api.statistics.JavaModule;
import me.goddragon.teaseai.api.statistics.StatisticsManager;
import me.goddragon.teaseai.utils.FileUtils;
import me.goddragon.teaseai.utils.TeaseLogger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Level;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class ScriptHandler {

    private static ScriptHandler handler = new ScriptHandler();
    private ScriptEngine engine;

    private Personality currentPersonality;

    private File currentFile;

    public void load() {
        registerFunction(new SendMessageFunction());
        registerFunction(new DebugMessageFunction());
        registerFunction(new WarningMessageFunction());
        registerFunction(new ErrorMessageFunction());
        registerFunction(new EstimAPIFunction());
        registerFunction(new AddSettingsPanelFunction());
        registerFunction(new AddCheckBoxFunction());
        registerFunction(new AddTextBoxFunction());
        registerFunction(new AddOptionsListFunction());
        registerFunction(new AddSpinnerFunction());
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
        registerFunction(new RegisterSupportedVariableFunction());
        registerFunction(new SetTypeSpeedFunction());
        registerFunction(new GetTypeSpeedFunction());
        registerFunction(new CreateMediaURLFromFileFunction());
        registerFunction(new GetCurrentImageURLFunction());
        registerFunction(new OpenLinkInBrowserFunction());
        registerFunction(new LoadCSSFunction());
        registerFunction(new RandomIntegerFunction());
        registerFunction(new RandomDoubleFunction());
        registerFunction(new SelectRandomFunction());
        registerFunction(new RunOnGuiThreadFunction());
        registerFunction(new WakeScriptThreadFunction());
        registerFunction(new RunFunction());
        registerFunction(new ShowCategoryVideo());
        registerFunction(new GetStrokingBPMFunction());
        registerFunction(new SetTextToSpeechFunction());
        registerFunction(new SystemMessageFunction());
        registerFunction(new SetResponseIgnoreDisabledFunction());
        registerFunction(new IgnoreCurrentModuleFunction());
        registerFunction(new ToggleModuleDetectionFunction());
        registerFunction(new ToggleEdgeDetectionFunction());
        registerFunction(new ToggleEdgeHoldDetectionFunction());
        registerFunction(new ToggleStrokeDetectionFunction());
        registerFunction(new SetEdgeHoldFunction());
        registerFunction(new AddEdgeStatisticFunction());
        registerFunction(new AddModuleStatisticFunction());
        registerFunction(new AddStrokeStatisticFunction());
        registerFunction(new GetThisSessionStatisticsFunction());
    }

    public void registerFunction(CustomFunction function) {
        for (String functionName : function.getFunctionNames()) {
            engine.put(functionName, function);
        }
    }

    public void startPersonality(Personality personality) {
        File mainScript = new File(personality.getFolder().getAbsolutePath() + File.separator + "main.js");

        if (!mainScript.isFile() || !mainScript.exists()) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Personality '" + currentPersonality.getName() + "' is missing the main.js script");
        } else {
            startPersonality(personality, mainScript);
        }

        TeaseAI.application.getSession().end();
    }

    public void startPersonality(Personality personality, File startScript) {
        //Reassign because we want to clear the catch
        this.engine = new ScriptEngineManager().getEngineByName("nashorn");
        ScriptHandler.getHandler().load();

        this.currentPersonality = personality;

        currentPersonality.getVariableHandler().setVariable("personalityVersion", currentPersonality.getVersion().getValue(), true);

        VocabularyHandler.getHandler().loadVocabulariesFromPersonality(personality);
        ResponseHandler.getHandler().loadResponsesFromPersonality(personality);


        try {
            runScript(startScript);
        } catch (FileNotFoundException e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Tried to run non-existent script '" + startScript.getName() + "'.");
        }

        TeaseAI.application.getSession().end();
    }

    public void evalScript(String scriptName) {
        if (!scriptName.toLowerCase().endsWith(".js")) {
            scriptName += ".js";
        }

        Personality personality = currentPersonality;
        if (personality == null) {
            personality = PersonalityManager.getManager().getLoadingPersonality();
        }
        File script = FileUtils.getRandomMatchingFile(personality.getFolder().getAbsolutePath() + File.separator + scriptName);
        if (TeaseAI.application.getSession() != null && StatisticsManager.moduleDetection)
            TeaseAI.application.getSession().statisticsManager.addModule(script.getName());

        if (script == null || !script.exists()) {
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

            boolean resetEngine = false;
            if (engine == null) {
                //Create a instance for the current purpose
                this.engine = new ScriptEngineManager().getEngineByName("nashorn");
                ScriptHandler.getHandler().load();
                resetEngine = true;
            }
            engine.eval(new FileReader(script));

            //Reset the engine again because we only created it temporarily
            if (resetEngine) {
                this.engine = null;
            }
        } catch (ScriptException e) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Latest loaded file was '" + currentFile.getPath() + "' and error was found in line " + e.getLineNumber() + "\n" +
                    "Error: " + e.getMessage(), false);
            if (TeaseAI.application.getSession() != null) {
                JavaModule current = TeaseAI.application.getSession().statisticsManager.getCurrentModule();
                if (current != null) {
                    current.setErrored(true);
                }
            }
            e.printStackTrace();
        }
    }

    public File getPersonalityFile(String fileName) {
        File file = FileUtils.getRandomMatchingFile(currentPersonality.getFolder().getAbsolutePath() + File.separator + fileName);

        if (file == null || !file.exists()) {
            TeaseLogger.getLogger().log(Level.SEVERE, "File " + fileName + " does not exist.");
            return null;
        }

        return file;
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
