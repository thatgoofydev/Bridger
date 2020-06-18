package com.opennetworksmc.bridger.velocity;

import com.opennetworksmc.bridger.api.model.proxy.Proxy;
import com.opennetworksmc.bridger.api.platform.UserProvider;
import com.opennetworksmc.bridger.common.event.AbstractEventBus;
import com.opennetworksmc.bridger.common.plugin.AbstractBridgerPlugin;
import com.velocitypowered.api.proxy.Player;

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
