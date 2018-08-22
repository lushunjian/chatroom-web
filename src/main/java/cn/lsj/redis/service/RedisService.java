package cn.lsj.redis.service;

import cn.lsj.redis.util.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;


/**
 * @Auther: Lushunjian
 * @Date: 2018/8/21 22:57
 * @Description: 支持对象存储，使用execute()方法，redis保存的数据会在内存和硬盘上存储，需要做序列化。
 */
@Service("redisService")
public class RedisService {

    @Resource
    private RedisTemplate<String, ?> redisTemplate;

    private final Jedis jedis;

    @Autowired
    public RedisService(Jedis jedis) {
        this.jedis = jedis;
    }

    /**
     * 添加
     * */
    public boolean set(final String key, final String value) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                connection.set(serializer.serialize(key), serializer.serialize(value));
                return true;
            }
        });
        return result;
    }

    /**
     * 根据key查询
     * */
    public String get(final String key) {
        String result = redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] value = connection.get(serializer.serialize(key));
                return serializer.deserialize(value);
            }
        });
        return result;
    }

    /**
     * 设置过期时间
     * */
    public boolean expire(final String key, long expire) {
        return redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    /**
     * 移除元素
     * */
    public boolean remove(final String key) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                connection.del(key.getBytes());
                return true;
            }
        });
        return result;
    }

    /**set Object*/
    public String setObject(String key,Object object)
    {
        return jedis.set(key.getBytes(), SerializeUtil.objectSerialize(object));
    }

    /**get Object*/
    public Object getObject(String key)
    {
        byte[] value = jedis.get(key.getBytes());
        return SerializeUtil.objectDeSerialize(value);
    }

    /**delete a key**/
    public boolean delObject(String key)
    {
        return jedis.del(key.getBytes())>0;
    }
}
