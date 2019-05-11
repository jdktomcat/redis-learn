package com.vivo.cache.redis.config.redisson;

import org.redisson.Redisson;
import org.redisson.api.RBitSet;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;

/**
 * 类描述：测试类
 *
 * @author 汤旗
 * @date 2019-05-08 16:19
 */
public class RedissonRunner {

    public static void main(String[] args) throws InterruptedException {
        RedissonClient client = RedissonConfig.getInstance().getClient();
        RBitSet rBitSet = client.getBitSet("bit-set-key");
        for (int i = 0; i < 10; i++) {
            rBitSet.set(i, i % 2);
        }

        RSemaphore semaphore = client.getSemaphore("semaphore-key");
        semaphore.acquire();
        client.shutdown();
        System.out.println("done!!!");
    }
}
