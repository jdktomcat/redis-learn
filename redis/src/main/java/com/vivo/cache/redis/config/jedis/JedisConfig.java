package com.vivo.cache.redis.config.jedis;

import redis.clients.jedis.Jedis;

/**
 * 类描述：jedis配置
 *
 * @author 汤旗
 * @date 2019-05-10 16:11
 */
public class JedisConfig {

    private Jedis jedis;

    private static JedisConfig ourInstance = new JedisConfig();

    public static Jedis getInstance() {
        return ourInstance.jedis;
    }

    private JedisConfig() {
        jedis = new Jedis("10.101.25.169");
    }
}
