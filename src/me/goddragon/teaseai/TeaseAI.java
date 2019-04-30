package me.goddragon.teaseai;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.chat.TypeSpeed;
import me.goddragon.teaseai.api.chat.response.Response;
import me.goddragon.teaseai.api.chat.response.ResponseHandler;
import me.goddragon.teaseai.api.config.ConfigHandler;
import me.goddragon.teaseai.api.config.ConfigValue;
import me.goddragon.teaseai.api.config.PersonalitiesSettingsHandler;
import me.goddragon.teaseai.api.media.MediaCollection;
import me.goddragon.teaseai.api.media.MediaFetishType;
import me.goddragon.teaseai.api.media.MediaHandler;
import me.goddragon.teaseai.api.runnable.TeaseRunnableHandler;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;
import me.goddragon.teaseai.api.session.Session;
import me.goddragon.teaseai.api.session.StrokeHandler;
import me.goddragon.teaseai.gui.ProgressForm;
import me.goddragon.teaseai.gui.StartupProgressPane;
import me.goddragon.teaseai.gui.main.MainGuiController;
import me.goddragon.teaseai.gui.settings.AppearanceSettings;
import me.goddragon.teaseai.utils.TeaseLogger;
import me.goddragon.teaseai.utils.update.UpdateHandler;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Created by GodDragon on 21.03.2018.
 */
public class TeaseAI extends Application {

    public static final String VERSION = "1.1";
    public static final String UPDATE_FOLDER = "Updates";


    public static TeaseAI application;
    private ConfigHandler configHandler = new ConfigHandler("TeaseAI.properties");
    private MediaCollection mediaCollection;
    private MainGuiController controller;
    private Thread mainThread;
    private Stage primaryStage;
    public Thread scriptThread;

    public StartupProgressPane startupProgressPane;
    private Scene mainScene;

    public final ConfigValue PREFERRED_SESSION_DURATION = new ConfigValue("preferredSessionDuration", "60", configHandler);
    public final ConfigValue CHAT_TEXT_SIZE = new ConfigValue("chatTextSize", Font.getDefault().getSize(), configHandler);
    public final ConfigValue DEFAULT_TYPE_SPEED = new ConfigValue("defaultTypeSpeed", TypeSpeed.MEDIUM, configHandler);
    public final ConfigValue LAST_SELECTED_PERSONALITY = new ConfigValue("lastSelectedPersonality", "null", configHandler);
    public final ConfigValue TEASE_AI_PROPERTIES_LINK = new ConfigValue("teaseAIPropertiesLink", "https://gist.githubusercontent.com/GodDragoner/6c7193903cb0695ff891e8468ad279cd/raw/TeaseAI.properties", configHandler);
    public final ConfigValue TEXT_TO_SPEECH = new ConfigValue("texttospeech", 2, configHandler);
    public final ConfigValue DEBUG_MODE = new ConfigValue("debugmode", false, configHandler);

    private Session session;
    public boolean TextToSpeechEnabled = false;
    public boolean responsesDisabled = false;


    @Override
    public void start(Stage primaryStage) throws Exception {
        if (Main.JAVA_VERSION < 10) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Old Java Version Detected");
            alert.setHeaderText(null);
            alert.setContentText("You are using java version " + Main.JAVA_VERSION + " which is not supported. Please use Java 10 or higher. This program will close now.");
            alert.showAndWait();
            return;
        }

        TeaseLogger.getLogger().log(Level.INFO, "Launching TAJ " + VERSION);

        //Will allow us to use ecma6 language
        System.setProperty("nashorn.args", "--language=es6");

        this.application = this;
        this.mainThread = Thread.currentThread();
        this.primaryStage = primaryStage;
        this.startupProgressPane = new StartupProgressPane();

        //Load config values first
        configHandler.loadConfig();

