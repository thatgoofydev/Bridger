package be.goofydev.bridger.bungee;

import be.goofydev.bridger.api.model.proxy.Proxy;
import be.goofydev.bridger.api.model.user.User;
import be.goofydev.bridger.common.plugin.BridgerPlugin;
import net.kyori.text.Component;
import net.kyori.text.adapter.bungeecord.TextAdapter;
import net.md_5.bungee.api.ProxyServer;

import java.util.Set;
import java.util.UUID;

public class BungeeProxy implements Proxy {

    private final BridgerPlugin plugin;
    private final ProxyServer proxyServer;

    public BungeeProxy(BridgerPlugin plugin, ProxyServer proxyServer) {
        this.plugin = plugin;
        this.proxyServer = proxyServer;
    }

    @Override
    public String getId() {
        return this.plugin.getConfiguration().getProxyConfiguration().getProxyId();
    }

    @Override
    public Set<User> getUsers() {
        return this.plugin.getUserProvider().getAll();
    }

    @Override
    public User getUser(UUID uniqueId) {
        return this.plugin.getUserProvider().get(uniqueId);
    }

    @Override
    public boolean hasUser(UUID uniqueId) {
        return this.plugin.getUserProvider().has(uniqueId);
    }

    @Override
    public void broadcastMessage(Component message) {
        this.proxyServer.broadcast(TextAdapter.toBungeeCord(message));
    }

    @Override
    public void executeCommand(String... command) {
        String fullCommand = String.join(" ", command);
        if (fullCommand.startsWith("/")) {
            fullCommand = fullCommand.substring(1);
        }

        this.proxyServer.getPluginManager().dispatchCommand(BridgerCommandSender.instance, fullCommand);
    }
}
