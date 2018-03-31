package me.goddragon.teaseai.api.session;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.scripts.ScriptHandler;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;

/**
 * Created by GodDragon on 26.03.2018.
 */
public class Session {
    private Personality activePersonality = null;
    private long startedAt;

    public void start() {
        startedAt = System.currentTimeMillis();

        TeaseAI.application.scriptThread = new Thread() {
            @Override
            public void run() {
                ScriptHandler.getHandler().startPersonality(PersonalityManager.getManager().getActivePersonality());

                /*ChatParticipant dom = ChatHandler.getHandler().getMainDomParticipant();
                VocabularyHandler.getHandler().registerVocabulary("tree", "%tree2%");
                VocabularyHandler.getHandler().registerVocabulary("tree2", "tree", "wood", "cool", "test");

                ResponseHandler.getHandler().registeResponse(new Response("fuck me", "lick me", "ShiT me") {
                    @Override
                    public boolean trigger() {
                        dom.sendMessage("Oh really?");
                        dom.sendMessage("Nice try!");
                        dom.sendMessage("Done!");
                        return true;
                    }
                });

                Answer answer = dom.sendInput("Hey %tree%");
                while (true) {
                    System.out.println(answer.getAnswer());
                    if (answer.matchesRegexLowerCase("hey([ ]|$)", "hello([ ]|$)", "hi([ ]|$)")) {
                        break;
                    } else {
                        dom.sendMessage("What?");
                        answer.loop();
                    }
                }

                answer = dom.sendInput("How do you feel today?", 10);
                while (true) {
                    if (answer.isLike("Good", "You", "Great") || answer.isTimeout()) {
                        break;
                    } else {
                        dom.sendMessage("What?");
                        answer.loop();
                    }
                }*/

                /*synchronized (this) {
                    while(session.getActivePersonality() == null) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                session.start();*/
            }
        };

        TeaseAI.application.scriptThread.start();
    }

    public long getStartedAt() {
        return startedAt;
    }

    public long getRuntime() {
        return System.currentTimeMillis() - startedAt;
    }

    public void setActivePersonality(Personality activePersonality) {
        this.activePersonality = activePersonality;
    }

    public Personality getActivePersonality() {
        return activePersonality;
    }
}
