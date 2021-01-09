package be.goofydev.bridger.common.redis;

public final class RedisConstants {

    public static final String PROXY_ALL = "all";

    public static final String CHANNEL_PREFIX = "bridger:";
    public static final String CHANNEL_PROXY_PREFIX = CHANNEL_PREFIX + "proxy:";

    public static final String MESSAGE_PREFIX_BROADCAST = CHANNEL_PREFIX + "broadcast-";
    public static final String MESSAGE_PREFIX_COMMAND = CHANNEL_PREFIX + "command-";
    public static final String MESSAGE_PREFIX_MSG = CHANNEL_PREFIX + "msg-";

    public static String getProxyChannel(String id) {
        return CHANNEL_PROXY_PREFIX + id;
    }

    private RedisConstants() { }
}
