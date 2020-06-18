package com.opennetworksmc.bridger.common.tasks;

import com.opennetworksmc.bridger.common.plugin.BridgerPlugin;

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
