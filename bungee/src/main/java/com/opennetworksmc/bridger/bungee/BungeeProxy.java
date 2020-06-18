package com.opennetworksmc.bridger.bungee;

import com.opennetworksmc.bridger.api.model.proxy.Proxy;
import com.opennetworksmc.bridger.api.model.user.User;
import com.opennetworksmc.bridger.common.plugin.BridgerPlugin;
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
    public void broadcastMessage(Component component) {
        this.proxyServer.broadcast(TextAdapter.toBungeeCord(component));
    }

    @Override
    public void executeCommand(String... command) {
        this.proxyServer.getPluginManager().dispatchCommand(BridgerCommandSender.instance, String.join(" ", command));
    }
}
