package be.goofydev.bridger.common.plugin;

import be.goofydev.bridger.api.BridgerApi;
import be.goofydev.bridger.api.model.proxy.ProxyManager;
import be.goofydev.bridger.api.model.user.UserManager;
import be.goofydev.bridger.common.config.Configuration;
import be.goofydev.bridger.common.model.proxy.StandardProxyManager;
import be.goofydev.bridger.common.model.user.StandardUserManager;
import be.goofydev.bridger.common.redis.RedisConstants;
import be.goofydev.bridger.common.redis.RedisDataManager;
import be.goofydev.bridger.common.redis.RedisManager;
import be.goofydev.bridger.common.redis.impl.RedisManagerFactory;
import be.goofydev.bridger.common.tasks.HeartbeatTask;
import be.goofydev.bridger.common.plugin.logging.PluginLogger;
import be.goofydev.bridger.common.plugin.scheduler.SchedulerAdapter;
import be.goofydev.bridger.common.plugin.scheduler.SchedulerTask;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.serializer.gson.GsonComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;

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
        this.subscribeToChannels(
                RedisConstants.getProxyChannel(RedisConstants.PROXY_ALL),
                RedisConstants.getProxyChannel(getConfiguration().getProxyConfiguration().getProxyId())
        );

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
    public @NonNull ProxyManager getProxyManager() {
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
    public void broadcast(@NonNull Component message) {
        this.redisDataManager.broadcast(RedisConstants.PROXY_ALL, message);
    }

    @Override
    public void executeCommand(@NonNull String... command) {
        this.redisDataManager.executeCommand(RedisConstants.PROXY_ALL, command);
    }

    @Override
    public void subscribeToChannels(@NonNull String... channels) {
        this.getRedisManager().subscribeToChannels(channels);
    }

    @Override
    public void publishToChannel(@NonNull String channel, @NonNull String message) {
        this.getRedisManager().publishToChannel(channel, message);
    }
}
