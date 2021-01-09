package be.goofydev.bridger.common.redis;

import io.lettuce.core.api.sync.RedisCommands;

import java.util.function.Consumer;
import java.util.function.Function;

public interface RedisManager {
    void execute(Consumer<RedisCommands<String, String>> consumer);
    <R> R execute(Function<RedisCommands<String, String>, R> function);
    void closeConnections();
    LuaScript loadScript(String script);
    void subscribeToChannels(String... channels);
    void publishToChannel(String channel, String message);
}
