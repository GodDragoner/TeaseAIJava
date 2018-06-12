package me.goddragon.teaseai.api.runnable;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by GodDragon on 12.06.2018.
 */
public class TeaseRunnableHandler {

    private static final TeaseRunnableHandler handler = new TeaseRunnableHandler();

    private final Collection<TeaseRunnable> runnables = new HashSet<>();

    private final Collection<TeaseRunnable> toRemoveRunnables = new HashSet<>();

    public void checkRunnables() {
        //Remove all runnables that are scheduled to be removed
        for(TeaseRunnable runnable : toRemoveRunnables) {
            runnables.remove(runnable);
        }

        toRemoveRunnables.clear();

        for (TeaseRunnable runnable : runnables) {
            runnable.tryRun();
        }
    }

    protected void addRunnable(TeaseRunnable runnable) {
        runnables.add(runnable);
    }

    protected void removeRunnable(TeaseRunnable runnable) {
        toRemoveRunnables.add(runnable);
    }

    public static TeaseRunnableHandler getHandler() {
        return handler;
    }
}
