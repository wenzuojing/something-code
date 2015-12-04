package com.github.wens.code.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


    public static ShardedJedisPool createSharedJedisPool(GenericObjectPoolConfig config, String redisInfos, String redisPassword) {
        if(config == null ){
            config = new JedisPoolConfig();
        }
        String[] redisAddressInfos = redisInfos.split("\",| |(\\r)?\\n\"");
        List<JedisShardInfo> jdsInfoList = new ArrayList<JedisShardInfo>(redisAddressInfos.length);
        for (String address : redisAddressInfos) {
            String[] hostAndPort = address.trim().split(":");
            String host = hostAndPort[0];
            int port = 6379;
            if (hostAndPort.length == 2) {
                port = Integer.parseInt(hostAndPort[1]);
            }
            JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port, 2000);
            if (redisPassword != null && redisPassword.length() != 0) {
                jedisShardInfo.setPassword(redisPassword);
            }
            jdsInfoList.add(jedisShardInfo);
        }
        return new ShardedJedisPool(config, jdsInfoList);
    }


    public static ShardedJedis createSharedJedis( String redisInfos, String redisPassword) {
        String[] redisAddressInfos = redisInfos.split("\",| |(\\r)?\\n\"");
        List<JedisShardInfo> jdsInfoList = new ArrayList<JedisShardInfo>(redisAddressInfos.length);
        for (String address : redisAddressInfos) {
            String[] hostAndPort = address.trim().split(":");
            String host = hostAndPort[0];
            int port = 6379;
            if (hostAndPort.length == 2) {
                port = Integer.parseInt(hostAndPort[1]);
            }
            JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port, 2000);
            if (redisPassword != null && redisPassword.length() != 0) {
                jedisShardInfo.setPassword(redisPassword);
            }
            jdsInfoList.add(jedisShardInfo);
        }
        return new ShardedJedis(jdsInfoList);
    }


    public static void putMap(Jedis client, String key, Map<String, String> data) {
        Pipeline pipelined = client.pipelined();
        for (String column : data.keySet()) {
            pipelined.hset(key, column, data.get(column));
        }
        pipelined.sync();
    }


}
