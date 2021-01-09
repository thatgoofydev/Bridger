package be.goofydev.bridger.api.event.redis;

import be.goofydev.bridger.api.event.BridgerEvent;

public class RedisMessageEvent implements BridgerEvent {

    private final String channel;
    private final String message;

    public RedisMessageEvent(String channel, String message) {
        this.channel = channel;
        this.message = message;
    }

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

}
