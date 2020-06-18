package com.opennetworksmc.bridger.common.model.user;

import com.opennetworksmc.bridger.api.model.user.User;
import com.opennetworksmc.bridger.api.model.user.UserManager;
import com.opennetworksmc.bridger.common.plugin.BridgerPlugin;

import java.util.UUID;

public class StandardUserManager implements UserManager {

    private final BridgerPlugin plugin;

    public StandardUserManager(BridgerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public User getUser(UUID uniqueId) {
        if (this.plugin.getCurrentProxy().hasUser(uniqueId)) {
            return this.plugin.getCurrentProxy().getUser(uniqueId);
        }
        return new BridgedUser(uniqueId, this.plugin.getRedisDataManager());
    }

    @Override
    public User getUser(String username) {
        return null; // todo
    }

    @Override
    public boolean isUserOnline(UUID uniqueId) {
        if (this.plugin.getCurrentProxy().hasUser(uniqueId)) {
            return true;
        }
        return this.plugin.getRedisDataManager().isPlayerOnline(uniqueId);
    }
}
