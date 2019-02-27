package me.goddragon.teaseai.utils.libraries.ripme.ripper;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import me.goddragon.teaseai.utils.TeaseLogger;
import me.goddragon.teaseai.utils.libraries.ripme.utils.Utils;

/**
 * Simple wrapper around a FixedThreadPool.
 */
public class DownloadThreadPool {

    private static final TeaseLogger logger = TeaseLogger.getLogger();
    private ThreadPoolExecutor threadPool = null;

    public DownloadThreadPool() {
        initialize("Main");
    }

    public DownloadThreadPool(String threadPoolName) {
        initialize(threadPoolName);
    }
    
    /**
     * Initializes the threadpool.
     * @param threadPoolName Name of the threadpool.
     */
    private void initialize(String threadPoolName) {
        int threads = Utils.getConfigInteger("threads.size", 10);
        logger.log(Level.FINE, "Initializing " + threadPoolName + " thread pool with " + threads + " threads");
        threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
    }
    /**
     * For adding threads to execution pool.
     * @param t 
     *      Thread to be added.
     */
    public void addThread(Thread t) {
        threadPool.execute(t);
    }

    /**
     * Tries to shutdown threadpool.
     */
    public void waitForThreads() {
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(3600, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "[!] Interrupted while waiting for threads to finish: ", e);
        }
    }
}
