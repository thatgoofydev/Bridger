package com.opennetworksmc.bridger.common.plugin;

import com.opennetworksmc.bridger.api.BridgerApi;
import com.opennetworksmc.bridger.api.model.proxy.ProxyManager;
import com.opennetworksmc.bridger.api.model.user.UserManager;
import com.opennetworksmc.bridger.common.config.Configuration;
import com.opennetworksmc.bridger.common.model.proxy.StandardProxyManager;
import com.opennetworksmc.bridger.common.model.user.StandardUserManager;
import com.opennetworksmc.bridger.common.plugin.logging.PluginLogger;
import com.opennetworksmc.bridger.common.plugin.scheduler.SchedulerAdapter;
import com.opennetworksmc.bridger.common.plugin.scheduler.SchedulerTask;
import com.opennetworksmc.bridger.common.redis.RedisDataManager;
import com.opennetworksmc.bridger.common.redis.RedisManager;
import com.opennetworksmc.bridger.common.redis.impl.RedisManagerFactory;
import com.opennetworksmc.bridger.common.tasks.HeartbeatTask;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;

import java.util.concurrent.TimeUnit;

public abstract class AbstractBridgerPlugin implements BridgerPlugin, BridgerApi {

    private Configuration configuration;

    private RedisManager redisManager;
    private RedisDataManager redisDataManager;

    private UserManager userManager;
    private ProxyManager proxyManager;

    private SchedulerTask heartbeatTask;

    public final void enable() {
        // load configuration
        getLogger().info("Loading configuration...");
        this.configuration = Configuration.load(this);

        // redis connection
        this.redisManager = RedisManagerFactory.create(this);
        this.redisDataManager = new RedisDataManager(this);
        this.subscribeToChannels("bridger-all", "bridger-" + getConfiguration().getProxyConfiguration().getProxyId());

        // managers
        this.userManager = new StandardUserManager(this);
        this.proxyManager = new StandardProxyManager(this);

        // tasks
        int heartbeatInterval = this.getConfiguration().getProxyConfiguration().getHeartbeatInterval();
        this.heartbeatTask = this.getSchedulerAdapter().asyncRepeating(new HeartbeatTask(this), heartbeatInterval, TimeUnit.SECONDS);

        // platform listeners
        this.registerPlatformListeners();

        getLogger().info("Successfully enabled.");
    }

    public final void disable() {
        getLogger().info("Starting shutdown process...");

        // tasks
        this.heartbeatTask.cancel();
        this.getSchedulerAdapter().shutdown();

        // event bus
        this.getEventBus().close();

        // redis connection
        this.redisDataManager.cleanupProxy();
        this.redisManager.closeConnections();

        getLogger().info("Goodbye!");
    }

    public abstract void registerPlatformListeners();

    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }

    @Override
    public PluginLogger getLogger() {
        return getBootstrap().getPluginLogger();
    }

    @Override
    public SchedulerAdapter getSchedulerAdapter() {
        return getBootstrap().getSchedulerAdapter();
    }

    @Override
    public RedisManager getRedisManager() {
        return this.redisManager;
    }

    @Override
    public RedisDataManager getRedisDataManager() {
        return this.redisDataManager;
    }

    @Override
    public UserManager getUserManager() {
        return this.userManager;
    }

    @Override
    public ProxyManager getProxyManager() {
        return this.proxyManager;
    }

    @Override
    public int getTotalUserCount() {
        return this.redisDataManager.getTotalPlayerCount();
    }

    @Override
    public BridgerApi getApi() {
        return this;
    }

    @Override
    public void broadcastToAllProxies(Component component) {
        final String content = ((TextComponent) component).content();
        if(content.isEmpty()) return;
        this.publishToChannel("bridger-all", "bridger:broadcast-" + content);
    }

    @Override
    public void subscribeToChannels(String... channels) {
        this.getRedisManager().subscribeToChannels(channels);
    }

    @Override
    public void publishToChannel(String channel, String message) {
        this.getRedisManager().publishToChannel(channel, message);
    }
}
