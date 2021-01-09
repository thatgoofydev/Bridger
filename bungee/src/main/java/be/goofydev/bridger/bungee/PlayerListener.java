package be.goofydev.bridger.bungee;

import be.goofydev.bridger.api.event.user.UserServerChangeEvent;
import be.goofydev.bridger.api.event.user.UserJoinEvent;
import be.goofydev.bridger.api.event.user.UserLeaveEvent;
import be.goofydev.bridger.api.model.user.User;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener {
    private final BridgerBungeePlugin plugin;

    public PlayerListener(BridgerBungeePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        User user = this.plugin.getUserProvider().add(event.getPlayer());
        this.plugin.getRedisDataManager().addUser(user);
        this.plugin.getEventBus().post(new UserJoinEvent(user));
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        User user = this.plugin.getUserProvider().remove(event.getPlayer().getUniqueId());
        this.plugin.getRedisDataManager().removeUser(user);
        this.plugin.getEventBus().post(new UserLeaveEvent(user));
    }

    @EventHandler
    public void onServerChange(ServerConnectEvent event) {
        User user = this.plugin.getUserProvider().get(event.getPlayer().getUniqueId());
        String from = user.getServer();
        String to = event.getTarget().getName();

        this.plugin.getRedisDataManager().changeUserServer(user, to);
        this.plugin.getEventBus().post(new UserServerChangeEvent(user, from, to));
    }

    @EventHandler(priority = 3)
    public void onPing(ProxyPingEvent event) {
        if (!this.plugin.getConfiguration().getProxyConfiguration().shouldOverridePlayerCount()) {
            return;
        }
        event.getResponse().getPlayers().setOnline(this.plugin.getTotalUserCount());
    }
}
