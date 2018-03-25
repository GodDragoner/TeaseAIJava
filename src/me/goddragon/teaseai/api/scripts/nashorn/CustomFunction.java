package me.goddragon.teaseai.api.scripts.nashorn;

import jdk.nashorn.api.scripting.AbstractJSObject;

/**
 * Created by GodDragon on 25.03.2018.
 */
public abstract class CustomFunction extends AbstractJSObject {
    protected final String functionName;

    public CustomFunction(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionName() {
        return functionName;
    }
}
