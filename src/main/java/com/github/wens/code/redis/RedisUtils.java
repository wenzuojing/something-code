package com.github.wens.code.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

/**
 * Created by wens on 15-12-3.
 */
public class RedisUtils {

    public <T> T  execute(JedisPool jedisPool , RedisCallback<T> callback){
        Jedis jedis = jedisPool.getResource();
        boolean broken = false;
        try {
            return callback.handle( jedis);
        } catch (JedisException e) {
            broken = true;
            throw  e ;
        } catch (Exception e) {
            throw  e ;
        } finally {
            returnResource( jedisPool , jedis, broken);
        }

    }

    private void returnResource( JedisPool jedisPool ,  Jedis jedis, boolean broken) {
        if (jedis != null) {
            if (broken) {
                jedisPool.returnBrokenResource(jedis);
            } else {
                jedisPool.returnResource(jedis);
            }
        }
    }


}
