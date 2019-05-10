package com.vivo.cache.redis.config.redisson;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedissonConfig {

    private Config config;

    private static RedissonConfig ourInstance = new RedissonConfig();

    public static RedissonConfig getInstance() {
        return ourInstance;
    }

    private RedissonConfig() {
        config = new Config();
        config.useSingleServer().setAddress("redis://10.101.16.46:6379");
    }

    public RedissonClient getClient() {
        return Redisson.create(config);
    }
}
