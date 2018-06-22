package me.goddragon.teaseai.api.session;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.ChatHandler;
import me.goddragon.teaseai.api.chat.ChatParticipant;
import me.goddragon.teaseai.api.chat.SenderType;
import me.goddragon.teaseai.api.config.TeaseDate;
import me.goddragon.teaseai.api.runnable.TeaseRunnableHandler;
import me.goddragon.teaseai.api.scripts.ScriptHandler;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.logging.Level;

/**
 * Created by GodDragon on 26.03.2018.
 */
public class Session {
    private Personality activePersonality = null;
    private boolean started = false;
    private boolean haltSession = false;
    private long startedAt;

    public void start() {
        startedAt = System.currentTimeMillis();
        started = true;

        activePersonality.getVariableHandler().setVariable("startDate", new TeaseDate(startedAt), true);
        activePersonality.getVariableHandler().setVariable("subName", ChatHandler.getHandler().getSubParticipant().getName(), true);
        activePersonality.getVariableHandler().setVariable("domName", ChatHandler.getHandler().getMainDomParticipant().getName(), true);
        activePersonality.getVariableHandler().setVariable("domFriend1Name", ChatHandler.getHandler().getParticipantById(2).getName(), true);
        activePersonality.getVariableHandler().setVariable("domFriend2Name", ChatHandler.getHandler().getParticipantById(3).getName(), true);
        activePersonality.getVariableHandler().setVariable("domFriend3Name", ChatHandler.getHandler().getParticipantById(4).getName(), true);
        activePersonality.getVariableHandler().setVariable("prefSessionLength", TeaseAI.application.PREFERRED_SESSION_DURATION.getInt(), true);

        TeaseAI.application.getController().getChatWindow().getChildren().clear();

        TeaseAI.application.scriptThread = new Thread() {
            @Override
            public void run() {
                ScriptHandler.getHandler().startPersonality(PersonalityManager.getManager().getActivePersonality());
            }
        };

        TeaseAI.application.scriptThread.start();
    }

    public void checkForInteraction() {
        checkForForcedEnd();

        //Check for runnables
        TeaseRunnableHandler.getHandler().checkRunnables();

        //Check whether there are new responses to handle
        TeaseAI.application.checkForNewResponses();

        //Check for runnables -- twice because responses might take some time
        TeaseRunnableHandler.getHandler().checkRunnables();
    }

    public void checkForForcedEnd() {
        if(TeaseAI.application.getSession().isHaltSession()) {
            if(TeaseAI.application.scriptThread == Thread.currentThread()) {
                synchronized (TeaseAI.application.scriptThread) {
                    TeaseAI.application.getSession().end();

                    while (true) {
                        try {
                            Thread.sleep(10000000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            } else {
                TeaseLogger.getLogger().log(Level.SEVERE, "Checked for forced session end in other thread than script thread.");
            }
        }
    }

    public void end() {
        //Restore the previous state of the start button and set the new session
        TeaseAI.application.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                TeaseAI.application.getController().getStartChatButton().setText("Start");
                TeaseAI.application.getController().getStartChatButton().setDisable(false);
                TeaseAI.application.getController().getPersonalityChoiceBox().setDisable(false);

                //Reset lazy sub interface
                TeaseAI.application.getController().getLazySubController().clear();
                TeaseAI.application.getController().getLazySubController().createDefaults();

                //Clear chat
                TeaseAI.application.getController().getChatWindow().getChildren().clear();
                
                //Clear css
                TeaseAI.application.getScene().getStylesheets().clear();

                //Initialize a new session instance
                TeaseAI.application.initializeNewSession();
            }
        });

        //Restore default type speed
        for(ChatParticipant participant : ChatHandler.getHandler().getSenders().values()) {
            if(participant.getType() != SenderType.SUB) {
                participant.setTypeSpeed(ChatHandler.getHandler().getTypeSpeed());
            }
        }
    }

    public long getStartedAt() {
        return startedAt;
    }

    public long getRuntime() {
        return System.currentTimeMillis() - startedAt;
    }

    public void setActivePersonality(Personality activePersonality) {
        if(this.activePersonality != null) {
            activePersonality.unload();
        }

        this.activePersonality = activePersonality;
        activePersonality.load();
    }

    public Personality getActivePersonality() {
        return activePersonality;
    }

    public boolean isHaltSession() {
        return haltSession;
    }

    public void setHaltSession(boolean haltSession) {
        this.haltSession = haltSession;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
}
