package me.goddragon.teaseai.api.runnable;

/**
 * Created by GodDragon on 12.06.2018.
 */
public abstract class TeaseRunnable implements Runnable {

    private boolean running = false;

    private int runs = 0;

    private long delay;
    private long interval;

    private long lastCall;

    public void runLater(long delay) {
        this.delay = delay;
        this.lastCall = System.currentTimeMillis();
        TeaseRunnableHandler.getHandler().addRunnable(this);
    }

    public void runTimer(long delay, long interval) {
        this.delay = delay;
        this.interval = interval;
        this.lastCall = System.currentTimeMillis();
        TeaseRunnableHandler.getHandler().addRunnable(this);
    }

    public boolean tryRun() {
        //Check whether we are running it right now
        if(running) {
            return false;
        }

        boolean willRun = false;
        if(runs == 0) {
            if (System.currentTimeMillis() - lastCall >= delay) {
                willRun = true;
            }
        } else if(System.currentTimeMillis() - lastCall >= interval) {
            willRun = true;
        }

        if(willRun) {
            System.out.println("Passed since last run " + (System.currentTimeMillis() - lastCall));
            this.running = true;
            runs++;

            //Remove if we are supposed to only run this once
            if(interval <= 0) {
                TeaseRunnableHandler.getHandler().removeRunnable(this);
            }

            run();

            this.lastCall = System.currentTimeMillis();
            this.running = false;
        }

        return willRun;
    }


    public void end() {
        cancel();
    }

    public void cancel() {
        TeaseRunnableHandler.getHandler().removeRunnable(this);
    }
}
