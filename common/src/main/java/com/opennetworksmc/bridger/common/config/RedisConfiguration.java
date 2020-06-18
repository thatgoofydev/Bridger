package com.opennetworksmc.bridger.common.config;

import com.moandjiezana.toml.Toml;
import io.lettuce.core.RedisURI;

import java.util.ArrayList;
import java.util.List;

public class RedisConfiguration {
    private final List<RedisURI> redisURIs;

    protected RedisConfiguration(Toml redisTable) {
        this.redisURIs = new ArrayList<>();

        List<Toml> connections = redisTable.getTables("connection");
        for(Toml connectionToml : connections) {
            String host = connectionToml.getString("host");
            int port = connectionToml.getLong("port", 6379L).intValue();
            String password = connectionToml.getString("password", null);

            if(host != null && !host.isEmpty()) {
                RedisURI.Builder builder = RedisURI.builder()
                        .withHost(host)
                        .withPort(port);

                if(password != null) {
                    builder = builder.withPassword(password);
                }

                this.redisURIs.add(builder.build());
            }
        }
    }

    public List<RedisURI> getRedisURIs() {
        return redisURIs;
    }
}
