package com.zhuxiaobing;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.*;

public class Test_Jedis {

    public static void main(String[] args) {
        JedisPool jedisPool = JedisPoolUtil.getJedisPoolInstance();
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            // 测试redis 事物
            testTransaction(jedis);
            // 测试redis 五种数据类型
            testCommon(jedis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JedisPoolUtil.release(jedisPool, jedis);
        }
    }

    public static void testTransaction(Jedis jedis) {
        int balance;
        int debt;
        int amt = 10;
        jedis.set("balance", "100");
        jedis.set("debt", "10");
        jedis.watch("balance");
        balance = Integer.parseInt(jedis.get("balance"));
        if (balance < amt) {
            jedis.unwatch();
            return;
        } else {
            Transaction transaction = jedis.multi();
            transaction.decrBy("balance", amt);
            transaction.incrBy("debt", amt);
            transaction.exec();
            balance = Integer.parseInt(jedis.get("balance"));
            debt = Integer.parseInt(jedis.get("debt"));
            System.out.println(balance);
            System.out.println(debt);
        }
    }

    public static void testCommon(Jedis jedis) {
        System.out.println("****************key*****************");
        Set<String> keys = jedis.keys("*");
        for (Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            System.out.println(key);
        }
        System.out.println(jedis.exists("k2"));
        System.out.println(jedis.ttl("k9"));

        System.out.println("****************String*****************");
        jedis.set("k10", "v10");
        jedis.mset("ak1", "av1", "ak2", "av2");
        System.out.println(jedis.mget("ak1", "ak2"));

        System.out.println("****************list*****************");
        jedis.lpush("mylist", "a", "b", "c");
        List<String> list = jedis.lrange("mylist", 0, -1);
        for (String element : list) {
            System.out.println(element);
        }

        System.out.println("****************hash*****************");
        jedis.hset("hash1", "username", "zhuxiaobing");
        System.out.println(jedis.hget("hash1", "username"));
        Map<String, String> map = new HashMap<String, String>();
        map.put("telphone", "13811814763");
        map.put("address", "atguigu");
        map.put("email", "abc@163.com");
        jedis.hmset("hash2", map);
        List<String> result = jedis.hmget("hash2", "telphone", "email");
        for (String element : result) {
            System.out.println(element);
        }

        System.out.println("****************set*****************");
        jedis.sadd("orders", "jd001");
        jedis.sadd("orders", "jd002");
        jedis.sadd("orders", "jd003");
        Set<String> set1 = jedis.smembers("orders");
        for (Iterator iterator = set1.iterator(); iterator.hasNext(); ) {
            String string = (String) iterator.next();
            System.out.println(string);
        }
        jedis.srem("orders", "jd002");
        System.out.println(jedis.smembers("orders").size());

        System.out.println("****************zset*****************");
        jedis.zadd("zset01", 60d, "v1");
        jedis.zadd("zset01", 70d, "v2");
        jedis.zadd("zset01", 80d, "v3");
        jedis.zadd("zset01", 90d, "v4");

        Set<String> s1 = jedis.zrange("zset01", 0, -1);
        for (Iterator iterator = s1.iterator(); iterator.hasNext(); ) {
            String string = (String) iterator.next();
            System.out.println(string);
        }
    }
}
