package me.goddragon.teaseai.utils.async;

public abstract class UpdateJob implements Runnable {
    /**
     * This object gets locked while the UpdateWorker processes this job, if there is another job with this lock, it waits for the lock to be released
     */
    public Object lock;

    /**
     * Milliseconds to wait before removing the lock from the object, 0 for no delay
     */
    public int millisToWait = 0;

    public UpdateJob(Object lock) {
        this.lock = lock;
    }

    public UpdateJob(Object lock, int millisToWait) {
        this.lock = lock;
        this.millisToWait = millisToWait;
    }
}
