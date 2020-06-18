package com.opennetworksmc.bridger.bungee;

import com.opennetworksmc.bridger.api.model.proxy.Proxy;
import com.opennetworksmc.bridger.api.model.user.User;
import com.opennetworksmc.bridger.common.plugin.BridgerPlugin;
import net.kyori.text.Component;
import net.kyori.text.adapter.bungeecord.TextAdapter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

import java.util.UUID;

public class BungeeUser implements User {

    private final BridgerPlugin plugin;
    private final ProxiedPlayer player;

    public BungeeUser(BridgerPlugin plugin, ProxiedPlayer player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public UUID getUniqueId() {
        return this.player.getUniqueId();
    }

    @Override
    public String getUsername() {
        return this.player.getName();
    }

    @Override
    public String getIp() {
        return player.getSocketAddress().toString();
    }

    @Override
    public Proxy getProxy() {
        return this.plugin.getCurrentProxy();
    }

    @Override
    public String getServer() {
        Server server = this.player.getServer();
        if(server == null) {
            return null;
        }
        return server.getInfo().getName();
    }

    @Override
    public void sendMessage(Component component) {
        this.player.sendMessage(TextAdapter.toBungeeCord(component));
    }
}
