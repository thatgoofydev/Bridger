package be.goofydev.bridger.velocity;

import be.goofydev.bridger.velocity.logging.Slf4jLoggerWrapper;
import com.google.inject.Inject;
import be.goofydev.bridger.api.platform.PlatformType;
import be.goofydev.bridger.common.plugin.bootstrap.BridgerBootstrap;
import be.goofydev.bridger.common.plugin.logging.PluginLogger;
import be.goofydev.bridger.common.plugin.scheduler.SchedulerAdapter;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.InputStream;
import java.nio.file.Path;

@Plugin(
        id = "bridger-velocity",
        name = "Bridger-Velocity",
        version = "@Version@",
        authors = "goofydev"
)
public class BridgerVelocityBootstrap implements BridgerBootstrap {

    private final PluginLogger logger;
    private final ProxyServer proxyServer;
    private final Path dataDirectory;

    private final BridgerVelocityPlugin plugin;
    private final VelocitySchedulerAdapter schedulerAdapter;

    @Inject
    public BridgerVelocityBootstrap(Logger logger, @DataDirectory Path dataDirectory, ProxyServer proxyServer) {
        this.logger = new Slf4jLoggerWrapper(logger);
        this.proxyServer = proxyServer;
        this.dataDirectory = dataDirectory;
        this.plugin = new BridgerVelocityPlugin(this);
        this.schedulerAdapter = new VelocitySchedulerAdapter(this);
    }

    @Subscribe
    public void onEnable(ProxyInitializeEvent event) {
        this.plugin.enable();
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        this.plugin.disable();
    }

    @Override
    public PluginLogger getPluginLogger() {
        return this.logger;
    }

    @Override
    public SchedulerAdapter getSchedulerAdapter() {
        return this.schedulerAdapter;
    }

    @Override
    public PlatformType getPlatformType() {
        return PlatformType.VELOCITY;
    }

    @Override
    public Path getDataDirectory() {
        return this.dataDirectory;
    }

    @Override
    public InputStream getResourceStream(String path) {
        return getClass().getResourceAsStream(path);
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }
}
