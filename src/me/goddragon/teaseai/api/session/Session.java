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
        
        activePersonality.getVariableHandler().setVariable("fetishesAnal", TeaseAI.application.FETISH_ANAL.toString(), true);
        activePersonality.getVariableHandler().setVariable("fetishesBallTorture", TeaseAI.application.FETISH_BALLTORTURE.toString(), true);
        activePersonality.getVariableHandler().setVariable("fetishesBDSMPositions", TeaseAI.application.FETISH_BDSMPOSITIONS.toString(), true);
        activePersonality.getVariableHandler().setVariable("fetishesBladderControl", TeaseAI.application.FETISH_BLADDERCONTROL.toString(), true);
        activePersonality.getVariableHandler().setVariable("fetishesBondage", TeaseAI.application.FETISH_BONDAGE.toString(), true);
        activePersonality.getVariableHandler().setVariable("fetishesCockSucking", TeaseAI.application.FETISH_COCKSUCKING.toString(), true);
        activePersonality.getVariableHandler().setVariable("fetishesCockTorture", TeaseAI.application.FETISH_COCKTORTURE.toString(), true);
        activePersonality.getVariableHandler().setVariable("fetishesCumEating", TeaseAI.application.FETISH_CUMEATING.toString(), true);
        activePersonality.getVariableHandler().setVariable("fetishesExercise", TeaseAI.application.FETISH_EXERCISE.toString(), true);
        activePersonality.getVariableHandler().setVariable("fetishesFeet", TeaseAI.application.FETISH_FEET.toString(), true);
        activePersonality.getVariableHandler().setVariable("fetishesNippleTorture", TeaseAI.application.FETISH_NIPPLETORTURE.toString(), true);
        activePersonality.getVariableHandler().setVariable("fetishesSelfFellatio", TeaseAI.application.FETISH_SELFFELLATIO.toString(), true);
        activePersonality.getVariableHandler().setVariable("fetishesSissy", TeaseAI.application.FETISH_SISSY.toString(), true);
        

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
        TeaseAI.application.FETISH_ANAL.setValue(activePersonality.getVariableHandler().getVariableValue("fetishesAnal").toString());
        TeaseAI.application.FETISH_BALLTORTURE.setValue(activePersonality.getVariableHandler().getVariableValue("fetishesBallTorture").toString());
        TeaseAI.application.FETISH_BDSMPOSITIONS.setValue(activePersonality.getVariableHandler().getVariableValue("fetishesBDSMPositions").toString());
        TeaseAI.application.FETISH_BLADDERCONTROL.setValue(activePersonality.getVariableHandler().getVariableValue("fetishesBladderControl").toString());
        TeaseAI.application.FETISH_BODYMARKING.setValue(activePersonality.getVariableHandler().getVariableValue("fetishesBodyMarking").toString());
        TeaseAI.application.FETISH_BONDAGE.setValue(activePersonality.getVariableHandler().getVariableValue("fetishesBondage").toString());
        TeaseAI.application.FETISH_COCKSUCKING.setValue(activePersonality.getVariableHandler().getVariableValue("fetishesCockSucking").toString());
        TeaseAI.application.FETISH_COCKTORTURE.setValue(activePersonality.getVariableHandler().getVariableValue("fetishesCockTorture").toString());
        TeaseAI.application.FETISH_CUMEATING.setValue(activePersonality.getVariableHandler().getVariableValue("fetishesCumEating").toString());
        TeaseAI.application.FETISH_EXERCISE.setValue(activePersonality.getVariableHandler().getVariableValue("fetishesExercise").toString());
        TeaseAI.application.FETISH_FEET.setValue(activePersonality.getVariableHandler().getVariableValue("fetishesFeet").toString());
        TeaseAI.application.FETISH_NIPPLETORTURE.setValue(activePersonality.getVariableHandler().getVariableValue("fetishesNippleTorture").toString());
        TeaseAI.application.FETISH_SELFFELLATIO.setValue(activePersonality.getVariableHandler().getVariableValue("fetishesSelfFellatio").toString());
        TeaseAI.application.FETISH_SISSY.setValue(activePersonality.getVariableHandler().getVariableValue("fetishesSissy").toString());
        
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
