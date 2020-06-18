package com.opennetworksmc.bridger.common.redis.impl;

import com.opennetworksmc.bridger.common.config.RedisConfiguration;
import com.opennetworksmc.bridger.common.plugin.BridgerPlugin;
import com.opennetworksmc.bridger.common.redis.RedisManager;

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
