package me.goddragon.teaseai.api.session;

import me.goddragon.teaseai.utils.Metronome;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.logging.Level;

/**
 * Created by GodDragon on 02.04.2018.
 */
public class StrokeHandler {

    private static StrokeHandler handler = new StrokeHandler();

    private volatile Metronome metronome;
    private Thread waitingThread;
    private int currentBPM;

    public void startMetronome(int bpm, int durationSeconds) {
        if(bpm <= 0) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Tried to set metronome bpm to 0 or lower.");
            return;
        }

        stopMetronome();

        currentBPM = bpm;

        (metronome = new Metronome()).start(bpm);

        if(durationSeconds > 0) {
            (waitingThread = new Thread() {
                Metronome watchingMetronome = metronome;
                @Override
                public void run() {
                    synchronized (this) {
                        try {
                            wait(durationSeconds*1000);

                            //Check if we are still dealing with the metronome that we were watching
                            if(watchingMetronome == metronome) {
                                metronome.stop();
                                waitingThread = null;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    public void stopMetronome() {
        if(metronome != null) {
            metronome.stop();
            metronome = null;

            //Check if we are waiting for stopping the metronome
            if(waitingThread != null) {
                waitingThread.notify();
                waitingThread = null;
            }
        }
    }

    public boolean isStroking() {
        return metronome != null;
    }

    public int getCurrentBPM() {
        return currentBPM;
    }

    public static StrokeHandler getHandler() {
        return handler;
    }

    public static void setHandler(StrokeHandler handler) {
        StrokeHandler.handler = handler;
    }
}
