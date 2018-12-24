package com.zhuxiaobing;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Date;

public class Test_Jedis {

    private static final String IP = "192.168.241.129";
    private static final Integer PORT = 6379;

    public static void main(String[] args) {
        test02();
    }

    /**
     * 最普通的方式，jedis是非线程安全的。
     */
    private static void test01() {
        Jedis jedis = new Jedis(Test_Jedis.IP, Test_Jedis.PORT);
        jedis.set("key001", "hello Jedis");
        System.out.println(jedis.get("key001"));
        jedis.expire("key001", 5);
        jedis.close();
    }

    /**
     * 采用jedis线程池（JedisPool）， JedisPool是一个线程安全的线程池。
     */
    private static void test02() {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(5);
        jedisPoolConfig.setMaxWaitMillis(2000000);

        jedisPoolConfig.setTestWhileIdle(true); // 接池在空闲的时候校验连接池中链接的有效性。
        jedisPoolConfig.setTestOnBorrow(true);  // 每次从连接池中借一个链接的时候，要校验链接的可用性。
        jedisPoolConfig.setTestOnCreate(true);  // 每次创建一个链接放入到连接池的时候，需要校验该链接的有效性。

        final JedisPool jedisPool = new JedisPool(jedisPoolConfig, Test_Jedis.IP, Test_Jedis.PORT);

        Thread[] ts = new Thread[10];
        for (int i = 0; i < 10; i++) { // 开启10个线程。
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Jedis jedis = null;
                    try {
                        jedis = jedisPool.getResource();
                        Long time = new Date().getTime();
                        String key = Thread.currentThread().getName() + "_" + time;
                        jedis.set(key, time.toString());
                        jedis.expire(key, 20);
                        System.out.println(key);
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (jedis != null) {
                            jedis.close();
                        }
                    }
                }
            });
            t.start();
            ts[i] = t;
        }
        for (Thread t : ts) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        jedisPool.close();
    }
}
