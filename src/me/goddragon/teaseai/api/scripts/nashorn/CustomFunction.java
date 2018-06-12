package me.goddragon.teaseai.api.scripts.nashorn;

import jdk.nashorn.api.scripting.AbstractJSObject;
import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;

/**
 * Created by GodDragon on 25.03.2018.
 */
public abstract class CustomFunction extends AbstractJSObject {
    protected final String functionName;

    protected final Collection<String> functionNames = new ArrayList();

    public CustomFunction(String... functionName) {
        this.functionName = functionName[0];
        functionNames.addAll(Arrays.asList(functionName));
    }

    public Collection<String> getFunctionNames() {
        return functionNames;
    }

    public String getFunctionName() {
        return functionName;
    }

    @Override
    public Object call(Object object, Object... args) {
        if(TeaseAI.application.getSession().isHaltSession()) {
            synchronized(TeaseAI.application.scriptThread) {
                //Restore the previous state of the start button and set the new session
                TeaseAI.application.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        TeaseAI.application.getController().getStartChatButton().setText("Start");
                        TeaseAI.application.getController().getStartChatButton().setDisable(false);
                        TeaseAI.application.getController().getPersonalityChoiceBox().setDisable(false);
                        TeaseAI.application.initializeNewSession();

                        //Clear chat
                        TeaseAI.application.getController().getChatWindow().getChildren().clear();
                    }
                });

                while(true) {
                    try {
                        if(TeaseAI.application.scriptThread == Thread.currentThread()) {
                            Thread.sleep(10000000);
                        } else {
                            TeaseLogger.getLogger().log(Level.SEVERE, "Checked for forced session end in other thread than script thread.");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //Check whether there are new responses to handle
        TeaseAI.application.checkForNewResponses();
        return null;
    }
}
