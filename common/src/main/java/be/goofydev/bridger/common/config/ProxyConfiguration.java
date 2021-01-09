package be.goofydev.bridger.common.config;

import com.moandjiezana.toml.Toml;

public class ProxyConfiguration {

    private final String proxyId;
    private final int heartbeatInterval;
    private final boolean overridePlayerCount;

    protected ProxyConfiguration(Toml proxyTable) {
        this.proxyId = proxyTable.getString("proxy-id", "proxy-1");
        this.heartbeatInterval = proxyTable.getLong("heartbeat-interval", 5L).intValue();
        this.overridePlayerCount = proxyTable.getBoolean("override-player-count", false);
    }

    public String getProxyId() {
        return proxyId;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public boolean shouldOverridePlayerCount() {
        return overridePlayerCount;
    }
}
