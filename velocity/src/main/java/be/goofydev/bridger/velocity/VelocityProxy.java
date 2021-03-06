package be.goofydev.bridger.velocity;

import be.goofydev.bridger.api.model.proxy.Proxy;
import be.goofydev.bridger.api.model.user.User;
import be.goofydev.bridger.common.plugin.BridgerPlugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.text.Component;

import java.util.Set;
import java.util.UUID;

public class VelocityProxy implements Proxy {

    private final BridgerPlugin plugin;
    private final ProxyServer proxyServer;

    public VelocityProxy(BridgerPlugin plugin, ProxyServer proxyServer) {
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
        this.proxyServer.broadcast(message);
    }

    @Override
    public void executeCommand(String... command) {
        this.proxyServer.getCommandManager().execute(BridgerCommandSource.instance, String.join(" ", command));
    }
}
