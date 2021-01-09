package be.goofydev.bridger.api.model.user;

import be.goofydev.bridger.api.model.proxy.Proxy;
import net.kyori.text.Component;

import java.util.UUID;

public interface User {

    /**
     * Gets the unique id of the user.
     *
     * @return uuid
     */
    UUID getUniqueId();

    /**
     * Gets the name of the user.
     *
     * @return name
     */
    String getUsername();

    /**
     * Gets the ip which the user is currently connected from.
     *
     * @return ip
     */
    String getIp();

    /**
     * Gets the proxy the user is connected to.
     *
     * @return proxy
     */
    Proxy getProxy();

    /**
     * Get the name of the server the user is connected to.
     *
     * @return server name
     */
    String getServer();

    /**
     * Send a message to the user.
     *
     * @param message The message to send.
     */
    void sendMessage(Component message);
}
