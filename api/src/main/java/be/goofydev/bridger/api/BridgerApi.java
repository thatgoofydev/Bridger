package be.goofydev.bridger.api;

import be.goofydev.bridger.api.event.redis.RedisMessageEvent;
import be.goofydev.bridger.api.model.proxy.ProxyManager;
import be.goofydev.bridger.api.model.user.UserManager;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;

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
    @NonNull ProxyManager getProxyManager();

    /**
     * Broadcast a message to all proxies.
     *
     * @param message The message to send.
     */
    void broadcast(@NonNull Component message);

    /**
     * Broadcast a message to all proxies.
     *
     * @param message The message to send.
     */
    default void broadcast(@NonNull String message) {
        broadcast(TextComponent.of(message));
    }

    /**
     * Execute a command on all proxies.
     *
     * @param command The command to execute on all proxies.
     */
    void executeCommand(@NonNull String... command);

    /**
     * Subscribe to specific redis channels to trigger a {@link RedisMessageEvent} for.
     *
     * @param channels to subscribe to
     */
    void subscribeToChannels(@NonNull String... channels);

    /**
     * Send a message over a specific redis channel.
     *
     * @param channel to send the message over
     * @param message to send
     */
    void publishToChannel(@NonNull String channel, @NonNull String message);

}
