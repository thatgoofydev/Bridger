package be.goofydev.bridger.common.plugin;

import be.goofydev.bridger.api.BridgerApi;
import be.goofydev.bridger.api.model.proxy.Proxy;
import be.goofydev.bridger.api.model.user.User;
import be.goofydev.bridger.api.platform.UserProvider;
import be.goofydev.bridger.common.config.Configuration;
import be.goofydev.bridger.common.event.AbstractEventBus;
import be.goofydev.bridger.common.plugin.logging.PluginLogger;
import be.goofydev.bridger.common.redis.RedisDataManager;
import be.goofydev.bridger.common.redis.RedisManager;
import be.goofydev.bridger.common.plugin.bootstrap.BridgerBootstrap;
import be.goofydev.bridger.common.plugin.scheduler.SchedulerAdapter;

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

