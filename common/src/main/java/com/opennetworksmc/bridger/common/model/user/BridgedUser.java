package com.opennetworksmc.bridger.common.model.user;

import com.opennetworksmc.bridger.api.model.proxy.Proxy;
import com.opennetworksmc.bridger.api.model.user.User;
import com.opennetworksmc.bridger.common.redis.RedisDataManager;
import net.kyori.text.Component;

import java.util.UUID;

public class BridgedUser implements User {

    private final UUID uniqueId;
    private final RedisDataManager redisDataManager;

    private String ip;
    private Proxy proxy;
    private String server;

    public BridgedUser(UUID uniqueId, RedisDataManager redisDataManager) {
        this.uniqueId = uniqueId;
        this.redisDataManager = redisDataManager;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public String getUsername() {
        return ""; // todo
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
    public void sendMessage(Component component) {
        // todo
    }
}
