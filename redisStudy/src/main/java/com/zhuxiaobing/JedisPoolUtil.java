package com.zhuxiaobing;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolUtil {
    private static volatile JedisPool jedisPool = null;

    private JedisPoolUtil() {

    }

    public static JedisPool getJedisPoolInstance() {
        if (null == jedisPool) {
            synchronized (JedisPoolUtil.class) {
                JedisPoolConfig poolConfig = new JedisPoolConfig();
                poolConfig.setMaxIdle(32);
                poolConfig.setMaxWaitMillis(1000);
                poolConfig.setMaxTotal(100);
                poolConfig.setTestOnBorrow(true);
                jedisPool = new JedisPool(poolConfig, "192.168.241.131", 6381);
            }
        }
        return jedisPool;
    }

    public static void release(JedisPool jedisPool, Jedis jedis) {
        if (null != jedis) {
            jedisPool.returnResource(jedis);
        }
    }
}
