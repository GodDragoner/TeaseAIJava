package me.goddragon.teaseai.utils.async;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdaterWorker {

    //Work queue for locked objects
    private final List<UpdateJob> queue = new ArrayList<>();

    //List with locked items
    private final List<Object> locked = new ArrayList<>();

    //Worker threads
    private final ExecutorService executor;

    //lock to start thread updates
    private final Object updaterLock = new Object();

    private final Thread watchdog = new Thread(() -> {
        while (true) {
            try {
                synchronized (updaterLock) {
                    updaterLock.wait();
                }

                ArrayList<UpdateJob> toRemove = new ArrayList<>();

                synchronized (queue) {
                    synchronized (locked) {
                        if (queue.size() > 0) {
                            for (UpdateJob p : queue) {
                                if (!locked.contains(p.lock)) {
                                    toRemove.add(p);
                                    executeJob(p);
                                }
                            }

                            for (UpdateJob p : toRemove) {
                                queue.remove(p);
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    public UpdaterWorker(int threads) {
        this.executor = Executors.newFixedThreadPool(threads);
        watchdog.start();
    }

    public UpdaterWorker() {
        this.executor = Executors.newCachedThreadPool();
        watchdog.start();
    }

    /**
     * Adds a job to process
     *
     * @param p The yet to process update job
     */
    public void addJob(UpdateJob p) {
        synchronized (queue) {
            synchronized (locked) {
                if (locked.contains(p.lock)) {
                    queue.add(p);
                    return;
                }
            }
        }

        executeJob(p);
    }

    private void executeJob(UpdateJob p) {
        synchronized (locked) {
            locked.add(p.lock);
        }

        executor.execute(() -> doJob(p));
    }

    //Runs async
    private void doJob(UpdateJob p) {
        try {
            p.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (p.millisToWait > 0) {
            try {
                Thread.currentThread().sleep(p.millisToWait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        removeLock(p);
    }

    /**
     * Remove the lock from an update job's locked object
     *
     * @param p The corresponding update job
     */
    private void removeLock(UpdateJob p) {
        synchronized (locked) {
            locked.remove(p.lock);
        }

        update();
    }

    private void update() {
        synchronized (updaterLock) {
            updaterLock.notifyAll();
        }
    }
}
