package be.goofydev.bridger.velocity;

import be.goofydev.bridger.api.platform.UserProvider;
import be.goofydev.bridger.common.plugin.BridgerPlugin;
import com.velocitypowered.api.proxy.Player;

import java.util.*;

public class VelocityUserProvider implements UserProvider<Player, VelocityUser> {

    private final Map<UUID, VelocityUser> users;
    private final BridgerPlugin plugin;

    public VelocityUserProvider(BridgerPlugin plugin) {
        this.plugin = plugin;
        this.users = new HashMap<>();
    }

    @Override
    public VelocityUser get(UUID uniqueId) {
        return this.users.get(uniqueId);
    }

    @Override
    public boolean has(UUID uniqueId) {
        return this.users.containsKey(uniqueId);
    }

    @Override
    public VelocityUser add(Player player) {
        if(has(player.getUniqueId())) {
            return get(player.getUniqueId());
        }
        VelocityUser user = new VelocityUser(this.plugin, player);
        this.users.put(player.getUniqueId(), user);
        return user;
    }

    @Override
    public VelocityUser remove(UUID uniqueId) {
        return this.users.remove(uniqueId);
    }

    @Override
    public Set<VelocityUser> getAll() {
        return new HashSet<>(this.users.values());
    }
}
