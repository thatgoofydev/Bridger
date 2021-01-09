package be.goofydev.bridger.bungee;

import be.goofydev.bridger.api.BridgerApi;
import be.goofydev.bridger.api.event.user.UserJoinEvent;
import be.goofydev.bridger.api.platform.PlatformType;
import be.goofydev.bridger.common.plugin.BridgerPlugin;
import be.goofydev.bridger.common.plugin.bootstrap.BridgerBootstrap;
import be.goofydev.bridger.common.plugin.logging.JavaLoggerWrapper;
import be.goofydev.bridger.common.plugin.logging.PluginLogger;
import be.goofydev.bridger.common.plugin.scheduler.SchedulerAdapter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

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

        this.getProxy().getPluginManager().registerCommand(this, new DebugCommand(this.plugin));
        this.plugin.getEventBus().subscribe(UserJoinEvent.class, (event) -> {
            this.getProxy().broadcast(new TextComponent(ChatColor.LIGHT_PURPLE + "Event listener triggered!"));
        });
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

    private static class DebugCommand extends Command { // TODO: remove

        private final BridgerPlugin plugin;

        public DebugCommand(BridgerPlugin plugin) {
            super("debug");
            this.plugin = plugin;
        }

        @Override
        public void execute(CommandSender commandSender, String[] args) {

            if (args.length == 0) {
                commandSender.sendMessage(new TextComponent(ChatColor.GOLD + "=== Debug Info ==="));

                BridgerApi api = this.plugin.getApi();
                commandSender.sendMessage(new TextComponent(ChatColor.GRAY + "Total user count: " + ChatColor.GOLD + api.getTotalUserCount()));

                Set<String> proxiesColored = api.getProxyManager().getActiveProxies().stream().map(proxyId -> (api.getProxyManager().getCurrentProxy().getId().equals(proxyId) ? ChatColor.GOLD : ChatColor.GRAY ) + proxyId).collect(Collectors.toSet());
                commandSender.sendMessage(new TextComponent(ChatColor.GRAY + "Proxies: " + String.join(ChatColor.GRAY + ", ", proxiesColored)));
            } else if(args.length == 1 && args[0].equals("event")) {
                this.plugin.getEventBus().post(new UserJoinEvent(null));
            }
        }
    }
}
