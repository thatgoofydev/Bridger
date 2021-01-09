package be.goofydev.bridger.bungee;

import be.goofydev.bridger.api.platform.PlatformType;
import be.goofydev.bridger.common.plugin.bootstrap.BridgerBootstrap;
import be.goofydev.bridger.common.plugin.logging.JavaLoggerWrapper;
import be.goofydev.bridger.common.plugin.logging.PluginLogger;
import be.goofydev.bridger.common.plugin.scheduler.SchedulerAdapter;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.InputStream;
import java.nio.file.Path;

public class BridgerBungeeBootstrap extends Plugin implements BridgerBootstrap {

    private final PluginLogger logger;
    private final SchedulerAdapter schedulerAdapter;
    private final BridgerBungeePlugin plugin;

    public BridgerBungeeBootstrap() {
        this.logger = new JavaLoggerWrapper(this.getLogger());
        this.schedulerAdapter = new BungeeSchedulerAdapter(this);
        this.plugin = new BridgerBungeePlugin(this);
    }

    @Override
    public void onEnable() {
        this.plugin.enable();
    }

    @Override
    public void onDisable() {
        this.plugin.disable();
    }

    public PluginLogger getPluginLogger() {
        return this.logger;
    }

    public SchedulerAdapter getSchedulerAdapter() {
        return this.schedulerAdapter;
    }

    public PlatformType getPlatformType() {
        return PlatformType.BUNGEECORD;
    }

    public Path getDataDirectory() {
        return super.getDataFolder().toPath();
    }

    public InputStream getResourceStream(String path) {
        return getClass().getResourceAsStream(path);
    }
}
