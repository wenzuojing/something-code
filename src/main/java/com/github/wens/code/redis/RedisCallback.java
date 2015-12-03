package com.github.wens.code.redis;

import redis.clients.jedis.Jedis;

/**
 * Created by wens on 15-12-3.
 */
public interface RedisCallback<T> {
    public T handle(Jedis jedis);
}
