package com.opennetworksmc.bridger.api.model.proxy;

import com.opennetworksmc.bridger.api.model.user.User;
import net.kyori.text.Component;

import java.util.Set;
import java.util.UUID;

public interface Proxy {
    /**
     * Gets the unique identifier of the proxy.
     * @return id
     */
    String getId();

    /**
     * Gets all users connected to the proxy.
     * @return set of users
     */
    Set<User> getUsers();

    /**
     * Gets a specific user that is connected to the proxy.
     * @param id of the user to retrieve
     * @return instance of {@link User} or null if user not found
     */
    User getUser(UUID id);

    /**
     * Check if a user is connected to this proxy.
     * @param id of the user to check for
     * @return true if found
     */
    boolean hasUser(UUID id);

    /**
     * Broadcast a message to every user connected to the proxy.
     * @param component to broadcast
     */
    void broadcastMessage(Component component);

    /**
     * Execute a command on the proxy
     * @param command command to execute
     */
    void executeCommand(String... command);
}
