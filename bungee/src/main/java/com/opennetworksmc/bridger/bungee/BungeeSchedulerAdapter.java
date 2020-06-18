package com.opennetworksmc.bridger.bungee;

import com.opennetworksmc.bridger.common.plugin.scheduler.SchedulerAdapter;
import com.opennetworksmc.bridger.common.plugin.scheduler.SchedulerTask;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class BungeeSchedulerAdapter implements SchedulerAdapter {

    private final BridgerBungeeBootstrap bootstrap;
    private final Executor executor;
    private final Set<ScheduledTask> tasks = Collections.newSetFromMap(new WeakHashMap<>());

    public BungeeSchedulerAdapter(BridgerBungeeBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.executor = r -> this.bootstrap.getProxy().getScheduler().runAsync(bootstrap, r);
    }

    @Override
    public void executeAsync(Runnable task) {
        this.executor.execute(task);
    }

    @Override
    public SchedulerTask asyncLater(Runnable task, long delay, TimeUnit unit) {
        ScheduledTask st = this.bootstrap.getProxy().getScheduler().schedule(this.bootstrap, task, delay, unit);
        this.tasks.add(st);
        return st::cancel;
    }

    @Override
    public SchedulerTask asyncRepeating(Runnable task, long interval, TimeUnit unit) {
        ScheduledTask st = this.bootstrap.getProxy().getScheduler().schedule(this.bootstrap, task, 0, interval, unit);
        this.tasks.add(st);
        return st::cancel;
    }

    @Override
    public void shutdown() {
        this.tasks.forEach(ScheduledTask::cancel);
    }
}
