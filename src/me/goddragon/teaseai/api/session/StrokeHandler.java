package me.goddragon.teaseai.api.session;

import me.goddragon.teaseai.TeaseAI;
import me.goddragon.teaseai.api.chat.response.Response;
import me.goddragon.teaseai.api.chat.response.ResponseHandler;
import me.goddragon.teaseai.api.statistics.JavaEdge;
import me.goddragon.teaseai.api.statistics.JavaStroke;
import me.goddragon.teaseai.api.statistics.StatisticsManager;
import me.goddragon.teaseai.utils.Metronome;
import me.goddragon.teaseai.utils.EstimMetronome;
import me.goddragon.teaseai.utils.TeaseLogger;

import java.util.logging.Level;

/**
 * Created by GodDragon on 02.04.2018.
 */
public class StrokeHandler {

    public static final Response EDGE_RESPONSE = new Response("edge") {
        @Override
        public boolean trigger() {
            StrokeHandler.getHandler().setEdging(false);
            ResponseHandler.getHandler().unregisterResponse(this);
            return true;
        }
    };

    private static StrokeHandler handler = new StrokeHandler();

    private boolean isEdging;
    private boolean isStroking;
    private boolean isHolding;

    private volatile Metronome metronome;
    private volatile EstimMetronome estimMetronome;
    private Thread waitingThread;
    private int currentBPM;

    public void startStroking(int bpm, int durationSeconds) {
        setStroking(true);
        startMetronome(bpm, durationSeconds);
    }

    public void startEdging(int bpm) {
        setEdging(true);
        if (bpm > 0)
            startMetronome(bpm, 0);
    }

    public boolean isEdgeHolding() {
        return isHolding;
    }

    public void startMetronome(int bpm, int durationSeconds) {
        if (bpm <= 0) {
            TeaseLogger.getLogger().log(Level.SEVERE, "Tried to set metronome bpm to 0 or lower.");
            return;
        }

        stopMetronome();

        currentBPM = bpm;

        (metronome = new Metronome()).start(bpm);
        
        if (TeaseAI.application.ESTIM_ENABLED.getBoolean() && TeaseAI.application.ESTIM_METRONOME.getBoolean()) {
            (estimMetronome = new EstimMetronome()).start(bpm);
        }

        if (durationSeconds > 0) {
            (waitingThread = new Thread() {
                Metronome watchingMetronome = metronome;

                @Override
                public void run() {
                    synchronized (this) {
                        try {
                            wait(durationSeconds * 1000);

                            //Check if we are still dealing with the metronome that we were watching
                            if (watchingMetronome == metronome) {
                                metronome.destroy();
                                if (estimMetronome != null) {
                                    estimMetronome.stop();
                                }
                                setStroking(false);
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
        if (metronome != null) {
            metronome.destroy();
            metronome = null;
            setStroking(false);
            
            if (estimMetronome != null) {
                estimMetronome.stop();
                estimMetronome = null;
            }

            //Check if we are waiting for stopping the metronome
            if (waitingThread != null) {
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

    public boolean isEdging() {
        return isEdging;
    }

    public void setEdging(boolean edging) {
        if (edging && TeaseAI.application.getSession() != null && !isEdging && StatisticsManager.edgeDetection) {
            JavaEdge edge = TeaseAI.application.getSession().statisticsManager.addEdge();
            if (currentBPM != 0)
                edge.BPM = currentBPM;
        } else if (!edging && TeaseAI.application.getSession() != null && isEdging && StatisticsManager.edgeDetection) {
            TeaseAI.application.getSession().statisticsManager.endEdge();
            stopMetronome();
        }

        isEdging = edging;
    }

    public void setStroking(boolean stroking) {
        if (stroking && TeaseAI.application.getSession() != null && !isStroking && StatisticsManager.strokeDetection) {
            JavaStroke stroke = TeaseAI.application.getSession().statisticsManager.addStroke();
            if (currentBPM != 0)
                stroke.BPM = currentBPM;
        } else if (!stroking && TeaseAI.application.getSession() != null && isStroking && StatisticsManager.strokeDetection) {
            TeaseAI.application.getSession().statisticsManager.endStroke();
        }

        isStroking = stroking;
    }

    public void setEdgeHold(boolean value) {
        if (value && TeaseAI.application.getSession() != null && !isHolding && StatisticsManager.edgeHoldDetection) {
            TeaseAI.application.getSession().statisticsManager.addEdgeHold();
        } else if (!value && TeaseAI.application.getSession() != null && isHolding && StatisticsManager.edgeHoldDetection) {
            TeaseAI.application.getSession().statisticsManager.endEdgeHold();
        }

        isHolding = value;
    }

    public static StrokeHandler getHandler() {
        return handler;
    }

    public static void setHandler(StrokeHandler handler) {
        StrokeHandler.handler = handler;
    }
}
