package me.goddragon.teaseai;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.chat.response.Response;
import me.goddragon.teaseai.api.chat.response.ResponseHandler;
import me.goddragon.teaseai.api.config.ConfigHandler;
import me.goddragon.teaseai.api.config.ConfigValue;
import me.goddragon.teaseai.api.media.MediaCollection;
import me.goddragon.teaseai.api.media.MediaFetishType;
import me.goddragon.teaseai.api.scripts.ScriptHandler;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;
import me.goddragon.teaseai.api.session.Session;
import me.goddragon.teaseai.gui.main.Controller;

/**
 * Created by GodDragon on 21.03.2018.
 */
public class TeaseAI extends Application {

    public static TeaseAI application;
    private ConfigHandler configHandler = new ConfigHandler("TeaseAI.properties");
    private MediaCollection mediaCollection;
    private Controller controller;
    private Thread mainThread;
    public Thread scriptThread;

    public final ConfigValue PREFERRED_SESSION_DURATION = new ConfigValue("preferredSessionDuration", "60", configHandler);
    public final ConfigValue LAST_SELECTED_PERSONALITY = new ConfigValue("lastSelectedPersonality", "null", configHandler);

    private Session session;

    @Override
    public void start(Stage primaryStage) throws Exception {
        application = this;
        mainThread = Thread.currentThread();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gui/main/main.fxml"));
        controller = new Controller(primaryStage);
        loader.setController(controller);
        Parent root = loader.load();
        primaryStage.setTitle("Tease-AI");
        primaryStage.setScene(new Scene(root, 1480, 720));
        primaryStage.show();
        controller.initiate();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                try {
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //Load values to add the config values
        MediaFetishType.values();

        //Load config values first
        configHandler.loadConfig();

        ChatHandler.getHandler().load();

        this.mediaCollection = new MediaCollection();

        ScriptHandler.getHandler().load();
        PersonalityManager.getManager().loadPersonalities();

        initializeNewSession();

        controller.loadDomInfo();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public boolean checkForNewResponses() {
        if (Thread.currentThread() != scriptThread) {
            throw new IllegalStateException("Can only check for new responses on the script thread");
        }

        Response queuedResponse = ResponseHandler.getHandler().getLatestQueuedReponse();
        if (queuedResponse != null) {
            ResponseHandler.getHandler().removeQueuedReponse(queuedResponse);
            return queuedResponse.trigger();
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

    public void sleepScripThread(long sleepMillis) {
        sleepThread(scriptThread, sleepMillis);
    }

    public void waitScriptThread(long timeoutMillis) {
        waitThread(scriptThread, timeoutMillis);
    }

    public void waitThread(Thread thread) {
        waitThread(thread, 0);
    }

    public void waitThread(Thread thread, long timeoutMillis) {
        synchronized (thread) {
            try {
                thread.wait(timeoutMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sleepThread(Thread thread, long sleepMillis) {
        synchronized (thread) {
            try {
                thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Controller getController() {
        return controller;
    }

    public void initializeNewSession() {
        this.session = new Session();

        session.setActivePersonality((Personality) controller.getPersonalityChoiceBox().getSelectionModel().getSelectedItem());
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
}
