package com.vivo.cache.redis.config.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;

/**
 * 类描述：jedis配置
 *
 * @author 汤旗
 * @date 2019-05-10 16:11
 */
public class JedisConfig {
    public static Jedis getJedis() {
        return new Jedis("10.101.17.51");
    }
    public static void close(Jedis jedis) {
        if (jedis != null && jedis.isConnected()) {
            jedis.close();
        }
    }
    public static Jedis getSentinelJedis(){
        String masterName = "mymaster";
        Set<String> sentinelSet = new HashSet<>();
        sentinelSet.add("10.101.17.51:26379");
        sentinelSet.add("10.101.17.51:26380");
        sentinelSet.add("10.101.17.51:26381");
        JedisSentinelPool jedisSentinelPool = new JedisSentinelPool(masterName,sentinelSet);
        return jedisSentinelPool.getResource();
    }
}
