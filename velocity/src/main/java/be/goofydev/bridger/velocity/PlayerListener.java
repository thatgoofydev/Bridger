package be.goofydev.bridger.velocity;

import be.goofydev.bridger.api.event.user.UserServerChangeEvent;
import be.goofydev.bridger.api.event.user.UserJoinEvent;
import be.goofydev.bridger.api.event.user.UserLeaveEvent;
import be.goofydev.bridger.api.model.user.User;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;

public class PlayerListener {

    private final BridgerVelocityPlugin plugin;

    public PlayerListener(BridgerVelocityPlugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onUserLogin(LoginEvent event) {
        if(!event.getResult().isAllowed()) return;

        User user = this.plugin.getUserProvider().add(event.getPlayer());
        this.plugin.getRedisDataManager().addUser(user);
        this.plugin.getEventBus().post(new UserJoinEvent(user));
    }

    @Subscribe(order = PostOrder.LAST)
    public void onUserDisconnect(DisconnectEvent event) {
        User user = this.plugin.getUserProvider().remove(event.getPlayer().getUniqueId());
        this.plugin.getRedisDataManager().removeUser(user);
        this.plugin.getEventBus().post(new UserLeaveEvent(user));
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onServerPreConnectEvent(ServerPreConnectEvent event) {
        User user = this.plugin.getUserProvider().get(event.getPlayer().getUniqueId());
        String from = user.getServer();
        String to = event.getResult().getServer().isPresent() ? event.getResult().getServer().get().getServerInfo().getName() : null;

        this.plugin.getRedisDataManager().changeUserServer(user, to);
        this.plugin.getEventBus().post(new UserServerChangeEvent(user, from, to));
    }

    @Subscribe
    public void onPing(ProxyPingEvent event) {
        if (!this.plugin.getConfiguration().getProxyConfiguration().shouldOverridePlayerCount()) {
            return;
        }
        event.setPing(event.getPing().asBuilder().onlinePlayers(this.plugin.getTotalUserCount()).build());
    }

}
