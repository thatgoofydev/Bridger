package com.opennetworksmc.bridger.api;

import com.opennetworksmc.bridger.api.model.proxy.ProxyManager;
import com.opennetworksmc.bridger.api.model.user.UserManager;
import net.kyori.text.Component;

public interface BridgerApi {

    /**
     * Get total player count.
     *
     * @return combined player count of all proxies
     */
    int getTotalUserCount();

    /**
     * Gets the UserManager.
     *
     * @return UserManager
     */
    UserManager getUserManager();

    /**
     * Gets the ProxyManager.
     *
     * @return ProxyManager.
     */
    ProxyManager getProxyManager();

    /**
     * Broadcast a message to all proxies.
     *
     * @param component to send
     */
    void broadcastToAllProxies(Component component);

    /**
     * Subscribe to specific redis channels to trigger a {@link com.opennetworksmc.bridger.api.event.redis.RedisMessageEvent} for.
     *
     * @param channels to subscribe to
     */
    void subscribeToChannels(String... channels);

    /**
     * Send a message over a specific redis channel.
     *
     * @param channel to send the message over
     * @param message to send
     */
    void publishToChannel(String channel, String message);

}
