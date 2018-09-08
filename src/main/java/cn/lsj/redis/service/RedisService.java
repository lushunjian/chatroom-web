package cn.lsj.redis.service;

import cn.lsj.redis.connect.RedisConnect;
import cn.lsj.util.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * @Auther: Lushunjian
 * @Date: 2018/8/21 22:57
 * @Description: 支持对象存储，使用execute()方法，redis保存的数据会在内存和硬盘上存储，需要做序列化。
 * 不支持连接释放，废弃
 */
@Service("redisService")
public class RedisService {

    @Resource
    private RedisTemplate<String, ?> redisTemplate;

    @Autowired
    private RedisConnect redisConnect;


    /**
     * 添加
     * */
    public boolean put(final String key, final String value) {
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

    /**
     * 对象存储
     * */
    /**set Object*/
    public boolean putObject(final String key, final Object object) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                connection.set(serializer.serialize(key), SerializeUtil.objectSerialize(object));
                return true;
            }
        });
    }

    /**get Object*/
    public Object getObject(final String key) {
        String result = redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] value = connection.get(serializer.serialize(key));
                return serializer.deserialize(value);
            }
        });
        return SerializeUtil.objectDeSerialize(result.getBytes());
    }

    /**
     * 对象存储
     * */
    /**set Object*/
    public void jedisPutObject(String key,Object object){
        Jedis jedis = redisConnect.getJedis();
        jedis.set(key.getBytes(), SerializeUtil.objectSerialize(object));
    }


    /**get Object*/
    public Object jedisGetObject(String key){
        Jedis jedis = redisConnect.getJedis();
        byte[] value = jedis.get(key.getBytes());
        return SerializeUtil.objectDeSerialize(value);
    }

    /**delete a key*/
    public boolean jedisDelObject(String key){
        Jedis jedis = redisConnect.getJedis();
        return jedis.del(key.getBytes())>0;
    }

    /**
     * 清空redis 所有数据
     */
    public String flushDB(){
        Jedis jedis = redisConnect.getJedis();
        return jedis.flushDB();
    }

    /**
     * 检查是否连接成功
     */
    public String ping(){
        Jedis jedis = redisConnect.getJedis();
        return jedis.ping();
    }

    /**
     * 通过正则匹配keys
     */
    public Set<String> keys(String pattern){
        Jedis jedis = redisConnect.getJedis();
        return jedis.keys(pattern);
    }

    /**
     * 检查key是否已经存在\
     */
    public boolean exists(String key) {
        Jedis jedis = redisConnect.getJedis();
        return jedis.exists(key);
    }


        /**
         * 将redis连接，返还到连接池
         * @param jedis
         */
    public static void returnResource(Jedis jedis) {
        if (jedis != null && jedis.isConnected()) {
            jedis.close();
        }
    }

    public void putTest(String key, Map<String,String> map){
        Jedis jedis = redisConnect.getJedis();
        jedis.hmset(key, map);
    }

    public String getTest(String key, String mapKey){
        Jedis jedis = redisConnect.getJedis();
        return jedis.hget(key, mapKey);
    }


}
