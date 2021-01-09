package be.goofydev.bridger.common.redis;

import be.goofydev.bridger.common.model.proxy.BridgedProxy;
import be.goofydev.bridger.common.model.user.BridgedUser;
import be.goofydev.bridger.common.util.StreamUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import be.goofydev.bridger.api.model.proxy.Proxy;
import be.goofydev.bridger.api.model.user.User;
import be.goofydev.bridger.common.plugin.BridgerPlugin;
import net.kyori.text.Component;
import net.kyori.text.serializer.gson.GsonComponentSerializer;

import java.awt.*;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RedisDataManager {

    public static final String PREFIX_PROXY_ONLINE = "proxy:online:";
    public static final String PREFIX_PROXY_HEARTBEAT = "proxy:heartbeats:";

    public static final String PREFIX_USER = "user:";
    public static final String FIELD_USER_USERNAME = "username";
    public static final String FIELD_USER_IP = "ip";
    public static final String FIELD_USER_PROXY = "proxy";
    public static final String FIELD_USER_SERVER = "server";

    private final BridgerPlugin plugin;
    private final RedisManager redisManager;

    private final Cache<UUID, String> playerProxyCache;
    private final Cache<UUID, String> playerIpCache;
    private final Cache<UUID, String> playerUsernameCache;

    private LuaScript getTotalPlayerCountScript;
    private LuaScript getActiveProxies;

    public RedisDataManager(BridgerPlugin plugin) {
        this.plugin = plugin;
        this.redisManager = plugin.getRedisManager();

        this.playerProxyCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();

        this.playerIpCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();

        this.playerUsernameCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();

        registerScripts();
    }

    private void registerScripts() {
        this.getTotalPlayerCountScript = registerScript("/lua/get_total_player_count.lua");
        this.getActiveProxies = registerScript("/lua/get_active_proxies.lua");
    }

    private LuaScript registerScript(String resourcePath) {
        InputStream is = this.plugin.getBootstrap().getResourceStream(resourcePath);
        String script = StreamUtil.streamToString(is);
        if (script == null) {
            return null;
        }
        return this.redisManager.loadScript(script);
    }

    public void addUser(User user) {
        final String key = PREFIX_USER + user.getUniqueId();
        final String proxyOnlineKey = PREFIX_PROXY_ONLINE + user.getProxy().getId();

        this.redisManager.execute(commands -> {
            commands.multi();

            Map<String, String> playerData = new HashMap<>();
            playerData.put(FIELD_USER_USERNAME, user.getUsername());
            playerData.put(FIELD_USER_IP, user.getIp());
            playerData.put(FIELD_USER_PROXY, user.getProxy().getId());
            playerData.put(FIELD_USER_SERVER, user.getServer());
            commands.hset(key, playerData);

            commands.sadd(proxyOnlineKey, user.getUniqueId().toString());
            commands.exec();
        });
    }

    public void removeUser(User user) {
        final String key = PREFIX_USER + user.getUniqueId();
        final String proxyOnlineKey = PREFIX_PROXY_ONLINE + user.getProxy().getId();

        this.redisManager.execute(commands -> {
            commands.multi();
            commands.hdel(key, FIELD_USER_USERNAME, FIELD_USER_IP, FIELD_USER_PROXY, FIELD_USER_SERVER);
            commands.srem(proxyOnlineKey, user.getUniqueId().toString());
            commands.exec();
        });
    }

    public String getPlayerUsername(UUID id) {
        try {
            return this.playerUsernameCache.get(id, () -> this.redisManager.execute(commands -> {
                final String userKey = PREFIX_USER + id.toString();
                return commands.hget(userKey, FIELD_USER_USERNAME);
            }));
        } catch (ExecutionException e) {
            throw new RuntimeException("Unable to get username for " + id, e);
        }
    }

    public String getPlayerIp(UUID id) {
        try {
            return this.playerIpCache.get(id, () -> this.redisManager.execute(commands -> {
                final String userKey = PREFIX_USER + id.toString();
                return commands.hget(userKey, FIELD_USER_IP);
            }));
        } catch (ExecutionException e) {
            throw new RuntimeException("Unable to get ip for " + id, e);
        }
    };

    public Proxy getPlayerProxy(UUID id) {

        try {
            String playerProxyId = this.playerProxyCache.get(id, () -> this.redisManager.execute(commands -> {
                final String userKey = PREFIX_USER + id.toString();
                return commands.hget(userKey, FIELD_USER_PROXY);
            }));

            return new BridgedProxy(playerProxyId, this, this.plugin.getRedisManager());
        } catch (ExecutionException e) {
            throw new RuntimeException("Unable to get proxy for " + id, e);
        }
    }

    public String getPlayerServer(UUID id) {
        final String userKey = PREFIX_USER + id.toString();
        return this.redisManager.execute(commands -> {
            return commands.hget(userKey, FIELD_USER_SERVER);
        });
    }

    public void changeUserServer(User user, String serverName) {
        final String key = PREFIX_USER + user.getUniqueId();

        this.redisManager.execute(commands -> {
            commands.hset(key, FIELD_USER_SERVER, serverName);
        });
    }

    public void updateProxyHeartbeat() {
        final String key = PREFIX_PROXY_HEARTBEAT + this.plugin.getConfiguration().getProxyConfiguration().getProxyId();
        final long heartbeatInterval = this.plugin.getConfiguration().getProxyConfiguration().getHeartbeatInterval() * 2;
        this.redisManager.execute(commands -> {
            commands.setex(key, heartbeatInterval, "1");
        });
    }

    public void cleanupProxy() {
        final String key = PREFIX_PROXY_HEARTBEAT + this.plugin.getConfiguration().getProxyConfiguration().getProxyId();
        final String proxyOnlineKey = PREFIX_PROXY_ONLINE + this.plugin.getConfiguration().getProxyConfiguration().getProxyId();
        this.redisManager.execute(commands -> {
            commands.del(key);
            commands.del(proxyOnlineKey);
        });
    }

    public Set<User> getUsersOfProxy(String proxyId) {
        final String proxyOnlineKey = PREFIX_PROXY_ONLINE + proxyId;

        return this.redisManager.execute(commands -> {
            return commands.smembers(proxyOnlineKey)
                    .stream()
                    .map(UUID::fromString)
                    .map(id -> new BridgedUser(id, this, this.redisManager))
                    .collect(Collectors.toSet());
        });
    }

    public boolean isUserConnectedToProxy(String proxyId, UUID playerId) {
        final String proxyOnlineKey = PREFIX_PROXY_ONLINE + proxyId;
        return this.redisManager.execute(commands -> {
            return commands.sismember(proxyOnlineKey, playerId.toString());
        });
    }

    public int getUserCountOfProxy(String proxyId) {
        final String proxyOnlineKey = PREFIX_PROXY_ONLINE + proxyId;
        return this.redisManager.execute(commands -> {
            return commands.scard(proxyOnlineKey).intValue();
        });
    }

    public int getTotalPlayerCount() {
        if (getTotalPlayerCountScript == null) {
            return -2;
        }

        ArrayList<Long> results = getTotalPlayerCountScript.evalCast();
        if (results.isEmpty()) {
            return -1;
        }
        return results.get(0).intValue();
    }

    public Set<String> getActiveProxies() {
        return new HashSet<>(getActiveProxies.evalCast());
    }

    public boolean isPlayerOnline(UUID playerId) {
        final String key = PREFIX_USER + playerId;
        return this.redisManager.execute(commands -> commands.exists(key) == 1L);
    }

    public void broadcast(String proxyId, Component message) {
        final String content = GsonComponentSerializer.INSTANCE.serialize(message);
        if(content.isEmpty()) return;
        this.redisManager.publishToChannel(
                RedisConstants.getProxyChannel(proxyId),
                RedisConstants.MESSAGE_PREFIX_BROADCAST + content
        );
    }

    public void executeCommand(String proxyId, String... command) {
        if (command.length == 0) {
            return;
        }

        String fullCommand = String.join(" ", command);
        if (fullCommand.startsWith("/")) {
            fullCommand = fullCommand.substring(1);
        }

        if (fullCommand.trim().isEmpty()) {
            return;
        }

        this.redisManager.publishToChannel(
                RedisConstants.getProxyChannel(proxyId),
                RedisConstants.MESSAGE_PREFIX_COMMAND + fullCommand
        );
    }

    public void messageUser(UUID userId, Component message) {
        final String content = GsonComponentSerializer.INSTANCE.serialize(message);
        if(content.isEmpty()) return;
        this.redisManager.publishToChannel(
                RedisConstants.getProxyChannel(RedisConstants.PROXY_ALL),
                RedisConstants.MESSAGE_PREFIX_MSG + userId.toString() + ":" + content
        );
    }
}