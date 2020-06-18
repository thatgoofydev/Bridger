package com.opennetworksmc.bridger.api.platform;

import org.checkerframework.checker.nullness.qual.NonNull;

public enum PlatformType {
    
    BUNGEECORD("BungeeCord"),
    VELOCITY("Velocity");

    private final String friendlyName;

    PlatformType(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public @NonNull String getFriendlyName() {
        return friendlyName;
    }
}
