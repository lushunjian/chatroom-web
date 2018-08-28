package cn.lsj.redis.service;

import redis.clients.jedis.Jedis;

public interface RedisAction<T> {
     T doRedisAction(Jedis redis);
}
