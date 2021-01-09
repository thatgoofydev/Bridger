package be.goofydev.bridger.common.model.user;

import be.goofydev.bridger.api.model.proxy.Proxy;
import be.goofydev.bridger.api.model.user.User;
import be.goofydev.bridger.common.redis.RedisConstants;
import be.goofydev.bridger.common.redis.RedisDataManager;
import be.goofydev.bridger.common.redis.RedisManager;
import net.kyori.text.Component;
import net.kyori.text.TextComponent;
import net.kyori.text.serializer.gson.GsonComponentSerializer;

import java.util.UUID;

public class BridgedUser implements User {

    private final UUID uniqueId;
    private final RedisDataManager redisDataManager;
    private final RedisManager redisManager;

    private String username;
    private String ip;
    private Proxy proxy;
    private String server;

    public BridgedUser(UUID uniqueId, RedisDataManager redisDataManager, RedisManager redisManager) {
        this.uniqueId = uniqueId;
        this.redisDataManager = redisDataManager;
        this.redisManager = redisManager;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public String getUsername() {
        if (this.username == null) {
            this.username = this.redisDataManager.getPlayerUsername(this.uniqueId);
        }
        return this.username;
    }

    @Override
    public String getIp() {
        if (this.ip == null) {
            this.ip = this.redisDataManager.getPlayerIp(this.uniqueId);
        }
        return this.ip;
    }

    @Override
    public Proxy getProxy() {
        if(this.proxy == null) {
            this.proxy = this.redisDataManager.getPlayerProxy(this.uniqueId);
        }
        return this.proxy;
    }

    @Override
    public String getServer() {
        if (this.server == null) {
            this.server = this.redisDataManager.getPlayerServer(this.uniqueId);
        }
        return this.server;
    }

    @Override
    public void sendMessage(Component message) {
        this.redisDataManager.messageUser(getUniqueId(), message);
    }
}
