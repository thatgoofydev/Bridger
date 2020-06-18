package com.opennetworksmc.bridger.common.redis;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.opennetworksmc.bridger.api.model.proxy.Proxy;
import com.opennetworksmc.bridger.api.model.user.User;
import com.opennetworksmc.bridger.common.model.proxy.BridgedProxy;
import com.opennetworksmc.bridger.common.model.user.BridgedUser;
import com.opennetworksmc.bridger.common.plugin.BridgerPlugin;
import com.opennetworksmc.bridger.common.util.StreamUtil;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RedisDataManager {

    public static final String PREFIX_PROXY_ONLINE = "proxy:online:";
    public static final String PREFIX_PROXY_HEARTBEAT = "proxy:heartbeats:";

    public static final String PREFIX_USER = "user:";
    public static final String FIELD_USER_IP = "ip";
    public static final String FIELD_USER_PROXY = "proxy";
    public static final String FIELD_USER_SERVER = "server";

    private final BridgerPlugin plugin;
    private final RedisManager redisManager;

    private final Cache<UUID, String> playerProxyCache;
    private final Cache<UUID, String> playerIpCache;

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
            // todo throw error?
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
            commands.hdel(key, FIELD_USER_IP, FIELD_USER_PROXY, FIELD_USER_SERVER);
            commands.srem(proxyOnlineKey, user.getUniqueId().toString());
            commands.exec();
        });
    }

    public String getPlayerServer(UUID id) {
        final String userKey = PREFIX_USER + id.toString();
        return this.redisManager.execute(commands -> {
            return commands.hget(userKey, FIELD_USER_SERVER);
        });
    }

    public String getPlayerIp(UUID id) {
        try {
            return this.playerIpCache.get(id, () -> this.redisManager.execute(commands -> {
                final String userKey = PREFIX_USER + id.toString();
                return commands.hget(userKey, FIELD_USER_IP);
            }));
        } catch (ExecutionException e) {
            throw new RuntimeException("Unable to get proxy for " + id, e);
        }
    };

    public Proxy getPlayerProxy(UUID id) {

        try {
            String playerProxyId = this.playerProxyCache.get(id, () -> this.redisManager.execute(commands -> {
                final String userKey = PREFIX_USER + id.toString();
                return commands.hget(userKey, FIELD_USER_PROXY);
            }));

            return new BridgedProxy(playerProxyId, this);
        } catch (ExecutionException e) {
            throw new RuntimeException("Unable to get proxy for " + id, e);
        }
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
                    .map(id -> new BridgedUser(id, this))
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
}