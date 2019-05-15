package log5j.helpers;

/**
 * Interface to receive a notification when the LoggerRepository is being shut down.
 * Used to terminate the {@link FileWatchdog} thread.
 *
 */
public interface ShutdownEventListener {
    /**
     * Called when the LoggerRepository is being shut down.
     */
    public void shutdown();
}