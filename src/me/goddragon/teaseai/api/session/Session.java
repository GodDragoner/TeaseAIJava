package me.goddragon.teaseai.api.session;

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
        ScriptHandler.getHandler().startPersonality(PersonalityManager.getManager().getActivePersonality());
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
