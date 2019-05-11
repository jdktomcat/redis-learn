package com.vivo.cache.redis.config.redisson;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * Redisson配置
 *
 * @author 汤旗
 * @date 2019-05-11
 */
public class RedissonConfig {

    private Config config;

    private static RedissonConfig ourInstance = new RedissonConfig();

    public static RedissonConfig getInstance() {
        return ourInstance;
    }

    private RedissonConfig() {
        config = new Config();
        config.useSingleServer().setAddress("10.101.17.51:6379").setConnectionPoolSize(10);
    }

    public RedissonClient getClient() {
        return Redisson.create(config);
    }
}
