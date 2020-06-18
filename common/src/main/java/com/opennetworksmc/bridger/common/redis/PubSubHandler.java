package com.opennetworksmc.bridger.common.redis;

public interface PubSubHandler {
    void handle(String channel, String message);
}
