package com.opennetworksmc.bridger.common.config;

import com.moandjiezana.toml.Toml;
import com.opennetworksmc.bridger.common.plugin.BridgerPlugin;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public class Configuration {

    private final ProxyConfiguration proxyConfiguration;
    private final RedisConfiguration redisConfiguration;

    private Configuration(Toml toml) {
        this.proxyConfiguration = new ProxyConfiguration(toml.getTable("proxy"));
        this.redisConfiguration = new RedisConfiguration(toml.getTable("redis"));
    }

    public ProxyConfiguration getProxyConfiguration() {
        return proxyConfiguration;
    }

    public RedisConfiguration getRedisConfiguration() {
        return redisConfiguration;
    }

    public static Configuration load(BridgerPlugin plugin) {
        try {
            File folder = plugin.getBootstrap().getDataDirectory().toFile();
            File file = new File(folder, "config.toml");
            if(!folder.exists()) {
                folder.mkdirs();
            }

            if(!file.exists()) {
                InputStream is = plugin.getClass().getResourceAsStream("/" + file.getName());
                if (is != null) {
                    Files.copy(is, file.toPath());
                } else {
                    file.createNewFile();
                }
            }

            return  new Configuration(new Toml().read(file));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load configuration");
        }
    }

}
