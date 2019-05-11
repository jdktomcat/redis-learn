package com.vivo.cache.redis.config.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * 类描述：测试类
 *
 * @author 汤旗
 * @date 2019-05-10 16:18
 */
public class JedisRunner {

    public static void main(String[] args) {
//        Jedis jedis = JedisConfig.getInstance();
//        String keyName = "hyper-log-log-key";
//        Pipeline pipeline = jedis.pipelined();
//        long start = System.currentTimeMillis();
//        for (int i = 0; i < 1000000; i++) {
//            pipeline.pfadd(keyName, i + "");
//        }
//        pipeline.sync();
//        long end = System.currentTimeMillis();
//        System.out.println("pfcount:" + jedis.pfcount(keyName));
//        System.out.println("执行了" + (end - start) + "ms.");
    }
}
