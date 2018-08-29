package cn.lsj.redis.service;

import cn.lsj.redis.connect.RedisConnect;
import cn.lsj.redis.util.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class RedisHandler{

    @Autowired
    private RedisConnect<Object> redisConnect;

    /** 根据Key，存储对象 */
    public void putObject(String key,Object object){
        redisConnect.doRedis(new RedisAction<Object>() {
            @Override
            public Object doRedisAction(Jedis redis) {
                return redis.set(key.getBytes(), SerializeUtil.objectSerialize(object));
            }
        });
    }

    /** 根据Key，查询对象 */
    public Object getObject(String key){
        return redisConnect.doRedis(new RedisAction<Object>() {
            @Override
            public Object doRedisAction(Jedis redis) {
                byte[] value = redis.get(key.getBytes());
                return SerializeUtil.objectDeSerialize(value);
            }
        });
    }

    /** 根据key，删除对象*/
    public boolean deleteObject(String key){
        return
           (boolean)redisConnect.doRedis(new RedisAction<Object>() {
            @Override
            public Object doRedisAction(Jedis redis) {
               return redis.del(key.getBytes())>0;
            }
        });
    }

    /** 清空数据*/
    public String flushDB(){
        return
            (String) redisConnect.doRedis(new RedisAction<Object>() {
                @Override
                public Object doRedisAction(Jedis redis) {
                    return redis.flushDB();
                }
            });
    }

    /** 检查是否连接成功*/
    public String ping(){
        return
            (String) redisConnect.doRedis(new RedisAction<Object>() {
                @Override
                public Object doRedisAction(Jedis redis) {
                    return redis.ping();
                }
            });
    }

    /** 设置过期时间*/
    public void expire(String key, int seconds){
          redisConnect.doRedis(new RedisAction<Object>() {
                @Override
                public Object doRedisAction(Jedis redis) {
                    return redis.expire(key,seconds);
                }
            });
    }
    /** 判断key是否存在值*/
    public boolean exists(String key){
       return  (boolean)redisConnect.doRedis(new RedisAction<Object>() {
            @Override
            public Object doRedisAction(Jedis redis) {
                return redis.exists(key);
            }
        });
    }


}
