package be.goofydev.bridger.common.tasks;

import be.goofydev.bridger.common.plugin.BridgerPlugin;

public class HeartbeatTask implements Runnable {

    private final BridgerPlugin plugin;

    public HeartbeatTask(BridgerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.plugin.getRedisDataManager().updateProxyHeartbeat();
    }
}
