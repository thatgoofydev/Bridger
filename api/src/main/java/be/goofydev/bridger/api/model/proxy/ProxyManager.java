package be.goofydev.bridger.api.model.proxy;

import java.util.Set;

public interface ProxyManager {

    /**
     * Get proxy by identifier.
     *
     * @param proxyId of the proxy
     * @return instance of {@link Proxy} or null if not found
     */
    Proxy getProxy(String proxyId);

    /**
     * Get the current EmeraldProxy.
     *
     * @return current proxy or null if not a proxy instance
     */
    Proxy getCurrentProxy();

    /**
     * Get player count of proxy
     *
     * @param proxyId of the proxy
     * @return count of players (0 if no players or proxy not found)
     */
    int getProxyUserCount(String proxyId);

    /**
     * Gets a list of ids of all active proxy instances.
     *
     * @return list of active proxy ids
     */
    Set<String> getActiveProxies();

}
