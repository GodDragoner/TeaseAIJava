package me.goddragon.teaseai.api.scripts.nashorn;

import jdk.nashorn.api.scripting.AbstractJSObject;
import me.goddragon.teaseai.TeaseAI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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
        //Only check if the session already started and we are on the script thread
        if(TeaseAI.application.getSession().isStarted() && Thread.currentThread() == TeaseAI.application.scriptThread) {
            TeaseAI.application.getSession().checkForInteraction();
        }

        return null;
    }
}
