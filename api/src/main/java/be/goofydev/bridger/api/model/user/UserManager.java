package be.goofydev.bridger.api.model.user;

import java.util.UUID;

public interface UserManager {
    /**
     * Get player by id.
     * @param uniqueId of the user to retrieve
     * @return user or null when not found or online
     */
    User getUser(UUID uniqueId);

    /**
     * Get user by username.
     * @param username of the user to retrieve
     * @return user or null when not found or online
     */
    User getUser(String username);

    /**
     * Check if a user is online.
     * @param uniqueId of the user
     * @return true if user is online
     */
    boolean isUserOnline(UUID uniqueId);
}
