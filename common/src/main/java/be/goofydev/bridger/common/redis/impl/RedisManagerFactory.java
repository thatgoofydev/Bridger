package be.goofydev.bridger.common.redis.impl;

import be.goofydev.bridger.common.redis.RedisManager;
import be.goofydev.bridger.common.config.RedisConfiguration;
import be.goofydev.bridger.common.plugin.BridgerPlugin;

public final class RedisManagerFactory {

    private RedisManagerFactory() {
    }

    public static RedisManager create(BridgerPlugin plugin) {
        RedisConfiguration redisConfiguration = plugin.getConfiguration().getRedisConfiguration();
        if(redisConfiguration.getRedisURIs().size() > 1) {
            return null; // cluster
        }

        return new StandaloneRedisManager(plugin, redisConfiguration);
    }
}