        ProgressForm progressForm = new ProgressForm("Checking for TAJ update...");
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws InterruptedException {
                UpdateHandler.getHandler().checkForUpdate();

                progressForm.setNameSync("Checking personalities...");
                PersonalityManager.getManager().setProgressUpdate((workDone, totalWork) ->
                        updateProgress(workDone, totalWork));

                PersonalityManager.getManager().loadPersonalities();

                TeaseAI.application.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TeaseAI.application.finishedCheckup(progressForm);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                return null;
            }
        };

        progressForm.bindProgressBar(task);
        startupProgressPane.addProgressBar(progressForm);
        startupProgressPane.show();

        new PersonalitiesSettingsHandler();
        //task.setOnSucceeded(event -> startupProgressPane.getDialogStage().close());

        Thread thread = new Thread(task);
        thread.start();
        if (TEXT_TO_SPEECH.getInt() == 1) {
            setTTS(true);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void finishedCheckup(ProgressForm progressForm) throws IOException {
        //startupProgressPane.close();

        progressForm.setNameSync("Setting up GUI...");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/main/main.fxml"));
        controller = new MainGuiController(primaryStage);
        loader.setController(controller);
        Parent root = loader.load();
        primaryStage.setTitle("Tease-AI " + VERSION);
        mainScene = new Scene(root, 1480, 720);

        //So we can apply our themes to all relevant GUIs at once
        controller.addMainScene(mainScene);

        primaryStage.setScene(mainScene);
        controller.initiate();

        primaryStage.setOnCloseRequest(event -> {
            try {
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                //Load values to add the config values
                MediaFetishType.values();

                progressForm.setNameSync("Loading picture sets...");

                PersonalityManager.getManager().setProgressUpdate((workDone, totalWork) ->
                        updateProgress(workDone, totalWork));

                ChatHandler.getHandler().load();

                progressForm.setNameSync("Finishing startup...");

                TeaseAI.application.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        primaryStage.show();

                        TeaseAI.this.mediaCollection = new MediaCollection();

                        PersonalityManager.getManager().addPersonalitiesToGUI();

                        initializeNewSession();

                        controller.loadDomInfo();

                        //Load theme last so everything is setup
                        AppearanceSettings.loadSelectedTheme();

                        //Reload lazy sub because it is somehow messed up
                        TeaseAI.application.getController().getLazySubController().clear();
                        TeaseAI.application.getController().getLazySubController().createDefaults();
                    }
                });

                return null;
            }
        };

        progressForm.bindProgressBar(task);
        task.setOnSucceeded(event -> startupProgressPane.getDialogStage().close());
        Thread thread = new Thread(task);
        thread.start();
        //startupProgressPane.addProgressBar(progressForm);
    }

    public boolean checkForNewResponses() {
        if (Thread.currentThread() != scriptThread) {
            throw new IllegalStateException("Can only check for new responses on the script thread");
        }

        //Repeat for all queued responses
        while (true) {
            Response queuedResponse = ResponseHandler.getHandler().getLatestQueuedResponse();

            if (queuedResponse != null) {
                ResponseHandler.getHandler().removeQueuedResponse(queuedResponse);
                if (queuedResponse.trigger()) {
                    return true;
                }
            } else {
                break;
            }
        }

        return false;
    }

    public void runOnUIThread(Runnable runnable) {
        //If we are not on the main thread, run it later on it
        if (Thread.currentThread() != mainThread) {
            Platform.runLater(runnable);
        } else {
            //We can safely run the runnable because we are on the main thread
            runnable.run();
        }
    }

    public void waitPossibleScripThread(long timeoutMillis) {
        waitThread(timeoutMillis);

        //Let's check whether we are supposed to force the session to end
        if (Thread.currentThread() == scriptThread) {
            session.checkForForcedEnd();
        }
    }

    public void sleepPossibleScripThread(long sleepMillis) {
        sleepPossibleScripThread(sleepMillis, false);
    }

    public void sleepPossibleScripThread(long sleepMillis, boolean runnablesOnly) {
        if (Thread.currentThread() != scriptThread) {
            sleepThread(sleepMillis);
        } else {
            long startedAt = System.currentTimeMillis();
            long millisPerInterval = 100;
            while (startedAt + sleepMillis > System.currentTimeMillis()) {
                sleepThread(millisPerInterval);

                //Check for new stuff
                if (!runnablesOnly) {
                    //Only check if the session already started
                    if (session.isStarted()) {
                        session.checkForInteraction();
                    }
                } else {
                    TeaseRunnableHandler.getHandler().checkRunnables();
                }
            }
        }
    }

    public void sleepScripThread(long sleepMillis) {
        if (Thread.currentThread() != scriptThread) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Tried to sleep script thread from other thread.");
            return;
        }

        sleepThread(sleepMillis);
    }

    public void waitScriptThread(long timeoutMillis) {
        if (Thread.currentThread() != scriptThread) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Tried to wait script thread from other thread.");
            return;
        }

        waitThread(timeoutMillis);
    }

    public void waitThread() {
        waitThread(0);
    }

    public void waitThread(long timeoutMillis) {
        synchronized (Thread.currentThread()) {
            try {
                Thread.currentThread().wait(timeoutMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sleepThread(long sleepMillis) {
        synchronized (Thread.currentThread()) {
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public MainGuiController getController() {
        return controller;
    }

    public void initializeNewSession() {
        this.session = new Session();

        //End everything such as metronome and stroking
        StrokeHandler.getHandler().setEdging(false);
        StrokeHandler.getHandler().setOnEdge(false);
        StrokeHandler.getHandler().stopMetronome();

        //Show no picture
        MediaHandler.getHandler().showPicture(null);

        session.setActivePersonality((Personality) controller.getPersonalityChoiceBox().getSelectionModel().getSelectedItem());

        //Reset the temporary variables
        session.getActivePersonality().getVariableHandler().clearTemporaryVariables();
        AppearanceSettings.loadSelectedTheme();
    }

    public void setTTS(boolean value) {
        TextToSpeechEnabled = value;
    }

    public Thread getScriptThread() {
        return scriptThread;
    }

    public Thread getMainThread() {
        return mainThread;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public MediaCollection getMediaCollection() {
        return mediaCollection;
    }

    public Session getSession() {
        return session;
    }

    public Scene getScene() {
        return mainScene;
    }

    public static TeaseAI getApplication() {
        return application;
    }
}
