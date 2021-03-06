package be.goofydev.bridger.bungee;

import be.goofydev.bridger.api.model.proxy.Proxy;
import be.goofydev.bridger.api.platform.UserProvider;
import be.goofydev.bridger.common.event.AbstractEventBus;
import be.goofydev.bridger.common.plugin.AbstractBridgerPlugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BridgerBungeePlugin extends AbstractBridgerPlugin {

    private final BridgerBungeeBootstrap bootstrap;
    private final BungeeProxy currentProxy;
    private final BungeeUserProvider userProvider;
    private final BungeeEventBus eventBus;

    public BridgerBungeePlugin(BridgerBungeeBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.currentProxy = new BungeeProxy(this, this.bootstrap.getProxy());
        this.userProvider = new BungeeUserProvider(this);
        this.eventBus = new BungeeEventBus(this);
    }

    @Override
    public void registerPlatformListeners() {
        this.bootstrap.getProxy().getPluginManager().registerListener(this.bootstrap, new PlayerListener(this));
    }

    @Override
    public BridgerBungeeBootstrap getBootstrap() {
        return this.bootstrap;
    }

    @Override
    public Proxy getCurrentProxy() {
        return this.currentProxy;
    }

    @Override
    @SuppressWarnings("unchecked")
    public UserProvider<ProxiedPlayer, BungeeUser> getUserProvider() {
        return this.userProvider;
    }

    @Override
    public AbstractEventBus<?> getEventBus() {
        return this.eventBus;
    }
}
