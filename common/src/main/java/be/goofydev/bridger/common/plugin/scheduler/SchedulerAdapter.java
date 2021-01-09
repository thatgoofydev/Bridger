package be.goofydev.bridger.common.plugin.scheduler;

import java.util.concurrent.TimeUnit;

public interface SchedulerAdapter {

    /**
     * Execute task async.
     *
     * @param task to execute
     */
    void executeAsync(Runnable task);

    /**
     * Schedule a task to run after a specific delay
     * @param task to run
     * @param delay to run the task after
     * @param unit of the delay parameter
     * @return task
     */
    SchedulerTask asyncLater(Runnable task, long delay, TimeUnit unit);

    /**
     * Schedule a task to repeat
     * @param task to run
     * @param interval to repeat the task after
     * @param unit of the interval parameter
     * @return task
     */
    SchedulerTask asyncRepeating(Runnable task, long interval, TimeUnit unit);

    /**
     * Stop all tasks
     */
    void shutdown();
}
