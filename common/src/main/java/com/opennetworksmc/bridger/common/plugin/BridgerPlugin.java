package com.opennetworksmc.bridger.common.plugin;

import com.opennetworksmc.bridger.api.BridgerApi;
import com.opennetworksmc.bridger.api.model.proxy.Proxy;
import com.opennetworksmc.bridger.api.model.user.User;
import com.opennetworksmc.bridger.api.platform.UserProvider;
import com.opennetworksmc.bridger.common.config.Configuration;
import com.opennetworksmc.bridger.common.event.AbstractEventBus;
import com.opennetworksmc.bridger.common.plugin.logging.PluginLogger;
import com.opennetworksmc.bridger.common.plugin.bootstrap.BridgerBootstrap;
import com.opennetworksmc.bridger.common.plugin.scheduler.SchedulerAdapter;
import com.opennetworksmc.bridger.common.redis.RedisDataManager;
import com.opennetworksmc.bridger.common.redis.RedisManager;

public interface BridgerPlugin {

    BridgerBootstrap getBootstrap();

    PluginLogger getLogger();

    Configuration getConfiguration();

    Proxy getCurrentProxy();

    SchedulerAdapter getSchedulerAdapter();

    RedisManager getRedisManager();

    RedisDataManager getRedisDataManager();

    <I, T extends User> UserProvider<I, T> getUserProvider();

    BridgerApi getApi();

    AbstractEventBus<?> getEventBus();

}

