package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.config.VariableHandler;
import me.goddragon.teaseai.api.scripts.personality.Personality;
import me.goddragon.teaseai.api.scripts.personality.PersonalityManager;
import me.goddragon.teaseai.api.session.Session;

/**
 * Created by GodDragon on 25.03.2018.
 */
public class SetVarFunction extends CustomFunctionExtended {
    public SetVarFunction() {
        super("setVar", "setVariable", "setFlag");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    protected Object onCall(String variableName, Object value) {
        final VariableHandler variableHandler = getVariableHandler();
        return variableHandler.setVariable(variableName, value);
    }

    private VariableHandler getVariableHandler() {
        final Session session = TeaseAI.application.getSession();
        Personality personality;
        if (session == null) {
            personality = PersonalityManager.getManager().getLoadingPersonality();
        } else {
            personality = session.getActivePersonality();
        }

        return personality.getVariableHandler();
    }
}
