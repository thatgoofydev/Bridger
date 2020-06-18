package com.opennetworksmc.bridger.api.event.user;

import com.opennetworksmc.bridger.api.event.BridgerEvent;
import com.opennetworksmc.bridger.api.model.user.User;
import org.checkerframework.checker.nullness.qual.NonNull;

public class UserServerChangeEvent implements BridgerEvent {

    private final User user;
    private final String serverFrom;
    private final String serverTo;

    public UserServerChangeEvent(@NonNull User user, String serverFrom, String serverTo) {
        this.user = user;
        this.serverFrom = serverFrom;
        this.serverTo = serverTo;
    }

    public @NonNull User getUser() {
        return user;
    }

    public String getServerFrom() {
        return serverFrom;
    }

    public String getServerTo() {
        return serverTo;
    }
}
