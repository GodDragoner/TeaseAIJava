package me.goddragon.teaseai.api.scripts.nashorn;

import me.goddragon.teaseai.TeaseAI;

/**
 * Created by GodDragon on 09.10.2018.
 */
public class WakeScriptThreadFunction extends CustomFunction {

    public WakeScriptThreadFunction() {
        super("notifyScript", "wakeScript");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        synchronized (TeaseAI.application.scriptThread) {
            TeaseAI.application.scriptThread.notify();
        }

        return null;
    }
}
