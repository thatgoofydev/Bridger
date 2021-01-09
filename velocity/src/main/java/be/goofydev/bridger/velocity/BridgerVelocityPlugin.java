package be.goofydev.bridger.velocity;

import be.goofydev.bridger.api.model.proxy.Proxy;
import be.goofydev.bridger.api.platform.UserProvider;
import be.goofydev.bridger.common.event.AbstractEventBus;
import be.goofydev.bridger.common.plugin.AbstractBridgerPlugin;
import com.velocitypowered.api.proxy.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BridgerVelocityPlugin extends AbstractBridgerPlugin {

    private final BridgerVelocityBootstrap bootstrap;
    private final VelocityProxy currentProxy;
    private final VelocityUserProvider userProvider;
    private final VelocityEventBus eventBus;

    public BridgerVelocityPlugin(BridgerVelocityBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.currentProxy = new VelocityProxy(this, bootstrap.getProxyServer());
        this.userProvider = new VelocityUserProvider(this);
        this.eventBus = new VelocityEventBus(this);
    }

    @Override
    public void registerPlatformListeners() {
        this.bootstrap.getProxyServer().getEventManager().register(this.bootstrap, new PlayerListener(this));
    }

    @Override
    public BridgerVelocityBootstrap getBootstrap() {
        return this.bootstrap;
    }

    @Override
    public Proxy getCurrentProxy() {
        return this.currentProxy;
    }

    @Override
    @SuppressWarnings("unchecked")
    public UserProvider<Player, VelocityUser> getUserProvider() {
        return this.userProvider;
    }

    @Override
    public AbstractEventBus<?> getEventBus() {
        return this.eventBus;
    }
}
