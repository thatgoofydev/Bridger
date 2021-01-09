package be.goofydev.bridger.common.model.proxy;

import be.goofydev.bridger.api.model.proxy.Proxy;
import be.goofydev.bridger.api.model.user.User;
import be.goofydev.bridger.common.redis.RedisConstants;
import be.goofydev.bridger.common.redis.RedisDataManager;
import be.goofydev.bridger.common.redis.RedisManager;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.serializer.gson.GsonComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;
import java.util.UUID;

public class BridgedProxy implements Proxy {

    private final String id;
    private final RedisDataManager redisDataManager;
    private final RedisManager redisManager;

    private Set<User> users;

    public BridgedProxy(String id, RedisDataManager redisDataManager, RedisManager redisManager) {
        this.id = id;
        this.redisDataManager = redisDataManager;
        this.redisManager = redisManager;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Set<User> getUsers() {
        if(this.users == null) {
            this.users = this.redisDataManager.getUsersOfProxy(this.id);
        }
        return this.users;
    }

    @Override
    public User getUser(UUID uniqueId) {
        if (!hasUser(uniqueId)) {
            return null;
        }
        return this.getUsers().stream().filter(player -> player.getUniqueId() == uniqueId).findFirst().orElse(null);
    }

    @Override
    public boolean hasUser(UUID uniqueId) {
        if(this.users == null) {
            return this.redisDataManager.isUserConnectedToProxy(this.id, uniqueId);
        }
        return this.users.stream().allMatch(player -> player.getUniqueId().equals(uniqueId));
    }

    @Override
    public void broadcastMessage(@NonNull Component message) {
        this.redisDataManager.broadcast(getId(), message);
    }

    @Override
    public void executeCommand(@NonNull String... command) {
        this.redisDataManager.executeCommand(getId(), command);
    }
}
