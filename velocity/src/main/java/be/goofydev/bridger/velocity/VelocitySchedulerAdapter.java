package be.goofydev.bridger.velocity;

import be.goofydev.bridger.common.plugin.scheduler.SchedulerAdapter;
import be.goofydev.bridger.common.plugin.scheduler.SchedulerTask;
import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.Scheduler;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class VelocitySchedulerAdapter implements SchedulerAdapter {

    private final BridgerVelocityBootstrap bootstrap;
    private final Scheduler scheduler;
    private final Executor executor;
    private final Set<ScheduledTask> tasks = Collections.newSetFromMap(new WeakHashMap<>());

    public VelocitySchedulerAdapter(BridgerVelocityBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.scheduler = this.bootstrap.getProxyServer().getScheduler();
        this.executor = r -> this.scheduler.buildTask(bootstrap, r).schedule();
    }

    @Override
    public void executeAsync(Runnable task) {
        this.executor.execute(task);
    }

    @Override
    public SchedulerTask asyncLater(Runnable task, long delay, TimeUnit unit) {
        ScheduledTask st = this.scheduler.buildTask(this.bootstrap, task)
                .delay(delay, unit)
                .schedule();

        this.tasks.add(st);
        return st::cancel;
    }

    @Override
    public SchedulerTask asyncRepeating(Runnable task, long interval, TimeUnit unit) {
        ScheduledTask st = this.scheduler.buildTask(this.bootstrap, task)
                .repeat(interval, unit)
                .schedule();

        this.tasks.add(st);
        return st::cancel;
    }

    @Override
    public void shutdown() {
        tasks.forEach(ScheduledTask::cancel);
    }
}
