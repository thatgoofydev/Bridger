package com.opennetworksmc.bridger.common.model.proxy;

import com.opennetworksmc.bridger.api.model.proxy.Proxy;
import com.opennetworksmc.bridger.api.model.proxy.ProxyManager;
import com.opennetworksmc.bridger.common.plugin.BridgerPlugin;

import java.util.Set;

public class StandardProxyManager implements ProxyManager {

    private final BridgerPlugin plugin;

    public StandardProxyManager(BridgerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Proxy getProxy(String proxyId) {
        if (this.plugin.getCurrentProxy().getId().equals(proxyId)) {
            return this.plugin.getCurrentProxy();
        }
        return new BridgedProxy(proxyId, this.plugin.getRedisDataManager());
    }

    @Override
    public Proxy getCurrentProxy() {
        return this.plugin.getCurrentProxy();
    }

    @Override
    public int getProxyUserCount(String proxyId) {
        return this.plugin.getRedisDataManager().getUserCountOfProxy(proxyId);
    }

    @Override
    public Set<String> getActiveProxies() {
        return this.plugin.getRedisDataManager().getActiveProxies();
    }
}
