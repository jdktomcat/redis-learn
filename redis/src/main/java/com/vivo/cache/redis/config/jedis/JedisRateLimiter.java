package com.vivo.cache.redis.config.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

/**
 * 类描述：限流器
 *
 * @author 汤旗
 * @date 2019-05-10 17:29
 */
public class JedisRateLimiter {

    /**
     * 限流操作
     *
     * @param userId    用户标识
     * @param actionKey 操作
     * @param period    时间
     * @param maxCount  操作数
     * @return 是否允许
     */
    public static boolean isActionAllowed(String userId, String actionKey, int period, int maxCount, Jedis jedis) {
        String key = String.format("rate:limiter:%s:%s", userId, actionKey);
        SetParams setParams = new SetParams();
        setParams.ex(period).nx();
        jedis.set(key, "0", setParams);
        return !(jedis.incr(key) > maxCount);

    }

    public static void main(String[] args) throws InterruptedException {
        Runnable runnable1 = () -> {
            Jedis jedis = JedisConfig.getJedis();
            for (int i = 0; i < 200; i++) {
                System.out.println("线程1第" + i + "次操作结果:" + isActionAllowed("tq", "reply", 60, 25, jedis));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            JedisConfig.close(jedis);
        };
        new Thread(runnable1).start();

        Runnable runnable2 = () -> {
            Jedis jedis = JedisConfig.getJedis();
            for (int i = 0; i < 2000; i++) {
                System.out.println("线程2第" + i + "次操作结果:" + isActionAllowed("tq", "reply", 60, 25, jedis));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            JedisConfig.close(jedis);
        };
        new Thread(runnable2).start();
    }
}
