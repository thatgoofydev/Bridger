package be.goofydev.bridger.common.redis.impl;

import be.goofydev.bridger.api.event.redis.RedisMessageEvent;
import be.goofydev.bridger.common.redis.LuaScript;
import be.goofydev.bridger.common.redis.RedisBridgerChannelHandler;
import be.goofydev.bridger.common.redis.RedisManager;
import be.goofydev.bridger.common.config.RedisConfiguration;
import be.goofydev.bridger.common.plugin.BridgerPlugin;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisConnectionException;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class StandaloneRedisManager implements RedisManager {
    private final RedisClient redisClient;
    private final GenericObjectPool<StatefulRedisConnection<String, String>> pool;
    private final StatefulRedisPubSubConnection<String, String> pubSubConnection;

    public StandaloneRedisManager(BridgerPlugin plugin, RedisConfiguration configuration) throws RuntimeException {
        Optional<RedisURI> connectionUri = configuration.getRedisURIs().stream().findFirst();
        if (!connectionUri.isPresent()) {
            throw new IllegalStateException("No redis connection details found, at least one is required.");
        }

        RedisURI redisURI = connectionUri.get();
        this.redisClient = RedisClient.create(redisURI);

        StatefulRedisConnection<String, String> connection = null;
        try {
            connection = this.redisClient.connect();
            String info = connection.sync().info();
            connection.close();

            String version = Arrays.stream(info.split("\r\n")).filter(s -> s.startsWith("redis_version:")).map(s -> s.split(":")[1]).findFirst().orElse(null);
            if (version == null) {
                throw new RuntimeException("Unable to retrieve redis server version.");
            }

            String[] versionNumbers = version.split("\\.");
            int major = Integer.parseInt(versionNumbers[0]);
            int minor = Integer.parseInt(versionNumbers[1]);
            if (major < 2 || (major == 2 && minor < 6)) {
                throw new RuntimeException("Lua is not supported on redis versions below 2.6. Please update your redis implementation.");
            }
        } catch (RedisConnectionException exception) {
            throw new IllegalStateException("Unable to connect to redis! " + redisURI.getHost() + ":" + redisURI.getPort());
        } finally {
            if(connection != null && connection.isOpen()) {
                connection.close();
            }
        }

        this.pool = ConnectionPoolSupport.createGenericObjectPool(redisClient::connect, new GenericObjectPoolConfig<>());
        this.pubSubConnection = redisClient.connectPubSub();
        this.pubSubConnection.addListener(new PubSubListener(plugin));
    }

    @Override
    public void execute(Consumer<RedisCommands<String, String>> consumer) {
        try (StatefulRedisConnection<String, String> connection = pool.borrowObject()) {
            RedisCommands<String, String> commands = connection.sync();
            consumer.accept(commands);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public <R> R execute(Function<RedisCommands<String, String>, R> function) {
        try (StatefulRedisConnection<String, String> connection = pool.borrowObject()) {
            RedisCommands<String, String> commands = connection.sync();
            R result = function.apply(commands);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void closeConnections() {
        pubSubConnection.close();
        pool.close();
        redisClient.shutdown();
    }

    @Override
    public LuaScript loadScript(String script) {
        return this.execute(commands -> {
            final String hashed = commands.scriptLoad(script);
            return new LuaScript(script, hashed, this);
        });
    }

    @Override
    public void subscribeToChannels(String... channels) {
        this.pubSubConnection.sync().subscribe(channels);
    }

    @Override
    public void publishToChannel(String channel, String message) {
        this.pubSubConnection.sync().publish(channel, message);
    }

    private static class PubSubListener extends RedisPubSubAdapter<String, String> {

        private final BridgerPlugin plugin;
        private final RedisBridgerChannelHandler handler;

        public PubSubListener(BridgerPlugin plugin) {
            this.plugin = plugin;
            this.handler = new RedisBridgerChannelHandler(plugin);
        }

        @Override
        public void message(String channel, String message) {
            if (channel.startsWith("bridger:")) {
                this.handler.handle(message);
                return;
            }
            this.plugin.getEventBus().post(new RedisMessageEvent(channel, message));
        }
    }

}
